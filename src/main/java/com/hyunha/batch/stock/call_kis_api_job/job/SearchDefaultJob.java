package com.hyunha.batch.stock.call_kis_api_job.job;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyunha.batch.stock.call_kis_api_job.client.KisClient;
import com.hyunha.batch.stock.call_kis_api_job.model.response.FluctuationResponse;
import com.hyunha.batch.stock.call_kis_api_job.model.response.MarketCapRankResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Component
public class SearchDefaultJob {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager tx;
    private final KisClient kisClient;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    @Qualifier("stockNamedParameterJdbcTemplate")
    private final NamedParameterJdbcTemplate stockNamedParameterJdbcTemplate;

    @Bean
    public Job saveSearchDefaultJob(Step saveSearchDefaultStep) {
        return new JobBuilder("saveSearchDefaultJob", jobRepository)
                .start(saveSearchDefaultStep)
                .build();
    }

    private record SearchDefault(String symbol,
                                 String name,
                                 String sector,
                                 String market,
                                 int currentPrice,
                                 double changeRateFromPrevDay,
                                 Type type) {
        public enum Type {ALL, GAINER, LOSER}
    }

    @Bean
    public Step saveSearchDefaultStep() {
        return new StepBuilder("saveSearchDefaultStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    List<MarketCapRankResponse.Output> topMarketCapRankings = kisClient.fetchMarketCapRanking().getOutputList().stream().limit(5).toList();
                    List<FluctuationResponse.Output> topGainers = kisClient.fetchTopGainers().getOutput().stream().limit(5).toList();
                    List<FluctuationResponse.Output> topLosers = kisClient.fetchTopLosers().getOutput().stream().limit(5).toList();

                    Set<String> symbols = Stream.of(
                                    topMarketCapRankings.stream().map(MarketCapRankResponse.Output::getShortSymbolCode),
                                    topGainers.stream().map(FluctuationResponse.Output::getShortStockCode),
                                    topLosers.stream().map(FluctuationResponse.Output::getShortStockCode)
                            ).flatMap(s -> s)
                            .collect(Collectors.toSet());

                    String sql = "SELECT sm.symbol, t.theme_name FROM themes t " +
                            "JOIN theme_members tm ON tm.theme_code = t.theme_code " +
                            "JOIN stock_master sm ON sm.symbol = tm.stock_code " +
                            "WHERE sm.symbol IN (:symbols)";


                    MapSqlParameterSource params = new MapSqlParameterSource()
                            .addValue("symbols", symbols);
                    Map<String, String> themeNameBySymbol = stockNamedParameterJdbcTemplate.query(sql, params, (rs, rowNum) -> {
                                Map<String, String> row = new HashMap<>();
                                row.put("symbol", rs.getString("symbol"));
                                row.put("themeName", rs.getString("theme_name"));
                                return row;
                            }).stream()
                            .collect(Collectors.toMap(
                                    map -> map.get("symbol"),
                                    map -> map.get("themeName"),
                                    (a, b) -> a
                            ));

                    List<SearchDefault> searchDefaults = Stream.of(
                            topMarketCapRankings.stream()
                                    .map(output -> new SearchDefault(
                                            output.getShortSymbolCode(),
                                            output.getHtsKoreanName(),
                                            themeNameBySymbol.get(output.getShortSymbolCode()),
                                            "KOSPI",
                                            Integer.parseInt(output.getCurrentPrice().replace(",", "")),
                                            Double.parseDouble(output.getPrevDayChangeRate().replace(",", "")),
                                            SearchDefault.Type.ALL
                                    )),
                            topGainers.stream()
                                    .map(output -> new SearchDefault(
                                            output.getShortStockCode(),
                                            output.getStockName(),
                                            themeNameBySymbol.get(output.getShortStockCode()),
                                            "KOSPI",
                                            Integer.parseInt(output.getCurrentPrice().replace(",", "")),
                                            Double.parseDouble(output.getChangeRateFromPrevDay().replace(",", "")),
                                            SearchDefault.Type.GAINER
                                    )),
                            topLosers.stream()
                                    .map(output -> new SearchDefault(
                                            output.getShortStockCode(),
                                            output.getStockName(),
                                            themeNameBySymbol.get(output.getShortStockCode()),
                                            "KOSPI",
                                            Integer.parseInt(output.getCurrentPrice().replace(",", "")),
                                            Double.parseDouble(output.getChangeRateFromPrevDay().replace(",", "")),
                                            SearchDefault.Type.LOSER
                                    ))
                    ).flatMap(s -> s).toList();


                    String json = objectMapper.writeValueAsString(searchDefaults);
                    stringRedisTemplate.opsForValue().set("search:defaults", json);

                    return RepeatStatus.FINISHED;
                }, tx)
                .build();
    }

}
