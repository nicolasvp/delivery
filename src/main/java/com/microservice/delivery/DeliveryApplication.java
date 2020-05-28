package com.microservice.delivery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@EnableEurekaClient // No es realmente necesario, ya que con tener la dependencia en el pom.xml se autoregistra automaticamente
@EnableCircuitBreaker
@EnableFeignClients
@SpringBootApplication
@EntityScan({"com.microservices.commons.models.entity.delivery"})
@ComponentScan({"com.microservices.commons.models.services", "com.microservice.delivery"})
public class DeliveryApplication {

	public static void main(String[] args) {
        SpringApplication application = new SpringApplication(DeliveryApplication.class);
        application.setAllowBeanDefinitionOverriding(true);
        application.run(args);

	}

    @Bean
    public FeignErrorDecoder errorDecoder() {
        return new FeignErrorDecoder();
    }
}
