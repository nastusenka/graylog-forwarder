package com.shulichenko.tool;

import com.fasterxml.jackson.databind.JsonNode;

public interface LineListener {

    void consume(JsonNode json);
}
