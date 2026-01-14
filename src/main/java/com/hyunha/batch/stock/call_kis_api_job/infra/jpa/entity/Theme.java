package com.hyunha.batch.stock.call_kis_api_job.infra.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Table(name = "themes")
@Entity
public class Theme {

    @Id
    @Column(name = "theme_code")
    private String code;

    @Column(name = "theme_name")
    private String name;

}
