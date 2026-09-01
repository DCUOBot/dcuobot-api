package com.dcuobot.api.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies {@link RateLimitFilter} lets a client through while under its limit, blocks it with a
 * 429 once the limit is exceeded, tracks separate clients independently, and identifies clients
 * via {@code CF-Connecting-IP} (Cloudflare) ahead of {@code X-Forwarded-For} (Traefik).
 */
class RateLimitFilterTest {
    private static final int CAPACITY = 60;

    private RateLimitFilter filter;

    @BeforeEach
    void setUp() {
        filter = new RateLimitFilter(new ObjectMapper());
    }

    @Test
    void allowsRequests_upToCapacity() throws Exception {
        for (int i = 0; i < CAPACITY; i++) {
            MockHttpServletResponse response = sendRequest("203.0.113.1");
            assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        }
    }

    @Test
    void blocksRequests_onceCapacityIsExceeded() throws Exception {
        for (int i = 0; i < CAPACITY; i++) {
            sendRequest("203.0.113.2");
        }

        MockHttpServletResponse response = sendRequest("203.0.113.2");

        assertThat(response.getStatus()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS.value());
        assertThat(response.getHeader("Retry-After")).isEqualTo("60");
        assertThat(response.getContentAsString()).contains("\"status\":429").contains("\"path\":\"/v1/census/characters\"");
    }

    @Test
    void tracksClients_independently_byIpAddress() throws Exception {
        for (int i = 0; i < CAPACITY; i++) {
            sendRequest("203.0.113.3");
        }

        MockHttpServletResponse exhaustedClient = sendRequest("203.0.113.3");
        MockHttpServletResponse freshClient = sendRequest("203.0.113.4");

        assertThat(exhaustedClient.getStatus()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS.value());
        assertThat(freshClient.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void identifiesClient_byForwardedFor_whenPresent() throws Exception {
        for (int i = 0; i < CAPACITY; i++) {
            MockHttpServletRequest request = new MockHttpServletRequest("GET", "/v1/census/characters");
            request.setRemoteAddr("10.0.0.1");
            request.addHeader("X-Forwarded-For", "203.0.113.5, 10.0.0.1");
            MockHttpServletResponse response = new MockHttpServletResponse();
            filter.doFilter(request, response, new MockFilterChain());
        }

        MockHttpServletRequest sameClientDifferentProxy = new MockHttpServletRequest("GET", "/v1/census/characters");
        sameClientDifferentProxy.setRemoteAddr("10.0.0.2");
        sameClientDifferentProxy.addHeader("X-Forwarded-For", "203.0.113.5, 10.0.0.2");
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(sameClientDifferentProxy, response, new MockFilterChain());

        assertThat(response.getStatus()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS.value());
    }

    @Test
    void identifiesClient_byCfConnectingIp_beforeForwardedFor() throws Exception {
        for (int i = 0; i < CAPACITY; i++) {
            MockHttpServletRequest request = new MockHttpServletRequest("GET", "/v1/census/characters");
            request.setRemoteAddr("10.0.0.1");
            request.addHeader("CF-Connecting-IP", "203.0.113.6");
            request.addHeader("X-Forwarded-For", "203.0.113.6, 10.0.0.1");
            MockHttpServletResponse response = new MockHttpServletResponse();
            filter.doFilter(request, response, new MockFilterChain());
        }

        // Same true client (per CF-Connecting-IP), but a forged X-Forwarded-For leading value -
        // should still be recognized as the same, already-exhausted client.
        MockHttpServletRequest spoofedForwardedFor = new MockHttpServletRequest("GET", "/v1/census/characters");
        spoofedForwardedFor.setRemoteAddr("10.0.0.1");
        spoofedForwardedFor.addHeader("CF-Connecting-IP", "203.0.113.6");
        spoofedForwardedFor.addHeader("X-Forwarded-For", "9.9.9.9, 10.0.0.1");
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(spoofedForwardedFor, response, new MockFilterChain());

        assertThat(response.getStatus()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS.value());
    }

    @Test
    void doesNotLimit_actuatorRequests_evenPastCapacity() throws Exception {
        for (int i = 0; i < CAPACITY + 5; i++) {
            MockHttpServletRequest request = new MockHttpServletRequest("GET", "/actuator/health");
            request.setRemoteAddr("203.0.113.7");
            MockHttpServletResponse response = new MockHttpServletResponse();
            filter.doFilter(request, response, new MockFilterChain());

            assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        }
    }

    private MockHttpServletResponse sendRequest(String remoteAddr) throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/v1/census/characters");
        request.setRemoteAddr(remoteAddr);
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(request, response, new MockFilterChain());
        return response;
    }
}
