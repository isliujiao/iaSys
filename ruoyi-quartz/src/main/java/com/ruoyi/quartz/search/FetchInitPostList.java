package com.ruoyi.quartz.search;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ruoyi.common.core.domain.entity.Post;
import com.ruoyi.system.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 获取初始帖子列表
 */
// 取消注释后，每次启动 springboot 项目时会执行一次 run 方法
//@Component
@Slf4j
public class FetchInitPostList implements CommandLineRunner {

    /**
     * 内容 字段最大容量
     */
    public static final Integer MAXIMUM_CONTENT = 16777215 - 1;

    /**
     * 获取第几页、多少条（改为可配置@Value）
     */
    public static final Integer GET_PAGE_CURRENT = 1;

    public static final Integer GET_PAGE_PAGESIZE = 5;

    @Autowired
    private PostService postService;

    /**
     * "@PostConstruct": 项目运行执行一次
     * 从网站获取初始帖子列表
     */
    @Override
    public void run(String... args) {
        log.info("--------获取初始帖子列表开始执行---------");
        // 1. 获取数据
        String json = "{\"current\": "+ GET_PAGE_CURRENT +", \"pageSize\": "+ GET_PAGE_PAGESIZE +", \"sortField\": \"createTime\", \"sortOrder\": \"descend\", \"category\": \"文章\",\"reviewStatus\": 1}";
        String url = "https://www.code-nav.cn/api/post/search/page/vo";
        String result = HttpRequest
                .post(url)
                .body(json)
                .execute()
                .body();
        // 2. json 转对象
        Map<String, Object> map = JSONUtil.toBean(result, Map.class);
        JSONObject data = (JSONObject) map.get("data");
        JSONArray records = (JSONArray) data.get("records");
        List<Post> postList = new ArrayList<>();
        for (Object record : records) {
            JSONObject tempRecord = (JSONObject) record;
            Post post = new Post();
            // todo 取值过程中,需要判空，插入的数据转为utf-8字符编码
            String title = strConUtf8Encod(tempRecord.getStr("title"));
            post.setTitle(title);

            String content = strConUtf8Encod(tempRecord.getStr("content"));
            post.setContent(StringUtils.substring(content, 0, MAXIMUM_CONTENT));

            JSONArray tags = (JSONArray) tempRecord.get("tags");
            List<String> tagList = tags.toList(String.class);
            post.setTags(JSONUtil.toJsonStr(tagList));
            post.setUserId(1L);
            postList.add(post);
        }
        // 3. 数据入库
        boolean saveFlag = postService.insertPostList(postList);
        if (saveFlag) {
            log.info("获取初始化帖子列表成功，条数 = {}", postList.size());
        } else {
            log.error("获取初始化帖子列表失败");
        }
    }

    /**
     * 将字符转为UTF-8编码
     * @param str 需要转换的字符
     * @return 转换后的字符
     */
    private String strConUtf8Encod(String str) {
        byte[] utf8Bytes = str.getBytes(StandardCharsets.UTF_8);
        return new String(utf8Bytes, StandardCharsets.UTF_8);
    }

}
