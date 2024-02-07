package com.optimagrowth.license.repository;

import com.optimagrowth.license.model.License;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Oywayten 04.02.2024.
 */


public interface LicenseRepository extends CrudRepository<License, String> {

    List<License> findAllByOrganizationId(String organizationId);

    License findByOrganizationIdAndLicenseId(String organizationId, String licenseId);
}
