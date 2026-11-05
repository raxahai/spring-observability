package org.practice.spring_observability.config;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MeterConfig {
    @Bean
    public TimedAspect register(MeterRegistry registry) {
        return new TimedAspect(registry);
    }
}
