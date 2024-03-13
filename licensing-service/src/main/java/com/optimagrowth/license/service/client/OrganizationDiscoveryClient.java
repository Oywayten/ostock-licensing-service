package com.optimagrowth.license.service.client;


import com.optimagrowth.license.model.Organization;
import lombok.AllArgsConstructor;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@AllArgsConstructor
public class OrganizationDiscoveryClient {

    private static final String ORGANIZATION_SERVICE_URI_PATTERN = "%s/v1/organization/%s";
    private final DiscoveryClient discoveryClient;

    public Organization getOrganization(String organizationId) {
        Organization organization = null;
        RestTemplate restTemplate = new RestTemplate();
        List<ServiceInstance> instances = discoveryClient.getInstances("organization-service");

        if (instances.size() == 0) {
            return organization;
        }
        String serviceUri =
                String.format(ORGANIZATION_SERVICE_URI_PATTERN, instances.get(0).getUri().toString(), organizationId);
        ResponseEntity<Organization> restExchange =
                restTemplate.exchange(serviceUri, HttpMethod.GET, null, Organization.class, organizationId);
        return restExchange.getBody();
    }
}
