package com.mygame.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = {"com.mygame.mancala.repository"})
@EntityScan(basePackages = {"com.mygame.mancala"})
@EnableCaching
public class CoreConfguration {
}
