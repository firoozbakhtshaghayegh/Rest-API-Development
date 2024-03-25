package com.telstra.sim.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/sims")
public class SimActivationController {

    private final RestTemplate restTemplate;
    private final String actuatorBaseUrl;

    public SimActivationController(RestTemplate restTemplate, @Value("${actuator.base.url}") String actuatorBaseUrl) {
        this.restTemplate = restTemplate;
        this.actuatorBaseUrl = actuatorBaseUrl;
    }

    @PostMapping
    public String activateSim(@RequestBody SimActivationRequest request) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> actuatorRequest = objectMapper.convertValue(request, new TypeReference<Map<String, String>>() {
        });

        ResponseEntity<Map<String, Boolean>> response = restTemplate.exchange(
                actuatorBaseUrl + "/actuate",
                HttpMethod.POST,
                new HttpEntity<>(actuatorRequest),
                new TypeReference<Map<String, Boolean>>() {
                }
        );

        return "SIM activation request for ICCID: " + request.getIccid() + " responded: " + response.getBody().get("success");
    }

    private static class SimActivationRequest {
        private String iccid;
        private String customerEmail;

        public SimActivationRequest(String iccid, String customerEmail) {
            this.iccid = iccid;
            this.customerEmail = customerEmail;
        }

        public String getIccid() {
            return iccid;
        }

        public String getCustomerEmail() {
            return customerEmail;
        }
    }
}