package com.optimagrowth.license.service;

import com.optimagrowth.license.config.LicenseConfig;
import com.optimagrowth.license.model.License;
import com.optimagrowth.license.model.Organization;
import com.optimagrowth.license.repository.LicenseRepository;
import com.optimagrowth.license.service.client.OrganizationDiscoveryClient;
import com.optimagrowth.license.service.client.OrganizationFeignClient;
import com.optimagrowth.license.service.client.OrganizationRestTemplateClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

/**
 * Oywayten 27.01.2024.
 */

@Service
@Slf4j
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

    public LicenseService(MessageSource messages, LicenseRepository licenseRepository, LicenseConfig config, OrganizationDiscoveryClient organizationDiscoveryClient, OrganizationRestTemplateClient organizationRestTemplateClient, OrganizationFeignClient organizationFeignClient) {
        this.messages = messages;
        this.licenseRepository = licenseRepository;
        this.config = config;
        this.organizationDiscoveryClient = organizationDiscoveryClient;
        this.organizationRestTemplateClient = organizationRestTemplateClient;
        this.organizationFeignClient = organizationFeignClient;
    }

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

    @CircuitBreaker(name="organizationService")
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

    @CircuitBreaker(name = "licenseService")
    public List<License> getLicensesByOrganization(String organizationId) throws TimeoutException {
        randomlyRunLong();
        return licenseRepository.findByOrganizationId(organizationId);
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
            throw new TimeoutException("It was timeout exception" + sleepDuration + "ms");
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }
    }
}
