package com.shulichenko.tool.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DataProcessorTest {

    @Mock
    private BufferedReader reader;

    @Mock
    private GraylogForwarder graylogForwarder;

    @Captor
    private ArgumentCaptor<JsonNode> jsonCaptor;

    private final ObjectMapper mapper = new ObjectMapper();

    private DataProcessor tested;

    @BeforeEach
    public void setup() {
        tested = new DataProcessor(List.of(graylogForwarder), mapper);
    }

    @Test
    void testProcess() throws IOException {
        var inputString = "{\"ClientDeviceType\": \"desktop\",\"ClientIP\": \"11.73.87.52\"}";
        var expectedJson = mapper.readTree(inputString);
        when(reader.readLine()).thenReturn(inputString,null);

        tested.process(reader);

        verify(graylogForwarder, times(1)).consume(jsonCaptor.capture());
        var actualJson = jsonCaptor.getValue();
        assertEquals(expectedJson, actualJson);
    }

    @Test
    void testProcess_doesnt_call_listeners_on_IOException() throws IOException {
        when(reader.readLine()).thenThrow(new IOException("error"));

        tested.process(reader);

        verify(graylogForwarder, never()).consume(any());
    }

    @Test
    void testProcess_doesnt_process_invalid_JSON_data() throws IOException {
        var invalidJsonString = "I am invalid JSON string {}";
        when(reader.readLine()).thenReturn(invalidJsonString, null);

        tested.process(reader);

        verify(graylogForwarder, never()).consume(any());
    }
}