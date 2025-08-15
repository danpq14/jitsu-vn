package com.jitsu.eventprocessor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "com.jitsu.common.model")
public class EventProcessorApplication {
    public static void main(String[] args) {
        SpringApplication.run(EventProcessorApplication.class, args);
    }
}