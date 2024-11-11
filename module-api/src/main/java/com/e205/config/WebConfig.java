package com.e205.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.StreamWriteConstraints;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfig {

  @Bean
  public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
    return builder -> builder
        .visibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
        .postConfigurer(objectMapper -> {
          objectMapper.getFactory().setStreamWriteConstraints(
              StreamWriteConstraints.builder().maxNestingDepth(2000).build()
          );
        });
  }


}
