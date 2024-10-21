package com.rslakra.healthcare.healthsuite;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Static resources can be moved to /public (or /static or /resources or /META-INF/resources) in the classpath root.
 * The same applies to messages.properties (which Spring Boot automatically detects in the root of the classpath).
 */

@SpringBootApplication
public class HealthSuiteApplication extends SpringBootServletInitializer {

    /**
     * comment below if deploying outside web container
     * @param builder
     * @return
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return configureApplication(builder);
    }

    private static SpringApplicationBuilder configureApplication(SpringApplicationBuilder builder) {
        return builder.sources(HealthSuiteApplication.class).bannerMode(Banner.Mode.OFF);
    }

    public static void main(String[] args) {
        SpringApplication.run(HealthSuiteApplication.class);
    }

}
