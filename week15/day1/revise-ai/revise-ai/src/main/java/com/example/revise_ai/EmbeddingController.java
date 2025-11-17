package com.example.revise_ai;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/embeddings")
public class EmbeddingController {

    private final EmbeddingModel embeddingModel;

    public EmbeddingController(@Qualifier("ollamaEmbeddingModel") EmbeddingModel embeddingModel)
    {
        this.embeddingModel = embeddingModel;
    }

    @GetMapping
    public float[] embed(@RequestParam String text)
    {
        return embeddingModel.embed(text);
//        return  List.of(response);
    }

    @PostMapping("/api/similarity")
    public double getSimilarity(@RequestParam String text1,String text2)
    {
        float[] embedding1 = embeddingModel.embed(text1);
        float[] embedding2 = embeddingModel.embed(text2);

        double dotProduct = 0;
        double norm1 = 0;
        double norm2 = 0;

        for(int i=0;i<embedding1.length;i++)
        {
            dotProduct+=embedding1[i]*embedding2[i];
            norm1 += Math.pow(embedding1[i],2);
            norm2 += Math.pow(embedding2[i],2);
        }

        return dotProduct*100/(Math.sqrt(norm1)*Math.sqrt(norm2));
    }
}
