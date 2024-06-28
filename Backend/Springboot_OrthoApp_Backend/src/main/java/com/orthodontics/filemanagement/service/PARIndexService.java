package com.orthodontics.filemanagement.service;

import ch.qos.logback.classic.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PARIndexService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    

    @Value("${flask.endpoint.url}")
    private String djangoEndpointUrl;

    public String processCoordinates(Map<String, Object> segments) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            String requestBody = objectMapper.writeValueAsString(segments);
            headers.setContentLength(requestBody.length());
            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(djangoEndpointUrl, request, String.class);

            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to process coordinates", e);
        }
    }
    
}
