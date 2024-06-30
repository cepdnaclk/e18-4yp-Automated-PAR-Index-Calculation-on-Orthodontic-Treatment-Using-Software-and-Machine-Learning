package com.orthodontics.filemanagement.service;

import com.orthodontics.filemanagement.dto.PARIndexIntermediateData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orthodontics.filemanagement.dto.PARIndexWebRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PARIndexService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${django.endpoint.url}")
    private String djangoEndpointUrl;

    @Value("${flask.endpoint.upper_stl_points}")
    private String upperStlPointsEndpoint;

    @Value("${flask.endpoint.lower_stl_points}")
    private String lowerStlPointsEndpoint;

    @Value("${flask.endpoint.buccal_stl_points}")
    private String buccalStlPointsEndpoint;

    @Value("${app.fileLocation}")
    private String fileLocation;

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

    public String processPredictedPoints(PARIndexIntermediateData parIndexIntermediateData) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            Map<String, Object> requestBodyMap = new HashMap<>();
            requestBodyMap.put("Upper Arch Segment", parIndexIntermediateData.getUpper_points());
            requestBodyMap.put("Lower Arch Segment", parIndexIntermediateData.getLower_points());
            requestBodyMap.put("Buccal Segment", parIndexIntermediateData.getBuccal_points());

            // Convert the map to a JSON string
            Object requestBody = objectMapper.writeValueAsString(requestBodyMap);
            HttpEntity<Object> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(djangoEndpointUrl, request, String.class);

            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to process coordinates", e);
        }
    }

    public void getPredictedPoints(PARIndexWebRequest parIndexWebRequest, PARIndexIntermediateData parIndexIntermediateData) {
        try {
            // Extract gz files to stl files
            File upperFile = FileUtils.extractGzFile(parIndexWebRequest.getUpper_stl(), fileLocation);
            File lowerFile = FileUtils.extractGzFile(parIndexWebRequest.getLower_stl(), fileLocation);
            File buccalFile = FileUtils.extractGzFile(parIndexWebRequest.getBuccal_stl(), fileLocation);

            parIndexIntermediateData.setUpper_file(upperFile);
            parIndexIntermediateData.setLower_file(lowerFile);
            parIndexIntermediateData.setBuccal_file(buccalFile);

            // Get points from Flask for each file
            parIndexIntermediateData.setUpper_points(callFlaskEndpoint(upperStlPointsEndpoint, upperFile, "upper_stl"));
            parIndexIntermediateData.setLower_points(callFlaskEndpoint(lowerStlPointsEndpoint, lowerFile, "lower_stl"));
            parIndexIntermediateData.setBuccal_points(callFlaskEndpoint(buccalStlPointsEndpoint, buccalFile, "buccal_stl"));
        } catch (IOException e) {
            log.error("Error occurred while calling Flask endpoints", e);
            throw new RuntimeException("Failed to get predicted points from Flask", e);
        }
    }

    private Object callFlaskEndpoint(String endpoint, File file, String fileName) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            // Create a map to hold the file name and path
            Map<String, String> requestBodyMap = new HashMap<>();
            requestBodyMap.put(fileName, file.getAbsolutePath());

            // Convert the map to a JSON string
            String requestBody = objectMapper.writeValueAsString(requestBodyMap);

            // Create the HTTP entity
            HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

            // Send the request
            ResponseEntity<String> response = restTemplate.postForEntity(endpoint, requestEntity, String.class);

            // Return the response body
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get predicted points", e);
        }
    }

}
