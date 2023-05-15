package ru.mirea.secureapp.components.jwt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "jwt")
@Component
@Data
public class JwtProperties {

    private String secretKey = "rzxlszyykpbgqcflzxsqcysyhljt";

    // validity in milliseconds
    private long validityInMs = 60 * 60 * 1000; // 1h
    private long refreshValidityInMs = 24 * 60 * 60 * 1000; // 24h
}