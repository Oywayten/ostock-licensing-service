package com.optimagrowth.license.controller;

import com.optimagrowth.license.model.License;
import com.optimagrowth.license.service.LicenseService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

/**
 * Oywayten 27.01.2024.
 */

@RestController
@RequestMapping("/v1/organization/{organizationId}/license")
@AllArgsConstructor
public class LicenseController {

    private final LicenseService licenseService;

    @GetMapping("/{licenseId}")
    public ResponseEntity<License> getLicense(@PathVariable("organizationId") String organizationId,
                                              @PathVariable("licenseId") String licenseId) {
        License license = licenseService.getLicense(licenseId, organizationId);
        return ResponseEntity.ok(license);
    }

    @PostMapping
    public ResponseEntity<String> createLicense(
            @PathVariable("organizationId") String organizationId,
            @RequestBody License license,
            @RequestHeader(value = "Accept-Language", required = false) Locale locale) {
        String response = licenseService.createLicense(license, organizationId, locale);
        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<String> updateLicense(
            @PathVariable("organizationId") String organizationId,
            @RequestBody License license,
            @RequestHeader(value = "Accept-Language", required = false) Locale locale) {
        String response = licenseService.updateLicense(license, organizationId, locale);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{licenseId}")
    public ResponseEntity<String> deleteLicense(
            @PathVariable("organizationId") String organizationId,
            @PathVariable("licenseId") String licenseId,
            @RequestHeader(value = "Accept-Language", required = false) Locale locale) {
        String response = licenseService.deleteLicense(licenseId, organizationId, locale);
        return ResponseEntity.ok(response);
    }
}
