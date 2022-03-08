package com.shulichenko.tool.service;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * General interface for handling the incoming data chunks
 */
public interface LineListener {

    /**
     * Handle single message data
     *
     * @param json
     */
    void consume(JsonNode json);
}
