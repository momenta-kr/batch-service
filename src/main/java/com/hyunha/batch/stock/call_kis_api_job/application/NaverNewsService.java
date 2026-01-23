package com.hyunha.batch.stock.call_kis_api_job.application;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyunha.batch.stock.call_kis_api_job.domain.ScrapydJob;
import com.hyunha.batch.stock.call_kis_api_job.infra.jpa.entity.Stock;
import com.hyunha.batch.stock.call_kis_api_job.infra.jpa.entity.StockMasterId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class NaverNewsService {

    @Value("${external.api.naver.client_id}")
    private String naverClientId;

    @Value("${external.api.naver.client_secret}")
    private String naverClientSecret;

    private final ObjectMapper objectMapper;
    private final RestClient naverNewsRestClient;


    // ✅ 도메인 -> spiderName 매핑 (if-else 제거)
    private static final Map<String, String> DOMAIN_TO_SPIDER = Map.ofEntries(
            Map.entry("mt.co.kr", "mt"),
            Map.entry("mk.co.kr", "mk"),
            Map.entry("asiae.co.kr", "asiae"),
            Map.entry("biz.heraldcorp.com", "bizheraldcorp"),
            Map.entry("bizwatch.co.kr", "bizwatch"),
            Map.entry("edaily.co.kr", "edaily"),
            Map.entry("fnnews.com", "fnnews"),
            Map.entry("hankyung.com", "hankyung"),
            Map.entry("joseilbo.com", "joseilbo"),
            Map.entry("sedaily.com", "sedaily")
    );


    public List<ScrapydJob> fetchNews(List<Stock> stocks) {

        // 1) 전체 종목의 ScrapydJob을 먼저 전부 수집
        List<ScrapydJob> allJobs = new ArrayList<>();
        List<String> keywords = List.of("주가", "급등");

        for (Stock stock : stocks) {
            for (String keyword : keywords) {
                String query = buildQuery(stock, keyword);

                try {
                    List<ScrapydJob> jobs = callNaverNews(query, stock.getSymbol());
                    allJobs.addAll(jobs);
                } catch (Exception e) {
                    log.warn("naver news failed. query={}, symbol={}, err={}",
                            query, stock.getSymbol(), e.toString());
                }
            }
        }

        // (옵션) tasklet 레벨에서 URL 중복 제거 (callNaverNews 내부에서도 제거하지만, 종목 간 중복까지 제거)
        return allJobs.stream()
                .collect(Collectors.toMap(ScrapydJob::url, j -> j, (a, b) -> a, LinkedHashMap::new))
                .values()
                .stream()
                .toList();
    }

    /**
     * ✅ 네이버 뉴스 호출 -> Scrapyd에 넘길 작업 목록으로 변환하여 리턴
     */
    private List<ScrapydJob> callNaverNews(String query, String symbol) throws Exception {
        String body = naverNewsRestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/search/news.json")
                        .queryParam("display", "100")
                        .queryParam("query", query)
                        .build())
                .headers(h -> {
                    h.set("X-Naver-Client-Id", naverClientId);
                    h.set("X-Naver-Client-Secret", naverClientSecret);
                })
                .retrieve()
                .body(String.class);

        JsonNode root = objectMapper.readTree(body);
        JsonNode items = root.get("items");
        if (items == null || !items.isArray()) return List.of();

        List<ScrapydJob> jobs = new ArrayList<>();
        for (JsonNode item : items) {
            // originallink가 비는 케이스가 있어서 link fallback
            String url = textOrEmpty(item, "originallink");
            if (url.isBlank()) url = textOrEmpty(item, "link");

            String description = textOrEmpty(item, "description");
            String pubDate = textOrEmpty(item, "pubDate");

            if (url.isBlank()) continue;

            // ✅ url 기반으로 spiderName 결정
            String spiderName = resolveSpiderName(url);
            if (spiderName == null) continue;

            jobs.add(new ScrapydJob(spiderName, url, query, symbol, description, pubDate));
        }

        // (옵션) 동일 URL 중복 제거
        return jobs.stream()
                .collect(Collectors.toMap(ScrapydJob::url, j -> j, (a, b) -> a, LinkedHashMap::new))
                .values()
                .stream()
                .toList();
    }


    private String resolveSpiderName(String url) {
        for (Map.Entry<String, String> e : DOMAIN_TO_SPIDER.entrySet()) {
            if (url.contains(e.getKey())) return e.getValue();
        }
        return null;
    }

    private String textOrEmpty(JsonNode node, String field) {
        JsonNode v = node.get(field);
        return (v == null || v.isNull()) ? "" : v.asText("");
    }




    /**
     * 종목별 쿼리 생성
     * 필요하면 "주가" 대신 "실적", "전망" 등 확장 가능
     */
    private String buildQuery(Stock stock, String keyword) {
        // name_ko가 null일 수 있으니 안전하게
        String nameKo = Optional.ofNullable(stock.getNameKo()).orElse("").trim();
        if (nameKo.isEmpty()) {
            // 심볼만 있을 때 fallback
            return Optional.ofNullable(stock.getId())
                    .map(StockMasterId::getSymbol)
                    .orElse(keyword);
        }
        return nameKo + " " + keyword;
    }

}
