package com.example.settlementsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestClient;

@SpringBootApplication
@EnableScheduling
public class SettlementsystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(SettlementsystemApplication.class, args);
    }

    @Bean
    public RestClient restClient(){
        return RestClient.create();
    }

}
