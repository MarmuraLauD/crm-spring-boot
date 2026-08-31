package com.gym.crmspringboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class CrmSpringBootApplication {

    static void main(String[] args) {
        SpringApplication.run(CrmSpringBootApplication.class, args);
    }

}
