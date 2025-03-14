package com.ruoyi.common.utils;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.ruoyi.common.core.domain.TyqwMsg;
import com.ruoyi.common.core.domain.TyqwReqBody;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.dto.ChatMsgVO;
import com.ruoyi.common.enums.chat.ChatMsgType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * @author liujiao
 * @date 2025/3/14 14:53
 */
@Slf4j
@Component
public class ChatToAIUtils {
    private static String tyqwApiKey;

    @Autowired
    public ChatToAIUtils(Environment environment) {
        // 获取配置文件中的 tyqw.apiKey 配置
        tyqwApiKey = environment.getProperty("tyqw.apiKey");
    }

    /**
     * 向百炼-通义千问请求消息（AI-ECharts图表）
     *
     * @param question
     */
    public static String askTyqwEcharts(String question) {
        log.info("---------开始向百炼-通义千问发送消息---------");
        // 如果是无效字符串，则不对大模型进行请求
        if (StringUtils.isBlank(question)) {
            throw new IllegalArgumentException("无效的消息内容");
        }

        try {
            // 创建请求体
            TyqwReqBody requestBody = new TyqwReqBody(
                    "qwen-plus",
                    new TyqwMsg[]{
                            new TyqwMsg("system", "你是一个数据分析师和前端开发专家，接下来我会按照以下固定格式给你提供内容：\n" +
                                    "分析需求：\n" +
                                    "{数据分析的需求或者目标}\n" +
                                    "原始数据：\n" +
                                    "{csv格式的原始数据，用,作为分隔符}\n" +
                                    "请根据这两部分内容，按照以下指定格式生成内容（此外不要输出任何多余的开头、结尾、注释）\n" +
                                    "【【【【【\n" +
                                    "{前端 Echarts V5 的 option 配置对象json代码（一定要符合json格式），合理地将数据进行可视化，不要生成任何多余的内容（比如注释、转义符、换行符等）}\n" +
                                    "【【【【【\n" +
                                    "{明确的数据分析结论、越详细越好，不要生成多余的注释}"),
                            new TyqwMsg("user", question)
                    }
            );

            // 将请求体转换为 JSON
            Gson gson = new Gson();
            String jsonInputString = gson.toJson(requestBody);
            // 创建 URL 对象
            URL url = new URL("https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            // 设置请求方法为 POST
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "application/json; utf-8");
            httpURLConnection.setRequestProperty("Accept", "application/json");
            // 若没有配置环境变量，请用百炼API Key将下行替换为：String apiKey = "sk-xxx";
            String apiKey = tyqwApiKey;
            String auth = "Bearer " + apiKey;
            httpURLConnection.setRequestProperty("Authorization", auth);
            // 启用输入输出流
            httpURLConnection.setDoOutput(true);
            // 写入请求体
            try (OutputStream os = httpURLConnection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            // 获取响应码
            int responseCode = httpURLConnection.getResponseCode();
            System.out.println("Response Code: " + responseCode);
            // 读取响应体
            try (BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                // 响应大模型的答案
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                log.info("【百炼-通义千问响应结果：{}】", response);
                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = mapper.readTree(response.toString());
                String content = rootNode.path("choices").get(0).path("message").path("content").asText();
                return JSONUtil.parseObj(content).toStringPretty();
            }
        } catch (Exception e) {
            log.error("错误：" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * 向百炼-通义千问请求消息（聊天室）
     *
     * @param question
     */
    public static String askTyqwGpt(String question) {
        log.info("---------开始向百炼-通义千问发送消息---------");
        // 如果是无效字符串，则不对大模型进行请求
        if (StringUtils.isBlank(question)) {
            throw new IllegalArgumentException("无效的消息内容");
        }

        try {
            // 创建请求体
            TyqwReqBody requestBody = new TyqwReqBody(
                    "qwen-plus",
                    new TyqwMsg[]{
                            new TyqwMsg("system", "You are a helpful assistant.name is '小A'"),
                            new TyqwMsg("user", question)
                    }
            );

            // 将请求体转换为 JSON
            Gson gson = new Gson();
            String jsonInputString = gson.toJson(requestBody);
            // 创建 URL 对象
            URL url = new URL("https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            // 设置请求方法为 POST
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "application/json; utf-8");
            httpURLConnection.setRequestProperty("Accept", "application/json");
            // 若没有配置环境变量，请用百炼API Key将下行替换为：String apiKey = "sk-xxx";
            String apiKey = tyqwApiKey;
            String auth = "Bearer " + apiKey;
            httpURLConnection.setRequestProperty("Authorization", auth);
            // 启用输入输出流
            httpURLConnection.setDoOutput(true);
            // 写入请求体
            try (OutputStream os = httpURLConnection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            // 获取响应码
            int responseCode = httpURLConnection.getResponseCode();
            System.out.println("Response Code: " + responseCode);
            // 读取响应体
            try (BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                // 响应大模型的答案
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                log.info("【百炼-通义千问响应结果：{}】", response);
                return response.toString();
            }
        } catch (Exception e) {
            log.error("错误：" + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
