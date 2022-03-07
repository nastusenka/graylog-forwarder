package com.shulichenko.tool;

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

    List<LineListener> lineListeners;

    private static final Logger log = LoggerFactory.getLogger(DataProcessor.class);

    private static final ObjectMapper mapper = new ObjectMapper();

    public DataProcessor(List<LineListener> lineListeners) {
        this.lineListeners = lineListeners;
    }

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
            // TODO: add logging
        }
    }

    public static Optional<JsonNode> parseString(String str) {
        try {
            return Optional.ofNullable(mapper.readTree(str));
        } catch (JsonProcessingException e) {
            log.error("Failed to parse string {} into JSON. Exception: {}", str, e.getMessage());
            return Optional.empty();
        }
    }
}
