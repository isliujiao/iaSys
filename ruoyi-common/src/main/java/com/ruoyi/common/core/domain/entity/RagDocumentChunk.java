package com.ruoyi.common.core.domain.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

@Data
public class RagDocumentChunk implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long documentId;
    private Integer chunkIndex;
    private String content;
    private String contentHash;
    private String esDocId;
    private Date createTime;
}
