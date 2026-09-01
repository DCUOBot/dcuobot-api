package com.dcuobot.api.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Configures the circuit breaker that wraps every {@code CensusClient} call (enabled via
 * {@code feign.circuitbreaker.enabled} in application.yml). Census is the one upstream dependency
 * every endpoint in this API relies on; without a breaker, a slow or down Census would let calls
 * pile up on Feign's read timeout ({@code feign.client.config.censusClient.read-timeout}) one
 * request at a time, tying up server threads. Once failures cross the threshold the breaker
 * opens and short-circuits straight to {@link com.dcuobot.api.census.client.CensusClientFallback},
 * so the app fails fast instead of piling up slow calls, and Census gets a break from a request
 * flood while it's already struggling.
 * <p>
 * There's only the one Feign client in this app, so a single default configuration (rather than
 * one keyed per circuit breaker id) is all that's needed.
 */
@Configuration
public class CensusResilienceConfig {
    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> censusCircuitBreakerCustomizer() {
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .circuitBreakerConfig(circuitBreakerConfig())
                .timeLimiterConfig(timeLimiterConfig())
                .build());
    }

    static CircuitBreakerConfig circuitBreakerConfig() {
        return CircuitBreakerConfig.custom()
                .slidingWindowSize(10)
                .minimumNumberOfCalls(5)
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofSeconds(30))
                .permittedNumberOfCallsInHalfOpenState(3)
                .build();
    }

    static TimeLimiterConfig timeLimiterConfig() {
        // A safety net above Feign's own read timeout, in case a call hangs somewhere Feign's
        // timeout doesn't cover.
        return TimeLimiterConfig.custom()
                .timeoutDuration(Duration.ofSeconds(6))
                .build();
    }
}
