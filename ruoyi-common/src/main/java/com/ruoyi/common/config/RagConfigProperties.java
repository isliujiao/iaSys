package com.ruoyi.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "rag")
public class RagConfigProperties {

    private String chatModel = "qwen-plus";
    private String embeddingModel = "text-embedding-v3";
    private int maxResults = 5;
    private double minScore = 0.6;
    private int chunkSize = 500;
    private int chunkOverlap = 50;
    private int embeddingDimensions = 1024;
    private String indexName = "rag_documents";
}
