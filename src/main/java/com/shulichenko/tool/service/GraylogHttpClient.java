package com.shulichenko.tool.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shulichenko.tool.config.GraylogForwarderConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * Simple Graylog HTTP client for sending GELF messages.
 */
@Service
public class GraylogHttpClient {

    private static final Logger logger = LoggerFactory.getLogger(GraylogHttpClient.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;
    private final GraylogForwarderConfig graylogForwarderConfig;

    public GraylogHttpClient(RestTemplate restTemplate,
                             ObjectMapper mapper,
                             GraylogForwarderConfig graylogForwarderConfig) {
        this.restTemplate = restTemplate;
        this.mapper = mapper;
        this.graylogForwarderConfig = graylogForwarderConfig;
    }

    /**
     * Send message to Graylog
     *
     * @param fields Complete set of fields in GELF format
     * @return
     */
    public boolean sendRequest(Map<String, Object> fields) {
        String message;
        try {
            message = mapper.writeValueAsString(fields);
        } catch (JsonProcessingException e) {
            logger.error("Failed to build the request body. Details: {}", e.getMessage());
            return false;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(message, headers);

        ResponseEntity<String> response;
        try {
            response = restTemplate.exchange(graylogForwarderConfig.getUrl(), HttpMethod.POST, entity, String.class);
        } catch (RestClientException e) {
            logger.error("Error sending request to Graylog: {}", e.getMessage());
            return false;
        }

        return response.getStatusCode().equals(HttpStatus.ACCEPTED);
    }
}
