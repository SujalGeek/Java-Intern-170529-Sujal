package com.example.revise_ai;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPooled;

@Configuration
public class RedisConfig {

    @Bean
    public JedisPooled jedisPooled(){
    return new JedisPooled("localhost",6379);
    }

    @Bean
    public VectorStore vectorStore(JedisPooled jedisPooled,  @Qualifier("ollamaEmbeddingModel") EmbeddingModel embeddingModel)
    {
        return RedisVectorStore.builder(jedisPooled,embeddingModel)
                .indexName("product-index")
                .initializeSchema(true)
                .build();
//        docker exec -it redis-vector redis-cli
//        FT._LIST
    }
}
