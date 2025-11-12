package com.spring.ai.second.project.service;

import org.springframework.ai.document.Document;

import java.util.List;

public interface DataLoader {

    List<Document> loadDocumentsFromJson();

    List<Document> loadDocumentsFromPdf();

}
