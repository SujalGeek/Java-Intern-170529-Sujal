package com.spring.ai.second.project.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.JsonReader;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DataLoaderImpl implements DataLoader{

    @Value("classpath:sample_data.json")
    private Resource jsonResource;

    @Value("classpath:cricket_rules.pdf")
    private Resource pdfResource;

    @Override
    public List<Document> loadDocumentsFromJson() {
        System.out.println("Started loading from json");
       var jsonReader = new JsonReader(jsonResource);
      List<Document> listDocuments = jsonReader.read();
       return listDocuments;
    }

    @Override
    public List<Document> loadDocumentsFromPdf() {

        PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(
                pdfResource,
                PdfDocumentReaderConfig.builder()
                        .withPageTopMargin(0)
                        .withPageExtractedTextFormatter(
                                ExtractedTextFormatter.builder()
                                        .withNumberOfTopTextLinesToDelete(0)
                                        .build()
                        )
                        .build()
        );

        return pdfReader.get();
    }
}
