package com.optimagrowth.license.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Oywayten 04.02.2024.
 */

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "example")
public class LicenseConfig {

    private String property;

}
