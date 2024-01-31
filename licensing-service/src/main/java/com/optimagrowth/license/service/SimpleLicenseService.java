package com.optimagrowth.license.service;

import com.optimagrowth.license.model.License;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Random;

/**
 * Oywayten 27.01.2024.
 */

@Service
@AllArgsConstructor
public class SimpleLicenseService implements LicenseService {

    private final MessageSource messages;

    @Override
    public License getLicense(String licenseId, String organizationId) {
        License license = new License();
        license.setId(new Random().nextInt(1000));
        license.setLicenseId(licenseId);
        license.setOrganizationId(organizationId);
        license.setDescription("Software product");
        license.setProductName("Ostock");
        license.setLicenseType("full");
        return license;
    }

    @Override
    public String createLicense(License license, String organizationId, Locale locale) {
        String responseMessage = null;
        if (license != null) {
            license.setOrganizationId(organizationId);
            responseMessage = messages.getMessage("license.create.message", new Object[]{license}, locale);
        }
        return responseMessage;
    }

    @Override
    public String updateLicense(License license, String organizationId, Locale locale) {
        String responseMessage = null;
        if (license != null) {
            license.setOrganizationId(organizationId);
            responseMessage = messages.getMessage("license.update.message", new Object[]{license}, locale);
        }
        return responseMessage;
    }

    @Override
    public String deleteLicense(String licenseId, String organizationId, Locale locale) {
        String responseMessage;
        responseMessage = messages.getMessage("license.delete.message", new Object[]{licenseId, organizationId}, locale);
        return responseMessage;
    }
}
