package com.shulichenko.tool.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shulichenko.tool.config.GraylogForwarderConfig;
import com.shulichenko.tool.util.EnvUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GraylogForwarderTest {

    @Mock
    private GraylogHttpClient graylogHttpClient;

    @Mock
    private GraylogForwarderConfig graylogForwarderConfig;

    @Mock
    private EnvUtil envUtil;

    @Captor
    private ArgumentCaptor<Map<String, Object>> captor;

    private final ObjectMapper mapper = new ObjectMapper();

    private GraylogForwarder tested;

    @BeforeEach
    public void setup() {
        tested = new GraylogForwarder(mapper, envUtil, graylogForwarderConfig, graylogHttpClient);
    }

    @Test
    void testConsume() throws JsonProcessingException {
        var inputString = "{\"ClientDeviceType\": \"desktop\"}";
        var json = mapper.readTree(inputString);
        var expectedMap = Map.of("version", "1.1",
            "host", "localhost",
            "short_message", "I am message",
            "_ClientDeviceType", "desktop");
        when(graylogForwarderConfig.getMessageVersion()).thenReturn("1.1");
        when(graylogForwarderConfig.getShortMessage()).thenReturn("I am message");
        when(envUtil.getHostname()).thenReturn("localhost");

        tested.consume(json);

        verify(graylogHttpClient, times(1)).sendRequest(captor.capture());
        assertEquals(expectedMap, captor.getValue());
    }
}