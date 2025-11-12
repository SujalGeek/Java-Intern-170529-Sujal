package com.spring.ai.second.project.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DataTransformerImpl implements DataTransformer{
    @Override
    public List<Document> transform(List<Document> documents) {

        var splitters = new TokenTextSplitter(300,400,10,5000,true);

        List<Document> transformed = splitters.transform(documents);
        return transformed;
    }
}
