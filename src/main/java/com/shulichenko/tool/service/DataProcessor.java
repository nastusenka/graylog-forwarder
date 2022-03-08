package com.shulichenko.tool.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class DataProcessor {

    private static final Logger logger = LoggerFactory.getLogger(DataProcessor.class);

    private final List<LineListener> lineListeners;
    private final ObjectMapper mapper;

    public DataProcessor(List<LineListener> lineListeners, ObjectMapper mapper) {
        this.lineListeners = lineListeners;
        this.mapper = mapper;
    }

    /**
     * Process complete input data represented by BufferedReader
     *
     * @param reader
     */
    public void process(BufferedReader reader) {
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                parseString(line)
                    .ifPresent(json -> {
                        lineListeners.forEach(listener -> listener.consume(json));
                    });
            }
        } catch (IOException e) {
            logger.error("Error reading the line: {}", e.getMessage());
        }
    }

    private Optional<JsonNode> parseString(String str) {
        try {
            return Optional.ofNullable(mapper.readTree(str));
        } catch (JsonProcessingException e) {
            logger.error("Failed to parse string \"{}\" into JSON. Exception: {}", str, e.getMessage());
            return Optional.empty();
        }
    }
}
