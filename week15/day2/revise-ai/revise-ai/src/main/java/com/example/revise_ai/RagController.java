package com.example.revise_ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/rag")
public class RagController {

    private final VectorStore vectorStore;
    private final ChatClient chatClient;

    public RagController(VectorStore vectorStore,ChatClient.Builder builder)
    {
        this.vectorStore=vectorStore;
        this.chatClient=builder.build();
    }


    @PostMapping("/product")
    public List<Document> getProducts(@RequestParam String text)
    {
        return vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(text)
                        .topK(2)
                        .build()
        );
    }

    @PostMapping("/ask")
    public String getAnswerUsingRag(@RequestParam String query)
    {
        return chatClient.prompt(query)
                .advisors(new QuestionAnswerAdvisor(vectorStore))
                .call()
                .content();
    }
}
