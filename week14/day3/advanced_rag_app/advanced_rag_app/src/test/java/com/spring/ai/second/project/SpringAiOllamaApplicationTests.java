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

//    @Autowired
//    private ChatService chatService;
//	@Test
//	void contextLoads() {
//
//	}
//
//    @Test
//    void testTemplateRender(){
//        System.out.println("Template Renderer");
////        var output = this.chatService.chatTemplate();
////        System.out.println(output);
//    }

        @Autowired
        private ChatService chatService;

        @Autowired
        private DataLoader dataLoader;

        @Autowired
        private DataTransformer dataTransformer;

        @Autowired
        private VectorStore vectorStore;

        @Test
        void savedDataToVectorDatabase(){
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
        void testPdfLoader(){
            List<Document> documents = this.dataLoader.loadDocumentsFromPdf();

            System.out.println(documents.size());
        documents.forEach(item->{
            System.out.println(item);
            System.out.println("-----------------------");
        });

            System.out.println("Data is read now going to transform");
          var tranformedDocument = this.dataTransformer.transform(documents);
            System.out.println(tranformedDocument.size());
        // going to save to db;

            this.vectorStore.add(tranformedDocument);
            System.out.println("done");
        }
}   
