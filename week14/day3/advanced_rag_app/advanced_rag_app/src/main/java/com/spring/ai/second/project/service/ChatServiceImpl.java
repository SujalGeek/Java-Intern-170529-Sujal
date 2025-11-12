package com.spring.ai.second.project.service;

import com.spring.ai.second.project.entity.Tut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.preretrieval.query.expansion.MultiQueryExpander;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.ai.rag.retrieval.join.ConcatenationDocumentJoiner;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;


import javax.swing.event.UndoableEditListener;
import javax.swing.text.*;
import java.util.List;
import java.util.Map;
import org.springframework.ai.document.Document;
import java.util.stream.Collectors;

@Service
public class ChatServiceImpl implements ChatService {

    private ChatClient chatClient;

    @Value("classpath:/prompts/user-message.st")
    private Resource userMessage;

    @Value("classpath:/prompts/system-message.st")
    private Resource systemMessage;

    @Autowired
    private VectorStore vectorStore;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public ChatServiceImpl(@Qualifier("ollamaChatClient") ChatClient chatClient, VectorStore vectorStore) {
        this.chatClient = chatClient;
        this.vectorStore = vectorStore;
    }

    public String chatTemplate(String query, String userId) {
      // load data from the database
        // similar result user query

        // pass in context
//        SearchRequest searchRequest = SearchRequest.builder()
//                .topK(3)
//                .similarityThreshold(0.6)
//                .query(query)
//                .build();
//
//       List<Document> documents = this.vectorStore.similaritySearch(searchRequest);
//        List<String> documentList = documents.stream().map(Document::getText).toList();
//      String contextData = String.join(" , ",documentList);
//        logger.info("Context Data: {}",contextData);

        var advisor = RetrievalAugmentationAdvisor.builder()
                .documentRetriever(VectorStoreDocumentRetriever
                        .builder()
                        .vectorStore(vectorStore)
                        .topK(3)
                        .similarityThreshold(0.5)
                        .build())
                .queryAugmenter(ContextualQueryAugmenter.builder().allowEmptyContext(true).build())
                .build();


        return this.chatClient
                    .prompt()
                .advisors(advisor)

//                .system(system -> system.text(this.systemMessage).param("documents",contextData))
//                .advisors(new QuestionAnswerAdvisor(vectorStore))
//                .advisors(QuestionAnswerAdvisor.builder(vectorStore).searchRequest(SearchRequest.builder().topK(3).similarityThreshold(0.5).build()).build())
                .user(user -> user.text(this.userMessage).param("query", query))
                .call()
                .content();
    }

    @Override
    public Flux<String> streamChat(String query) {

        return this.chatClient.prompt()
                .system(system -> system.text(this.systemMessage))
                .user(user -> user.text(this.userMessage).param("concept", query))
                .stream()
                .content();
        //return null;
    }

    @Override
    public String queryResponse(String userQuery) {

        var advisor = RetrievalAugmentationAdvisor.builder()
                .queryTransformers(
                        RewriteQueryTransformer.builder().chatClientBuilder(chatClient.mutate().clone())
                                .build()
                )
                .documentRetriever(
                        VectorStoreDocumentRetriever.builder()
                                .vectorStore(vectorStore)
                                .topK(3)
                                .similarityThreshold(0.3)
                                .build()
                )
                .queryExpander(MultiQueryExpander.builder().chatClientBuilder(chatClient.mutate().clone()).build())
                .documentJoiner(new ConcatenationDocumentJoiner())
                .queryAugmenter(ContextualQueryAugmenter.builder().build())
//                .documentPostProcessors()
                .build();

        return chatClient.prompt()
                .advisors(advisor)
                .user(userQuery)
                .call()
                .content();
    }

    @Override
    public void saveData(List<String> list) {

        List<Document> documentList = list.stream().map(Document::new).collect(Collectors.toList());
        this.vectorStore.add(documentList);
    }

}
