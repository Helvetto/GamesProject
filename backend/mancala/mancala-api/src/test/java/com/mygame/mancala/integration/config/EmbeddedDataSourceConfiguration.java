package com.mygame.mancala.integration.config;

import java.time.Duration;

import javax.sql.DataSource;

import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
@NoArgsConstructor
public class EmbeddedDataSourceConfiguration {

    private static final String DEFAULT_PG_DB_NAME = "postgres";
    private static final String DEFAULT_PG_USERNAME = "postgres";

    @Bean
    @SneakyThrows
    public DataSource dataSource(
    ) {
        String jdbcUrl;
        String userName = DEFAULT_PG_USERNAME;
        String password = DEFAULT_PG_USERNAME;
        var embeddedPostgres =
                EmbeddedPostgres.builder()
                        .setServerConfig("jit", "off")
                        .setPGStartupWait(Duration.ofSeconds(30)).start();
        jdbcUrl = embeddedPostgres.getJdbcUrl(DEFAULT_PG_DB_NAME, DEFAULT_PG_DB_NAME);

        var dataSource = new BasicDataSource();
        dataSource.setUsername(userName);
        dataSource.setPassword(password);
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl(jdbcUrl);
        log.info("Test database connection URL: {}", jdbcUrl);

        return dataSource;
    }

}