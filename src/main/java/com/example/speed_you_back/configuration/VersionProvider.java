package com.example.speed_you_back.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class VersionProvider {
    @Value("${speed_you_back.version}")
    private String version;
}
