package com.ruoyi.system.service;

import java.util.List;

import com.ruoyi.common.core.domain.entity.RagDocument;

public interface IRagDocumentService {

    List<RagDocument> selectRagDocumentList(RagDocument ragDocument);

    RagDocument selectRagDocumentById(Long id);

    RagDocument uploadDocument(String fileName, String filePath, String fileType, long fileSize, String createBy);

    void processDocument(Long documentId);

    int deleteDocument(Long id);
}
