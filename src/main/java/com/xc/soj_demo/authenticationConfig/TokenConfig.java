package com.xc.soj_demo.authenticationConfig;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


@Component
@Data
@ConfigurationProperties(prefix="token")
public class TokenConfig {
	
    private Map<String, String> config = new HashMap<>();
    private String resourceId;
    private String signingKey;
    private boolean isRefreshToken;

}
