package com.shulichenko.tool;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class GraylogHttpForwarder implements LineListener {

    private static final Logger logger = LoggerFactory.getLogger(GraylogHttpForwarder.class);

    private final ObjectMapper mapper;

    private final RestTemplate restTemplate;

    public GraylogHttpForwarder(ObjectMapper mapper, RestTemplate restTemplate) {
        this.mapper = mapper;
        this.restTemplate = restTemplate;
    }

    public void consume(JsonNode json) {
        try {
            sendRequest(buildRequestBody(json));
        } catch (JsonProcessingException e) {
            logger.error("Failed to build the request body. Details: {}", e.getMessage());
        } catch (RestClientException e) {
            logger.error("Error sending request to Graylog: {}", e.getMessage());
        }
    }

    private void sendRequest(String message) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(message, headers);

        // TODO handle exceptions
        restTemplate.exchange("http://192.168.99.100:12201/gelf", HttpMethod.POST, entity, String.class);
    }

    private String buildRequestBody(JsonNode json) throws JsonProcessingException {
        var fields = mapper.convertValue(json, new TypeReference<Map<String, Object>>() {})
            .entrySet().stream()
            .map(entry -> new AbstractMap.SimpleEntry<String, Object>("_" + entry.getKey(), entry.getValue()) {});

        var combinedFields = Stream.concat(fields, Map.of(
                "version", "1.1",
                "host", "localhost",
                "short_message", "Message produced by GraylogForwarder"
            ).entrySet().stream())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v1));

        return mapper.writeValueAsString(combinedFields);
    }
}
