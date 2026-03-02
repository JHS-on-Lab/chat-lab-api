package me.son.chatlabapi.global.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "user.admin")
public record AdminProperties(
        String username,
        String password
) {}
