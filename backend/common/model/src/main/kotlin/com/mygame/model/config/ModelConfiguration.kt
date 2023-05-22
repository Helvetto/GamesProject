package com.mygame.model.config

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@Configuration
@EnableJpaRepositories(basePackages = ["com.mygame.model.entity", "repository"])
@EntityScan(basePackages = ["com.mygame.model"])
open class ModelConfiguration