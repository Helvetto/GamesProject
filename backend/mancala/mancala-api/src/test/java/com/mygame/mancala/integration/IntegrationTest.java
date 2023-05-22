package com.mygame.mancala.integration;

import com.mygame.mancala.integration.config.EmbeddedDataSourceConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestConstructor;

@SpringBootTest
@Import(EmbeddedDataSourceConfiguration.class)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class IntegrationTest {

}
