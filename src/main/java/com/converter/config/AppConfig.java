package com.converter.config;

import com.converter.model.MapperConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;

@Configuration
public class AppConfig {
    private static final String DEFAULT_CONFIG_RESOURCE = "maps.json";

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public MapperConfig mapperConfig(@Value("${mapper.config.file:}") String mapperConfigFile,
                                     ObjectMapper objectMapper) {
        try {
            File configFile = null;
            if (StringUtils.hasLength(mapperConfigFile)) {
                configFile = new File(mapperConfigFile);
                if (!configFile.exists()) {
                    throw new RuntimeException(mapperConfigFile + " does not exist");
                }
                return objectMapper.readValue(configFile, MapperConfig.class);
            } else {
                return objectMapper.readValue(new ClassPathResource("maps.json").getInputStream(), MapperConfig.class);
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to read " + mapperConfigFile + ", cause " + e.getMessage());
        }
    }
}
