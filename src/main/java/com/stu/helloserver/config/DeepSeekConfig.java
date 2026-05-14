package com.stu.helloserver.config;

import io.micrometer.observation.ObservationRegistry;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.springframework.ai.deepseek.api.DeepSeekApi;
import org.springframework.ai.model.SimpleApiKey;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.support.RetryTemplate;

@Configuration
public class DeepSeekConfig {

    @Value("${spring.ai.deepseek.api-key}")
    private String apiKey;

    @Bean
    public DeepSeekApi deepSeekApi() {
        return DeepSeekApi.builder()
                .apiKey(new SimpleApiKey(apiKey))
                .baseUrl("https://api.deepseek.com")
                .build();
    }

    @Bean
    public DeepSeekChatModel deepSeekChatModel(DeepSeekApi deepSeekApi) {
        DeepSeekChatOptions defaultOptions = DeepSeekChatOptions.builder()
                .model("deepseek-chat")
                .temperature(0.7)
                .build();
        return new DeepSeekChatModel(deepSeekApi, defaultOptions,
                ToolCallingManager.builder().build(),
                new RetryTemplate(),
                ObservationRegistry.NOOP);
    }
}
