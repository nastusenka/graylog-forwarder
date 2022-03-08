package com.shulichenko.tool.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shulichenko.tool.config.GraylogForwarderConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GraylogHttpClientTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private GraylogForwarderConfig graylogForwarderConfig;

    private final ObjectMapper mapper = new ObjectMapper();

    private GraylogHttpClient tested;

    @BeforeEach
    public void setup() {
        tested = new GraylogHttpClient(restTemplate, mapper, graylogForwarderConfig);
    }

    @Test
    void testSendRequest() {
        Map<String, Object> fields = Map.of(
            "version", "1.1"
        );

        ResponseEntity<String> response = new ResponseEntity<>(HttpStatus.ACCEPTED);
        var url = "http://localhost";
        when(graylogForwarderConfig.getUrl()).thenReturn(url);
        when(restTemplate.exchange(eq(url), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
            .thenReturn(response);

        assertTrue(tested.sendRequest(fields));
    }

    @Test
    void testSendRequest_fails_when_response_from_server_is_invalid() {
        Map<String, Object> fields = Map.of(
            "version", "1.1"
        );

        ResponseEntity<String> response = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        var url = "http://localhost";
        when(graylogForwarderConfig.getUrl()).thenReturn(url);
        when(restTemplate.exchange(eq(url), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
            .thenReturn(response);

        assertFalse(tested.sendRequest(fields));
    }

    @Test
    void testSendRequest_fails_on_RestClientException(){
        Map<String, Object> fields = Map.of(
            "version", "1.1"
        );

        var url = "http://localhost";
        when(graylogForwarderConfig.getUrl()).thenReturn(url);
        when(restTemplate.exchange(eq(url), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
            .thenThrow(RestClientException.class);

        assertFalse(tested.sendRequest(fields));
    }
}