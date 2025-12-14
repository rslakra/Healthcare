package com.rslakra.healthcare.healthsuite.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import java.util.Locale;

/**
 * Web MVC configuration for HealthSuite application.
 * Replaces the XML-based servlet-config.xml with Java-based configuration.
 * 
 * @author rslakra
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * Locale resolver for internationalization.
     */
    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver resolver = new SessionLocaleResolver();
        resolver.setDefaultLocale(Locale.ENGLISH);
        return resolver;
    }

    /**
     * Locale change interceptor for handling language parameter.
     */
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("language");
        return interceptor;
    }

    /**
     * Thymeleaf is auto-configured by Spring Boot when spring-boot-starter-thymeleaf is present.
     * Templates are in src/main/resources/templates/ by default.
     * No explicit view resolver configuration needed.
     */

    /**
     * Add locale change interceptor.
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }

    /**
     * Configure content negotiation.
     */
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer
            .favorPathExtension(true)
            .favorParameter(false)
            .ignoreAcceptHeader(false)
            .defaultContentType(MediaType.TEXT_HTML)
            .mediaType("json", MediaType.APPLICATION_JSON);
    }
}

