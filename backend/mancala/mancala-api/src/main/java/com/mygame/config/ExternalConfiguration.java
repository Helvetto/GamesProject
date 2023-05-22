package com.mygame.config;

import com.mygame.model.config.ModelConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        ModelConfiguration.class
})
public class ExternalConfiguration {
}
