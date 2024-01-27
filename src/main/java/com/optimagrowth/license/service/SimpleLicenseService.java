package com.optimagrowth.license.service;

import com.optimagrowth.license.model.License;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * Oywayten 27.01.2024.
 */

@Service
public class SimpleLicenseService implements LicenseService {

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
    public String createLicense(License license, String organizationId) {
        String responseMessage = null;
        if (license != null) {
            license.setOrganizationId(organizationId);
            responseMessage = String.format("This is the post and the object is: %s", license);
        }
        return responseMessage;
    }

    @Override
    public String updateLicense(License license, String organizationId) {
        String responseMessage = null;
        if (license != null) {
            license.setOrganizationId(organizationId);
            responseMessage = String.format("This is the put and the object is: %s", license);
        }
        return responseMessage;
    }

    @Override
    public String deleteLicense(String licenseId, String organizationId){
        String responseMessage;
        responseMessage = String.format(
                "Deleting license with id %s for the organization %s",
                licenseId, organizationId);
        return responseMessage;
    }
}
