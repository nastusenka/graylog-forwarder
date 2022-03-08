package com.shulichenko.tool.util;

import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Component
public class EnvUtil {

    /**
     * Get server hostname.
     *
     * @return hostname
     */
    public String getHostname() {
        String hostName = System.getenv("HOSTNAME");
        if (hostName == null || hostName.isEmpty()) {
            try {
                var address = InetAddress.getLocalHost();
                hostName = address.getHostName();
            } catch (UnknownHostException e) {
                hostName = "Unknown";
            }
        }
        return hostName;
    }
}
