package com.hyunha.batch.stock.call_kis_api_job.application;

import com.hyunha.batch.stock.call_kis_api_job.document.NewsDoc;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NewsDedupService {

    private final ElasticsearchOperations operations;

    /**
     * jobs의 url들을 ES(momenta-news.url keyword)에서 한 번에 찾아서
     * "이미 존재하는 url" Set으로 반환
     */
    public Set<String> findExistingUrls(Collection<String> urls) {
        List<String> distinct = urls.stream()
                .filter(u -> u != null && !u.isBlank())
                .distinct()
                .toList();

        if (distinct.isEmpty()) return Set.of();

        // ✅ terms query와 동일한 효과 (url IN [...])
        Criteria criteria = Criteria.where("url").in(distinct); // :contentReference[oaicite:2]{index=2}
        Query query = new CriteriaQuery(criteria);

        // 너무 크게 잡지 말기(보통 네이버 뉴스 100개라 충분)
        query.setPageable(PageRequest.of(0, Math.min(distinct.size(), 10000)));

        // ✅ url만 가져오도록 소스 필터
        query.addSourceFilter(FetchSourceFilter.of(b -> b.withIncludes("url"))); // :contentReference[oaicite:3]{index=3}

        SearchHits<NewsDoc> hits = operations.search(query, NewsDoc.class);
        return hits.getSearchHits().stream()
                .map(h -> h.getContent().getUrl())
                .filter(u -> u != null && !u.isBlank())
                .collect(Collectors.toSet());
    }
}