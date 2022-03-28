package com.epam.esm;

import com.epam.esm.config.ParserProps;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ParserProps.class)
public class ParserApp {

    public static void main(String[] args) {
        SpringApplication.run(ParserApp.class, args);
    }
}
