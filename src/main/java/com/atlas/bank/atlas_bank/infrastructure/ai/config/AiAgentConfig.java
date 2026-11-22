package com.atlas.bank.atlas_bank.infrastructure.ai.config;

import com.atlas.bank.atlas_bank.infrastructure.ai.adapter.in.AtlasBankTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiAgentConfig {
    @Bean
    public ChatClient chatClient(
            ChatClient.Builder builder,
            AtlasBankTools tools
    ) {

        return builder
                .defaultSystem(
                        "You are an AI Agent for Atlas Bank. "
                                + "Help customers to interact with their accounts. "
                                + "Always respond in the language the user wrote in."
                )
                .defaultTools(tools)
                .build();
    }
}
