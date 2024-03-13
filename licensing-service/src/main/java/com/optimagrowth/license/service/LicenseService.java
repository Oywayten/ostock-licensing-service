package com.optimagrowth.license.service;

import com.optimagrowth.license.config.LicenseConfig;
import com.optimagrowth.license.model.License;
import com.optimagrowth.license.model.Organization;
import com.optimagrowth.license.repository.LicenseRepository;
import com.optimagrowth.license.service.client.OrganizationDiscoveryClient;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Oywayten 27.01.2024.
 */

@Service
@AllArgsConstructor
public class LicenseService {

    public static final String DISCOVERY_TYPE = "discovery";
    public static final String LICENSE_SEARCH_ERROR_MESSAGE = "license.search.error.message";
    public static final String DISCOVERY_CLIENT_MESSAGE = "I am using the discovery client";
    private final MessageSource messages;
    private final LicenseRepository licenseRepository;
    private final LicenseConfig config;

    private final OrganizationDiscoveryClient organizationDiscoveryClient;

    public License getLicense(String licenseId, String organizationId) {
        License license = licenseRepository.findByOrganizationIdAndLicenseId(organizationId, licenseId);
        if (null == license) {
            throw new IllegalArgumentException(messages.getMessage(LICENSE_SEARCH_ERROR_MESSAGE,
                    new Object[]{licenseId, organizationId}, Locale.getDefault()));
        }
        return license.withComment(config.getExampleProperty());
    }

    public License getLicense(String licenseId, String organizationId, String clientType) {
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

    private Organization retrieveOrganizationInfo(String organizationId, String clientType) {
        Organization organization = null;

        if (DISCOVERY_TYPE.equals(clientType)) {
            System.out.println(DISCOVERY_CLIENT_MESSAGE);
            organization = organizationDiscoveryClient.getOrganization(organizationId);
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
        return messages.getMessage("license.delete.message",
                new Object[]{license.getLicenseId(), license.getOrganizationId()}, Locale.getDefault());
    }

    public List<License> getLicensesByOrganization(String organizationId) {
        return licenseRepository.findByOrganizationId(organizationId);
    }
}
