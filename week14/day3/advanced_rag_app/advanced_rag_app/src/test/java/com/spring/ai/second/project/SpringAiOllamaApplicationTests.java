package com.spring.ai.second.project;

import com.spring.ai.second.project.helper.Helper;
import com.spring.ai.second.project.service.ChatService;
import com.spring.ai.second.project.service.DataLoader;
import com.spring.ai.second.project.service.DataTransformer;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;


@SpringBootTest
class SpringAiOllamaApplicationTests {

    @Autowired
    private ChatService chatService;

    @Autowired
    private DataLoader dataLoader;

    @Autowired
    private DataTransformer dataTransformer;

    // Use @Autowired to inject the REAL VectorStore bean
    @Autowired
    private VectorStore vectorStore;

    @Test
    void savedDataToVectorDatabase(){
        // This test will now save data to the real database
        this.chatService.saveData(Helper.getData());
        System.out.println("Data is saved successfully");
    }

    @Test
    void testDataLoader(){
        List<Document> documents = dataLoader.loadDocumentsFromJson();
        System.out.println(documents.size());

        documents.forEach(item->{
            System.out.println(item);
        });
    }

    @Test
    void testPdfLoaderAndSaveToDatabase(){
        System.out.println("Reading PDF data...");
        List<Document> documents = this.dataLoader.loadDocumentsFromPdf();
        System.out.println("PDF documents found: " + documents.size());

        System.out.println("Transforming documents...");
        var tranformedDocument = this.dataTransformer.transform(documents);
        System.out.println("Transformed documents: " + tranformedDocument.size());

        // This will now call the REAL database (pgvector)
        this.vectorStore.add(tranformedDocument);

        System.out.println("Data successfully saved to pgvector database!");
    }
}