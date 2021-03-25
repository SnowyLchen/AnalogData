package com.cj.demoredis.utils;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author chen
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "application.data")
public class ApiConfig {
    private Integer[] consumption;
    private Integer[] fault;
    private Integer[] gqs;
    private Integer[] ogqs;
    private Integer[] v1;
    private Integer[] v2;
    private Integer[] v3;
    private Integer[] types;
    private Integer[] electric;
    private Integer[] scatter;
}
