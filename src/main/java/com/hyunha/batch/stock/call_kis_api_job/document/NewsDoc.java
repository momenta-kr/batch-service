package com.hyunha.batch.stock.call_kis_api_job.document;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.Instant;

@Document(indexName = "momenta-news")
@Getter
@Setter
public class NewsDoc {

    @Id
    private String id;

    @Field(type = FieldType.Keyword)
    private String url;

    @Field(type = FieldType.Text)
    private String title;

    @Field(type = FieldType.Text)
    private String body;

    private String description;
    private Instant publishedAt;
    private Instant crawledAt;
}

