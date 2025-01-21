package com.ulk.readingflow.core;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "secret")
public class SecretProperties {

    private String key;
    private String googleSecretKey;

}
