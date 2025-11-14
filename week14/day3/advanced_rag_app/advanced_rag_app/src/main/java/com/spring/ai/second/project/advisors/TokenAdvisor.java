package com.spring.ai.second.project.advisors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
@ConditionalOnProperty(
        value = "advisor.token.enabled",
        havingValue = "true",
        matchIfMissing = false
)
public class TokenAdvisor implements CallAdvisor, StreamAdvisor {

    private static final Logger logger = LoggerFactory.getLogger(TokenAdvisor.class);

    /**
     * Logs request & response WITHOUT modifying the prompt.
     */
    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest,
                                         CallAdvisorChain callAdvisorChain) {

        logger.info("=== TokenAdvisor: Incoming Request ===");
        logger.info("Prompt Messages: {}", chatClientRequest.prompt().getContents());

        // ***** IMPORTANT: Do NOT modify the request *****
        ChatClientResponse response = callAdvisorChain.nextCall(chatClientRequest);

        logger.info("=== TokenAdvisor: Model Response ===");
        logger.info("Response Text: {}", response.chatResponse().getResult().getOutput().getText());

        try {
            logger.info("Total Tokens Used: {}",
                    response.chatResponse().getMetadata().getUsage().getTotalTokens());
        } catch (Exception e) {
            logger.warn("Token usage information not available.");
        }

        return response;  // RETURN UNCHANGED RESPONSE
    }

    /**
     * Logs streaming responses (no modification).
     */
    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest chatClientRequest,
                                                 StreamAdvisorChain streamAdvisorChain) {

        logger.info("=== TokenAdvisor: Streaming Request ===");
        logger.info("Prompt Messages: {}", chatClientRequest.prompt().getContents());

        // Pass through without modification
        return streamAdvisorChain.nextStream(chatClientRequest)
                .doOnNext(resp -> {
                    logger.info("Stream chunk received: {}",
                            resp.chatResponse().getResult().getOutput().getText());
                });
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
