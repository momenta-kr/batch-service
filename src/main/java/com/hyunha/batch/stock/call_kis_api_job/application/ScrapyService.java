package com.hyunha.batch.stock.call_kis_api_job.application;

import com.hyunha.batch.stock.call_kis_api_job.domain.ScrapydJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
public class ScrapyService {

    private final NewsDedupService newsDedupService;
    private final RestClient scrapyRestClient;


    public void callScrapy(List<ScrapydJob> dedupedAllJobs) {
        List<String> allUrls = dedupedAllJobs.stream()
                .map(ScrapydJob::url)
                .distinct()
                .toList();

        Set<String> existingUrls = newsDedupService.findExistingUrls(allUrls);

        // 3) 스케줄링
        int scheduled = 0;
        int skippedExisting = 0;

        for (ScrapydJob job : dedupedAllJobs) {
            if (existingUrls.contains(job.url())) {
                skippedExisting++;
                continue;
            }
            try {
                MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
                form.add("project", "momenta");
                form.add("spider", job.spiderName());
                form.add("url", job.url());
                form.add("query", job.query());
                form.add("symbol", job.symbol());
                form.add("description", job.description());
                form.add("published_at", job.publishedAt());

                String resp = scrapyRestClient.post()
                        .uri("/schedule.json")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .body(form)
                        .retrieve()
                        .body(String.class);

                log.debug("scrapyd scheduled. spider={}, url={}, resp={}", job.spiderName(), job.url(), resp);
                scheduled++;
            } catch (Exception e) {
                log.warn("scrapyd schedule failed. job={}, err={}", job, e.toString());
            }
        }

        log.info("done. urls={}, existing={}, scheduled={}, skippedExisting={}",
                allUrls.size(), existingUrls.size(), scheduled, skippedExisting);

    }
}
