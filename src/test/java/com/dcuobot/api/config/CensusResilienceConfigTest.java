package com.dcuobot.api.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Locks in the circuit breaker/timeout values {@link CensusResilienceConfig} applies to every
 * {@code CensusClient} call, so a change to these thresholds is a deliberate edit rather than an
 * accidental one.
 */
class CensusResilienceConfigTest {
    @Test
    void circuitBreaker_opensAfterHalfOfAMinimumOfFiveCallsFail_andStaysOpenFor30Seconds() {
        CircuitBreakerConfig config = CensusResilienceConfig.circuitBreakerConfig();

        assertThat(config.getSlidingWindowSize()).isEqualTo(10);
        assertThat(config.getMinimumNumberOfCalls()).isEqualTo(5);
        assertThat(config.getFailureRateThreshold()).isEqualTo(50f);
        assertThat(config.getPermittedNumberOfCallsInHalfOpenState()).isEqualTo(3);
        assertThat(config.getWaitIntervalFunctionInOpenState().apply(1)).isEqualTo(Duration.ofSeconds(30).toMillis());
    }

    @Test
    void timeLimiter_boundsCallsToSixSeconds_asABackstopAboveFeignsOwnReadTimeout() {
        TimeLimiterConfig config = CensusResilienceConfig.timeLimiterConfig();

        assertThat(config.getTimeoutDuration()).isEqualTo(Duration.ofSeconds(6));
    }
}
