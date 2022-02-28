package com.microserviceslearning.customer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(
        scanBasePackages = {
                "com.microserviceslearning.customer",
                "com.microserviceslearning.amqp"
        }
)
@EnableEurekaClient
@EnableFeignClients(
        basePackages = "com.microserviceslearning.clients"
)
public class CustomerApplication {
    public static void main(String[] args) {

        SpringApplication.run(CustomerApplication.class, args);
    }
}
