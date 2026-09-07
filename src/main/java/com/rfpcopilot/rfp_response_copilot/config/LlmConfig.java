package com.rfpcopilot.rfp_response_copilot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dev.langchain4j.model.openai.OpenAiChatModel;

@Configuration
public class LlmConfig {
	
	@Bean
    public OpenAiChatModel chatModel() {

        return OpenAiChatModel.builder()
                .baseUrl("https://openrouter.ai/api/v1")
                .apiKey(System.getenv("OPENROUTER_API_KEY"))
                .modelName("openrouter/free")
                .build();
    }

}
