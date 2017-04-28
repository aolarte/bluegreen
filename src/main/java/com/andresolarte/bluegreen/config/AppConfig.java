package com.andresolarte.bluegreen.config;

import com.andresolarte.bluegreen.routing.DynamicWeightedLoadBalancer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class AppConfig {

    @Bean
    DynamicWeightedLoadBalancer loadBalancer() {
        return new DynamicWeightedLoadBalancer(Arrays.asList(1,0));
    }
}
