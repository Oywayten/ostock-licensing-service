package com.optimagrowth.license.service;

import com.optimagrowth.license.config.LicenseConfig;
import com.optimagrowth.license.model.License;
import com.optimagrowth.license.repository.LicenseRepository;
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

    private final MessageSource messages;
    private final LicenseRepository licenseRepository;
    private final LicenseConfig config;

    public License getLicense(String licenseId, String organizationId) {
        License license = licenseRepository.findByOrganizationIdAndLicenseId(organizationId, licenseId);
        if (null == license) {
            throw new IllegalArgumentException(messages.getMessage("license.search.error.message",
                    new Object[]{licenseId, organizationId}, Locale.getDefault()));
        }
        return license.withComment(config.getProperty());
    }

    public License createLicense(License license) {
        license.setLicenseId(UUID.randomUUID().toString());
        licenseRepository.save(license);
        return license.withComment(config.getProperty());
    }

    public License updateLicense(License license) {
        licenseRepository.save(license);
        return license.withComment(config.getProperty());
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
