package com.togedog.auth.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.redis")
@Getter
@Setter
public class RedisProperties {

    private int port;
    private String host;

    private int getPort() {
        return this.port;
    }

    private String getHost() {
        return this.host;
    }
}
