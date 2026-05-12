package com.ruoyi.framework.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里云 OSS 配置
 * 当 access-key 为占位值时不创建真实客户端（返回 null，SysProfileController 通过 @Autowired(required=false) 处理）
 */
@Configuration
public class OssConfig {

    private static final Logger log = LoggerFactory.getLogger(OssConfig.class);

    @Value("${spring.cloud.alicloud.access-key:your-access-key}")
    private String accessKey;

    @Value("${spring.cloud.alicloud.secret-key:your-secret-key}")
    private String secretKey;

    @Value("${spring.cloud.alicloud.oss.endpoint:oss-cn-hangzhou.aliyuncs.com}")
    private String endpoint;

    @Bean
    public OSS ossClient() {
        if ("your-access-key".equals(accessKey) || accessKey.isBlank()
                || "your-secret-key".equals(secretKey) || secretKey.isBlank()) {
            log.warn("OSS AccessKey 未配置，OSS 客户端将不可用（头像上传功能需配置后方可使用）");
            return null;
        }
        log.info("初始化 OSS 客户端，endpoint={}", endpoint);
        return new OSSClientBuilder().build(endpoint, accessKey, secretKey);
    }
}
