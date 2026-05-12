package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.common.core.domain.entity.RagDocumentChunk;

public interface RagDocumentChunkMapper {

    List<RagDocumentChunk> selectChunksByDocumentId(Long documentId);

    int insertRagDocumentChunk(RagDocumentChunk chunk);

    int deleteChunksByDocumentId(Long documentId);
}
