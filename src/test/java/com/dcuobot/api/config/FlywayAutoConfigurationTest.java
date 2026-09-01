package com.dcuobot.api.config;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.flyway.autoconfigure.FlywayAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Guards against the dependency this project shipped with briefly: {@code flyway-core} and
 * {@code flyway-mysql} alone are the Flyway library, not Spring Boot's wiring for it. Without
 * {@code spring-boot-starter-flyway} on the classpath, {@link FlywayAutoConfiguration} never
 * activates, so {@code spring.flyway.*} properties (like {@code baseline-on-migrate} in
 * application.yml) are silently ignored and Flyway never runs at all - the app just keeps
 * whatever schema already exists, with no migration tracking.
 * <p>
 * Runs against an isolated context with an empty migration location (not the real, MariaDB-only
 * {@code db/migration}, which can't execute against this test's H2 database) purely to prove the
 * autoconfiguration activates and binds our configured properties.
 */
class FlywayAutoConfigurationTest {
    @Test
    void flywayAutoConfiguration_activates_andBindsBaselineOnMigrate() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(DataSourceAutoConfiguration.class, FlywayAutoConfiguration.class))
                .withPropertyValues(
                        "spring.datasource.url=jdbc:h2:mem:flyway-autoconfig-test;DB_CLOSE_DELAY=-1",
                        "spring.datasource.driver-class-name=org.h2.Driver",
                        "spring.flyway.locations=classpath:db/no-migrations-here",
                        "spring.flyway.baseline-on-migrate=true"
                )
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).hasSingleBean(Flyway.class);
                    assertThat(context.getBean(Flyway.class).getConfiguration().isBaselineOnMigrate()).isTrue();
                });
    }
}
