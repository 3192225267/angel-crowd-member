package com.angel.crowd.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author 刘振河
 * @create 2020--05--01 20:40
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
@ConfigurationProperties(prefix = "short.message")
public class ShortMessageProperites {
    private String host;
    private String path;
    private String method;
    private String appcode;
    private String sign;
    private String skin;
}
