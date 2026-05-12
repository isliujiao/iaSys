package com.ruoyi.common.core.domain.entity;

import java.io.Serializable;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class RagDocument implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String fileName;
    private String filePath;
    private Long fileSize;
    private String fileType;
    private String status;
    private Integer chunkCount;
    private String errorMsg;
    private String createBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}
