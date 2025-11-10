package com.spring.ai.second.project;

import com.spring.ai.second.project.service.ChatService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SpringAiOllamaApplicationTests {

    @Autowired
    private ChatService chatService;
	@Test
	void contextLoads() {
	}

    @Test
    void testTemplateRender(){
        System.out.println("Template Renderer");
//        var output = this.chatService.chatTemplate();
//        System.out.println(output);
    }

}
