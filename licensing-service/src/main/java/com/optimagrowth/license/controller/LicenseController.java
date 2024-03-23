package com.optimagrowth.license.controller;

import com.optimagrowth.license.model.License;
import com.optimagrowth.license.service.LicenseService;
import com.optimagrowth.license.service.utils.UserContext;
import com.optimagrowth.license.service.utils.UserContextHolder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeoutException;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Oywayten 27.01.2024.
 */

@RestController
@RequestMapping("/v1/organization/{organizationId}/license")
@AllArgsConstructor
@Slf4j
public class LicenseController {

    private final LicenseService licenseService;

    @GetMapping("/{licenseId}")
    public ResponseEntity<License> getLicense(
            @PathVariable("organizationId") String organizationId,
            @PathVariable("licenseId") String licenseId) {
        License license = licenseService.getLicense(licenseId, organizationId);
        license.add(
                linkTo(methodOn(LicenseController.class).getLicense(organizationId, licenseId)).withSelfRel(),
                linkTo(methodOn(LicenseController.class).createLicense(license)).withRel("createLicense"),
                linkTo(methodOn(LicenseController.class).updateLicense(license)).withRel("updateLicense"),
                linkTo(methodOn(LicenseController.class).deleteLicense(organizationId)).withRel("deleteLicense"));
        return ResponseEntity.ok(license);
    }

    @GetMapping("/{licenseId}/{clientType}")
    public License getLicenseWithClient(
            @PathVariable String organizationId,
            @PathVariable String licenseId,
            @PathVariable String clientType
    ) {
        return licenseService.getLicense(organizationId, licenseId, clientType);
    }

    @PostMapping
    public ResponseEntity<License> createLicense(@RequestBody License license) {
        License savedLicense = licenseService.createLicense(license);
        return ResponseEntity.ok(savedLicense);
    }

    @PutMapping
    public ResponseEntity<License> updateLicense(@RequestBody License license) {
        License updatedLicense = licenseService.updateLicense(license);
        return ResponseEntity.ok(updatedLicense);
    }

    @DeleteMapping("/{licenseId}")
    public ResponseEntity<String> deleteLicense(@PathVariable("licenseId") String licenseId) {
        String response = licenseService.deleteLicense(licenseId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/")
    public List<License> getLicenses(@PathVariable("organizationId") String organizationId) throws TimeoutException {
        log.debug("LicenseController Correlation id: {}", getUserContext().getCorrelationId());
        return licenseService.getLicensesByOrganization(organizationId);
    }

    private UserContext getUserContext() {
        return UserContextHolder.getContext();
    }
}
