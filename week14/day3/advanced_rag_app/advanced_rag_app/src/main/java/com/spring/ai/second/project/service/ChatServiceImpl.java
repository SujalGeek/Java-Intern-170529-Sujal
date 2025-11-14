package com.spring.ai.second.project.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatServiceImpl implements ChatService {

    private final ChatClient chatClient;

    @Autowired
    private VectorStore vectorStore;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public ChatServiceImpl(@Qualifier("ollamaChatClient") ChatClient chatClient,
                           VectorStore vectorStore) {
        this.chatClient = chatClient;
        this.vectorStore = vectorStore;
    }

    /**
     * Main RAG chat method
     */
    @Override
    public String chatTemplate(String query, String userId) {

        // Vector Retrieval Configuration
        var retriever = VectorStoreDocumentRetriever.builder()
                .vectorStore(vectorStore)
                .topK(5)
                .similarityThreshold(0.75)   // FIXED: Only retrieve strong matches
                .build();

        // RAG Advisor
        var advisor = RetrievalAugmentationAdvisor.builder()
                .documentRetriever(retriever)
                // Removed ContextualQueryAugmenter because it caused safety hallucinations
                .build();

        // PROMPT PIPELINE
        return chatClient
                .prompt()
                .system("You are a helpful assistant. Use the retrieved context provided by the system advisor. " +
                        "If context is empty, say: 'No matching information found.' Do not hallucinate.")
                .advisors(advisor)
                .user(query)
                .call()
                .content();
    }

    /**
     * Streaming Chat Method
     */
    @Override
    public Flux<String> streamChat(String query) {

        var retriever = VectorStoreDocumentRetriever.builder()
                .vectorStore(vectorStore)
                .topK(5)
                .similarityThreshold(0.75)
                .build();

        var advisor = RetrievalAugmentationAdvisor.builder()
                .documentRetriever(retriever)
                .build();

        return chatClient
                .prompt()
                .system("You are a helpful assistant. Use retrieved documents when useful.")
                .advisors(advisor)
                .user(query)
                .stream()
                .content();
    }

    /**
     * Public API Wrapper
     */
    @Override
    public String queryResponse(String userQuery) {
        return chatTemplate(userQuery, null);
    }

    /**
     * Save Data to Vector Store
     */
    @Override
    public void saveData(List<String> list) {
        List<Document> docs = list.stream()
                .map(Document::new)
                .collect(Collectors.toList());

        vectorStore.add(docs);
        logger.info("Documents added to vector store: {}", docs.size());
    }
}
