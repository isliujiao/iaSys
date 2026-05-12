package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.common.core.domain.entity.RagDocument;

public interface RagDocumentMapper {

    List<RagDocument> selectRagDocumentList(RagDocument ragDocument);

    RagDocument selectRagDocumentById(Long id);

    int insertRagDocument(RagDocument ragDocument);

    int updateRagDocument(RagDocument ragDocument);

    int deleteRagDocumentById(Long id);
}
