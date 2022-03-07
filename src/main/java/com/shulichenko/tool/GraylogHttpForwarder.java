package com.shulichenko.tool;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

@Component
public class GraylogHttpForwarder implements LineListener {

    public void consume(JsonNode json) {

    }
}
