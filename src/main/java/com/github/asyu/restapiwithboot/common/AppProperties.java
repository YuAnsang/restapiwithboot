package com.github.asyu.restapiwithboot.common;

import javax.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "my-app")
@Getter @Setter
public class AppProperties {

    @NotEmpty
    private String adminUsername;
    
    @NotEmpty
    private String adminPassword;
    
    @NotEmpty
    private String userUsername;
    
    @NotEmpty
    private String userPassword;
    
    @NotEmpty
    private String clientId;
    
    @NotEmpty
    private String clientSecret;
}
