package com.dcuobot.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class OpenApiConfig {
    private final BuildProperties buildProperties;

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
                .info(new Info()
                        .title("DCUOBot API")
                        .version(buildProperties.getVersion())
                        .description("""
                                Public API backing the DCUOBot Discord bot for DC Universe Online. \
                                Serves live character and league (guild) data sourced from Daybreak Games' \
                                Census API, game server status, and static reference data (alignments, \
                                power types, artifacts, allies, etc.) used to resolve the ids returned by \
                                the character and league endpoints.

                                Endpoints under `/v1/census/*` proxy and enrich data from the Census API and \
                                may return `502 Bad Gateway` if Census is unreachable. Endpoints under \
                                `/v1/data/*` serve static reference data maintained by DCUOBot and are not \
                                dependent on Census availability.""")
                        .contact(new Contact()
                                .name("DCUOBot")
                                .url("https://github.com/DCUOBot/dcuobot-api"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://github.com/DCUOBot/dcuobot-api/blob/main/LICENSE")))
                .servers(List.of(
                        new Server().url("https://dcuo.bot/api").description("Production")
                ));
    }
}
