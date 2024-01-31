package com.optimagrowth.license.service;

import com.optimagrowth.license.model.License;

import java.util.Locale;

/**
 * Oywayten 27.01.2024.
 */

public interface LicenseService {
    License getLicense(String licenseId, String organizationId);

    String createLicense(License license, String organizationId, Locale locale);

    String updateLicense(License license, String organizationId, Locale locale);

    String deleteLicense(String licenseId, String organizationId, Locale locale);
}
