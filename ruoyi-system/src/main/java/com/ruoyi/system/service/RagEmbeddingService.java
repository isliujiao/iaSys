package com.ruoyi.system.service;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.config.RagConfigProperties;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RagEmbeddingService {

    @Value("${tyqw.apiKey}")
    private String apiKey;

    private final RagConfigProperties ragConfig;

    private static final String EMBEDDING_URL = "https://dashscope.aliyuncs.com/api/v1/services/embeddings/text-embedding/text-embedding";

    public List<Float> embed(String text) {
        try {
            JSONObject body = new JSONObject();
            body.put("model", ragConfig.getEmbeddingModel());
            JSONObject input = new JSONObject();
            JSONArray texts = new JSONArray();
            texts.add(text);
            input.put("texts", texts);
            body.put("input", input);

            HttpURLConnection conn = (HttpURLConnection) URI.create(EMBEDDING_URL).toURL().openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(60000);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(body.toJSONString().getBytes(StandardCharsets.UTF_8));
                os.flush();
            }

            String response = new String(conn.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            JSONObject respJson = JSON.parseObject(response);
            JSONObject output = respJson.getJSONObject("output");
            if (output == null) {
                log.error("Embedding response missing output: {}", response);
                return null;
            }
            JSONArray embeddings = output.getJSONArray("embeddings");
            if (embeddings == null || embeddings.isEmpty()) {
                log.error("Embedding response missing embeddings array: {}", response);
                return null;
            }
            JSONArray embeddingArray = embeddings.getJSONObject(0).getJSONArray("embedding");
            List<Float> result = new ArrayList<>(embeddingArray.size());
            for (int i = 0; i < embeddingArray.size(); i++) {
                result.add(embeddingArray.getFloat(i));
            }
            return result;
        } catch (Exception e) {
            log.error("Failed to get embedding for text", e);
            return null;
        }
    }
}
