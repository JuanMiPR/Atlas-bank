package com.atlas.bank.atlas_bank.infrastructure.config;

import com.atlas.bank.atlas_bank.infrastructure.adapter.in.ai.AtlasBankTools;
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
                        "You are an AI Agent for Atlas Bank"
                                + "Help customer to interact with their accounts"
                                + "Response always with the language that the user has request information"
                )
                .defaultTools(tools)
                .build();
    }
}
