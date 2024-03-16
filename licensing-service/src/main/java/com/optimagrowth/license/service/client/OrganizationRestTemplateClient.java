package com.optimagrowth.license.service.client;

import com.optimagrowth.license.model.Organization;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@AllArgsConstructor
public class OrganizationRestTemplateClient {

    public static final String ORGANIZATION_SERVICE_URI_PATTERN =
            "http://organization-service/v1/organization/{organizationId}";
    private final RestTemplate restTemplate;

    public Organization getOrganization(String organizationId) {
        ResponseEntity<Organization> restExchange =
                restTemplate.exchange(
                        ORGANIZATION_SERVICE_URI_PATTERN, HttpMethod.GET, null, Organization.class, organizationId);
        return restExchange.getBody();
    }

}
