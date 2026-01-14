package com.hyunha.batch.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.hyunha.batch.stock.call_kis_api_job.infra.jpa",
        entityManagerFactoryRef = "stockEntityManagerFactory",
        transactionManagerRef = "stockTransactionManager"
)
public class StockDataSourceConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.stock")
    public DataSource stockDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean stockEntityManagerFactory(
            EntityManagerFactoryBuilder builder
    ) {
        return builder
                .dataSource(stockDataSource())
                .packages("com.hyunha.batch.stock.call_kis_api_job.infra.jpa.entity")
                .persistenceUnit("stock")
                .build();
    }

    @Bean
    public PlatformTransactionManager stockTransactionManager(
            @Qualifier("stockEntityManagerFactory")
            EntityManagerFactory emf
    ) {
        return new JpaTransactionManager(emf);
    }

    @Bean
    public JdbcTemplate stockJdbcTemplate() {
        return new JdbcTemplate(stockDataSource());
    }

    @Bean
    public NamedParameterJdbcTemplate stockNamedParameterJdbcTemplate() {
        return new NamedParameterJdbcTemplate(stockDataSource());
    }
}
