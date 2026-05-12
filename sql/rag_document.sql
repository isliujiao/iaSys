-- ----------------------------
-- RAG 知识库文档管理
-- ----------------------------

-- 文档元数据表
DROP TABLE IF EXISTS `rag_document`;
CREATE TABLE `rag_document` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '文档ID',
  `file_name` varchar(255) NOT NULL COMMENT '原始文件名',
  `file_path` varchar(500) NOT NULL COMMENT '文件存储路径',
  `file_size` bigint DEFAULT 0 COMMENT '文件大小(字节)',
  `file_type` varchar(20) DEFAULT NULL COMMENT '文件类型(pdf/docx/xlsx/txt)',
  `status` varchar(20) NOT NULL DEFAULT 'processing' COMMENT '状态(processing/ready/error)',
  `chunk_count` int DEFAULT 0 COMMENT '分块数量',
  `error_msg` varchar(1000) DEFAULT NULL COMMENT '错误信息',
  `create_by` varchar(64) DEFAULT '' COMMENT '上传人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='RAG知识库文档';

-- 文档文本块表
DROP TABLE IF EXISTS `rag_document_chunk`;
CREATE TABLE `rag_document_chunk` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '块ID',
  `document_id` bigint NOT NULL COMMENT '关联文档ID',
  `chunk_index` int NOT NULL DEFAULT 0 COMMENT '块序号(从0开始)',
  `content` text NOT NULL COMMENT '文本内容',
  `content_hash` varchar(64) DEFAULT NULL COMMENT '内容哈希(用于去重)',
  `es_doc_id` varchar(100) DEFAULT NULL COMMENT 'ES中对应向量文档ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_document_id` (`document_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='RAG文档文本块';
