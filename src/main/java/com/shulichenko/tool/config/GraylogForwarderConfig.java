package com.shulichenko.tool.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GraylogForwarderConfig {

    @Value("${graylog.message.version}")
    String messageVersion;

    @Value("${graylog.message.short_message}")
    String shortMessage;

    @Value("${graylog.client.url}")
    String url;

    public String getMessageVersion() {
        return messageVersion;
    }

    public String getShortMessage() {
        return shortMessage;
    }

    public String getUrl() {
        return url;
    }
}
