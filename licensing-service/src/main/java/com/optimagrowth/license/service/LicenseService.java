package com.optimagrowth.license.service;

import com.optimagrowth.license.config.LicenseConfig;
import com.optimagrowth.license.model.License;
import com.optimagrowth.license.model.Organization;
import com.optimagrowth.license.repository.LicenseRepository;
import com.optimagrowth.license.service.client.OrganizationDiscoveryClient;
import com.optimagrowth.license.service.client.OrganizationFeignClient;
import com.optimagrowth.license.service.client.OrganizationRestTemplateClient;
import com.optimagrowth.license.service.utils.UserContextHolder;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeoutException;

/**
 * Oywayten 27.01.2024.
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class LicenseService {

    private static final String DISCOVERY_TYPE = "discovery";
    private static final String REST_TYPE = "rest";
    private static final String FEIGN_TYPE = "feign";
    private static final String LICENSE_SEARCH_ERROR_MESSAGE = "license.search.error.message";
    private static final String DISCOVERY_CLIENT_MESSAGE = "I am using the discovery client";
    private static final String REST_CLIENT_MESSAGE = "I am using the rest client";
    private static final String FEIGN_CLIENT_MESSAGE = "I am using the feign client";
    private static final String LICENSE_DELETE_MESSAGE = "license.delete.message";

    @Value("${random.shift}")
    private int randomShift;

    @Value("${random.bound}")
    private int randomBound;

    @Value("${sleep.duration}")
    private int sleepDuration;

    private final MessageSource messages;
    private final LicenseRepository licenseRepository;
    private final LicenseConfig config;
    private final OrganizationDiscoveryClient organizationDiscoveryClient;
    private final OrganizationRestTemplateClient organizationRestTemplateClient;
    private final OrganizationFeignClient organizationFeignClient;

    public License getLicense(String licenseId, String organizationId) {
        License license = licenseRepository.findByOrganizationIdAndLicenseId(organizationId, licenseId);
        if (null == license) {
            throw new IllegalArgumentException(messages.getMessage(LICENSE_SEARCH_ERROR_MESSAGE,
                    new Object[]{licenseId, organizationId}, Locale.getDefault()));
        }
        return license.withComment(config.getExampleProperty());
    }

    public License getLicense(String organizationId, String licenseId, String clientType) {
        License license = licenseRepository.findByOrganizationIdAndLicenseId(organizationId, licenseId);
        if (null == license) {
            throw new IllegalArgumentException(messages.getMessage(LICENSE_SEARCH_ERROR_MESSAGE,
                    new Object[]{licenseId, organizationId}, Locale.getDefault()));
        }
        Organization organization = retrieveOrganizationInfo(organizationId, clientType);
        if (null != organization) {
            license.setOrganizationName(organization.getName());
            license.setContactName(organization.getContactName());
            license.setContactEmail(organization.getContactEmail());
            license.setContactPhone(organization.getContactPhone());
        }
        return license.withComment(config.getExampleProperty());
    }

    @CircuitBreaker(name = "organizationService")
    private Organization retrieveOrganizationInfo(String organizationId, String clientType) {
        Organization organization = null;

        switch (clientType) {
            case DISCOVERY_TYPE -> {
                log.info(DISCOVERY_CLIENT_MESSAGE);
                organization = organizationDiscoveryClient.getOrganization(organizationId);
            }
            case REST_TYPE -> {
                log.info(REST_CLIENT_MESSAGE);
                organization = organizationRestTemplateClient.getOrganization(organizationId);
            }
            case FEIGN_TYPE -> {
                log.info(FEIGN_CLIENT_MESSAGE);
                organization = organizationFeignClient.getOrganization(organizationId);
            }
        }
        return organization;
    }

    public License createLicense(License license) {
        license.setLicenseId(UUID.randomUUID().toString());
        licenseRepository.save(license);
        return license.withComment(config.getExampleProperty());
    }

    public License updateLicense(License license) {
        licenseRepository.save(license);
        return license.withComment(config.getExampleProperty());
    }

    public String deleteLicense(String licenseId) {
        License license = new License();
        license.setLicenseId(licenseId);
        licenseRepository.delete(license);
        return messages.getMessage(LICENSE_DELETE_MESSAGE,
                new Object[]{license.getLicenseId(), license.getOrganizationId()}, Locale.getDefault());
    }

    @CircuitBreaker(name = "licenseService", fallbackMethod = "buildFallbackLicenseList")
    @RateLimiter(name = "licenseService", fallbackMethod = "buildFallbackLicenseList")
    @Retry(name = "retryLicenseService", fallbackMethod = "buildFallbackLicenseList")
    @Bulkhead(name = "bulkheadLicenseService", fallbackMethod = "buildFallbackLicenseList")
    public List<License> getLicensesByOrganization(String organizationId) throws TimeoutException {
        log.debug("LicenseService Correlation id: {}", UserContextHolder.getContext().getCorrelationId());
        randomlyRunLong();
        return licenseRepository.findByOrganizationId(organizationId);
    }

    private List<License> buildFallbackLicenseList(String organizationId, Throwable t) {
        List<License> fallbackList = new ArrayList<>();
        License license = new License();
        license.setLicenseId("0000000-00-00000");
        license.setOrganizationId(organizationId);
        license.setProductName("Sorry no licensing information currently available");
        fallbackList.add(license);
        return fallbackList;
    }

    private void randomlyRunLong() throws TimeoutException {
        Random random = new Random();
        int randomNum = random.nextInt(randomBound) + randomShift;
        if (randomNum == randomBound) {
            sleep();
        }
    }

    private void sleep() throws TimeoutException {
        try {
            Thread.sleep(sleepDuration);
            throw new TimeoutException("It was timeout exception " + sleepDuration + " ms");
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }
    }
}
