package com.hyunha.batch.stock.call_kis_api_job.domain;

// ✅ 네이버 아이템 -> Scrapyd 작업 단위
public record ScrapydJob(String spiderName, String url, String query, String symbol, String description, String publishedAt) {}