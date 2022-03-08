package com.shulichenko.tool.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shulichenko.tool.util.EnvUtil;
import com.shulichenko.tool.config.GraylogForwarderConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Service that performs GELF message forwarding to Graylog
 */
@Service
public class GraylogForwarder implements LineListener {

    private static final Logger logger = LoggerFactory.getLogger(GraylogForwarder.class);
    private static final String FIELD_VERSION = "version";
    private static final String FIELD_HOST = "host";
    private static final String FIELD_SHORT_MESSAGE = "short_message";

    private final ObjectMapper mapper;
    private final EnvUtil envUtil;
    private final GraylogForwarderConfig graylogForwarderConfig;
    private final GraylogHttpClient graylogHttpClient;

    public GraylogForwarder(ObjectMapper mapper,
                            EnvUtil envUtil,
                            GraylogForwarderConfig graylogForwarderConfig,
                            GraylogHttpClient graylogHttpClient) {
        this.mapper = mapper;
        this.envUtil = envUtil;
        this.graylogForwarderConfig = graylogForwarderConfig;
        this.graylogHttpClient = graylogHttpClient;
    }

    public void consume(JsonNode json) {
        var fields = mapper.convertValue(json, new TypeReference<Map<String, Object>>() {});
        if (!graylogHttpClient.sendRequest(appendMandatoryParameters(fields))) {
            logger.error("There was an error sending message to Graylog");
        }
    }

    /**
     * Transform data into GELF format. Append mandatory fields, rename additional fields
     * to match specification.
     *
     * @param fields
     * @return GELF message
     */
    private Map<String, Object> appendMandatoryParameters(Map<String, Object> fields) {
        var messageFields = fields.entrySet().stream()
            .map(entry -> new AbstractMap.SimpleEntry<String, Object>("_" + entry.getKey(), entry.getValue()) {});

        return Stream.concat(messageFields, Map.of(
                FIELD_VERSION, graylogForwarderConfig.getMessageVersion(),
                FIELD_HOST, envUtil.getHostname(),
                FIELD_SHORT_MESSAGE, graylogForwarderConfig.getShortMessage()
            ).entrySet().stream())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v1));
    }
}
