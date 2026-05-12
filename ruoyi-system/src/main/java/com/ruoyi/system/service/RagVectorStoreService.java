package com.ruoyi.system.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.ScriptScoreQueryBuilder;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.xcontent.XContentBuilder;
import org.elasticsearch.xcontent.XContentFactory;
import org.springframework.stereotype.Service;

import com.ruoyi.common.config.RagConfigProperties;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * RAG 向量存储服务（基于 Elasticsearch 7.x RestHighLevelClient）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RagVectorStoreService {

    private final RestHighLevelClient restHighLevelClient;
    private final RagConfigProperties ragConfig;

    /**
     * 检查索引是否存在
     */
    public boolean indexExists() {
        try {
            GetIndexRequest request = new GetIndexRequest(ragConfig.getIndexName());
            return restHighLevelClient.indices().exists(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error("Failed to check index existence", e);
            return false;
        }
    }

    /**
     * 如果索引不存在则创建
     */
    public boolean createIndexIfNotExists() {
        try {
            if (indexExists()) {
                return true;
            }
            CreateIndexRequest request = new CreateIndexRequest(ragConfig.getIndexName());
            request.settings(Settings.builder()
                    .put("number_of_shards", 1)
                    .put("number_of_replicas", 0));

            XContentBuilder mappings = XContentFactory.jsonBuilder();
            mappings.startObject();
            {
                mappings.startObject("properties");
                {
                    mappings.startObject("document_id").field("type", "long").endObject();
                    mappings.startObject("chunk_index").field("type", "integer").endObject();
                    mappings.startObject("content").field("type", "text").field("analyzer", "standard").endObject();
                    mappings.startObject("embedding")
                            .field("type", "dense_vector")
                            .field("dims", ragConfig.getEmbeddingDimensions())
                            .endObject();
                }
                mappings.endObject();
            }
            mappings.endObject();
            request.mapping(mappings);

            restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
            log.info("Created ES index: {}", ragConfig.getIndexName());
            return true;
        } catch (Exception e) {
            log.error("Failed to create ES index: {}", ragConfig.getIndexName(), e);
            return false;
        }
    }

    /**
     * 索引文档分块
     */
    public String indexChunk(Long documentId, int chunkIndex, String content, List<Float> embedding) {
        try {
            createIndexIfNotExists();
            Map<String, Object> doc = new HashMap<>();
            doc.put("document_id", documentId);
            doc.put("chunk_index", chunkIndex);
            doc.put("content", content);
            doc.put("embedding", embedding);

            IndexRequest indexRequest = new IndexRequest(ragConfig.getIndexName()).source(doc);
            IndexResponse response = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
            return response.getId();
        } catch (Exception e) {
            log.error("Failed to index chunk doc_id={} chunk={}", documentId, chunkIndex, e);
            return null;
        }
    }

    /**
     * 按文档ID删除所有相关向量
     */
    public void deleteByDocumentId(Long documentId) {
        try {
            if (!indexExists()) {
                return;
            }
            DeleteByQueryRequest request =
                    new DeleteByQueryRequest(ragConfig.getIndexName());
            request.setQuery(QueryBuilders.termQuery("document_id", documentId));
            restHighLevelClient.deleteByQuery(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error("Failed to delete vectors for document {}", documentId, e);
        }
    }

    /**
     * 向量相似度检索（使用 script_score + cosineSimilarity）
     */
    public List<Map<String, Object>> search(List<Float> queryEmbedding, int topK, double minScore) {
        List<Map<String, Object>> results = new ArrayList<>();
        try {
            if (!indexExists()) {
                log.warn("ES index {} not exists, skip vector search", ragConfig.getIndexName());
                return results;
            }

            // 构建 script_score 查询
            Map<String, Object> params = new HashMap<>();
            params.put("query_vector", queryEmbedding);

            Script script = new Script(
                    ScriptType.INLINE,
                    "painless",
                    "cosineSimilarity(params.query_vector, 'embedding') + 1.0",
                    params
            );

            BoolQueryBuilder filter = QueryBuilders.boolQuery()
                    .must(QueryBuilders.existsQuery("embedding"));

            ScriptScoreQueryBuilder scriptScoreQuery = QueryBuilders.scriptScoreQuery(filter, script);

            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.query(scriptScoreQuery);
            sourceBuilder.size(topK);
            sourceBuilder.fetchSource(new String[]{"document_id", "chunk_index", "content"}, null);

            SearchRequest searchRequest = new SearchRequest(ragConfig.getIndexName());
            searchRequest.source(sourceBuilder);

            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            for (SearchHit hit : response.getHits().getHits()) {
                // cosineSimilarity 返回值域为 [-1,1]，加1后为 [0,2]，需折算回 [0,1]
                double score = (hit.getScore() - 1.0);
                if (score < minScore) {
                    continue;
                }
                Map<String, Object> result = new HashMap<>();
                result.put("score", score);
                result.put("esDocId", hit.getId());
                if (hit.getSourceAsMap() != null) {
                    result.putAll(hit.getSourceAsMap());
                }
                results.add(result);
            }
        } catch (Exception e) {
            log.error("Vector search failed", e);
        }
        return results;
    }
}
