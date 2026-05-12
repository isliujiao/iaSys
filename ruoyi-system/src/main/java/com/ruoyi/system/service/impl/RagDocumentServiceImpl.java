package com.ruoyi.system.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruoyi.common.config.RagConfigProperties;
import com.ruoyi.common.core.domain.entity.RagDocument;
import com.ruoyi.common.core.domain.entity.RagDocumentChunk;
import com.ruoyi.system.mapper.RagDocumentChunkMapper;
import com.ruoyi.system.mapper.RagDocumentMapper;
import com.ruoyi.system.service.IRagDocumentService;
import com.ruoyi.system.service.RagEmbeddingService;
import com.ruoyi.system.service.RagVectorStoreService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RagDocumentServiceImpl implements IRagDocumentService {

    private final RagDocumentMapper ragDocumentMapper;
    private final RagDocumentChunkMapper ragDocumentChunkMapper;
    private final RagEmbeddingService embeddingService;
    private final RagVectorStoreService vectorStoreService;
    private final RagConfigProperties ragConfig;

    @Override
    public List<RagDocument> selectRagDocumentList(RagDocument ragDocument) {
        return ragDocumentMapper.selectRagDocumentList(ragDocument);
    }

    @Override
    public RagDocument selectRagDocumentById(Long id) {
        return ragDocumentMapper.selectRagDocumentById(id);
    }

    @Override
    @Transactional
    public RagDocument uploadDocument(String fileName, String filePath, String fileType, long fileSize, String createBy) {
        RagDocument doc = new RagDocument();
        doc.setFileName(fileName);
        doc.setFilePath(filePath);
        doc.setFileType(fileType);
        doc.setFileSize(fileSize);
        doc.setStatus("processing");
        doc.setChunkCount(0);
        doc.setCreateBy(createBy);
        ragDocumentMapper.insertRagDocument(doc);
        return doc;
    }

    @Override
    public void processDocument(Long documentId) {
        RagDocument doc = ragDocumentMapper.selectRagDocumentById(documentId);
        if (doc == null) {
            log.error("Document not found: {}", documentId);
            return;
        }
        try {
            doc.setStatus("processing");
            ragDocumentMapper.updateRagDocument(doc);

            String text = extractText(doc.getFilePath(), doc.getFileType());
            if (text == null || text.trim().isEmpty()) {
                updateStatus(documentId, "error", "无法提取文本内容");
                return;
            }

            List<String> chunks = splitText(text);
            int chunkCount = 0;

            for (int i = 0; i < chunks.size(); i++) {
                String chunk = chunks.get(i);
                String hash = sha256(chunk);

                List<Float> embedding = embeddingService.embed(chunk);
                if (embedding == null || embedding.isEmpty()) {
                    log.warn("Failed to embed chunk {} for doc {}, skipping", i, documentId);
                    continue;
                }

                String esDocId = vectorStoreService.indexChunk(documentId, i, chunk, embedding);
                if (esDocId == null) {
                    log.warn("Failed to index chunk {} for doc {} in ES", i, documentId);
                }

                RagDocumentChunk chunkEntity = new RagDocumentChunk();
                chunkEntity.setDocumentId(documentId);
                chunkEntity.setChunkIndex(i);
                chunkEntity.setContent(chunk);
                chunkEntity.setContentHash(hash);
                chunkEntity.setEsDocId(esDocId);
                ragDocumentChunkMapper.insertRagDocumentChunk(chunkEntity);

                chunkCount++;
            }

            doc.setChunkCount(chunkCount);
            doc.setStatus("ready");
            ragDocumentMapper.updateRagDocument(doc);
            log.info("Document {} processed: {} chunks indexed", documentId, chunkCount);
        } catch (Exception e) {
            log.error("Failed to process document {}", documentId, e);
            updateStatus(documentId, "error", e.getMessage());
        }
    }

    @Override
    @Transactional
    public int deleteDocument(Long id) {
        // Delete ES vectors
        vectorStoreService.deleteByDocumentId(id);
        // Delete chunks from DB
        ragDocumentChunkMapper.deleteChunksByDocumentId(id);
        // Delete document record
        return ragDocumentMapper.deleteRagDocumentById(id);
    }

    private String extractText(String filePath, String fileType) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            log.error("File not found: {}", filePath);
            return null;
        }
        switch (fileType.toLowerCase()) {
            case "txt":
                return new String(java.nio.file.Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
            case "pdf":
                return extractPdfText(file);
            case "docx":
                return extractDocxText(file);
            case "xlsx":
                return extractXlsxText(file);
            case "pptx":
                return extractPptxText(file);
            default:
                log.warn("Unsupported file type: {}", fileType);
                return null;
        }
    }

    private String extractPdfText(File file) throws IOException {
        try (PDDocument document = PDDocument.load(file)) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            return stripper.getText(document);
        }
    }

    private String extractDocxText(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file);
             XWPFDocument document = new XWPFDocument(fis)) {
            StringBuilder sb = new StringBuilder();
            document.getParagraphs().forEach(p -> sb.append(p.getText()).append("\n"));
            document.getTables().forEach(table ->
                    table.getRows().forEach(row ->
                            row.getTableCells().forEach(cell ->
                                    sb.append(cell.getText()).append("\t"))));
            return sb.toString();
        }
    }

    private String extractXlsxText(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file);
             XSSFWorkbook workbook = new XSSFWorkbook(fis)) {
            StringBuilder sb = new StringBuilder();
            workbook.forEach(sheet -> {
                sb.append("【").append(sheet.getSheetName()).append("】\n");
                sheet.forEach(row -> {
                    row.forEach(cell -> {
                        switch (cell.getCellType()) {
                            case STRING:
                                sb.append(cell.getStringCellValue()).append("\t");
                                break;
                            case NUMERIC:
                                sb.append(cell.getNumericCellValue()).append("\t");
                                break;
                            case BOOLEAN:
                                sb.append(cell.getBooleanCellValue()).append("\t");
                                break;
                            default:
                                sb.append("\t");
                        }
                    });
                    sb.append("\n");
                });
            });
            return sb.toString();
        }
    }

    private String extractPptxText(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file);
             XMLSlideShow slideshow = new XMLSlideShow(fis)) {
            StringBuilder sb = new StringBuilder();
            for (XSLFSlide slide : slideshow.getSlides()) {
                sb.append("--- Slide ---\n");
                slide.getShapes().forEach(shape -> {
                    if (shape instanceof org.apache.poi.xslf.usermodel.XSLFTextShape) {
                        org.apache.poi.xslf.usermodel.XSLFTextShape textShape =
                                (org.apache.poi.xslf.usermodel.XSLFTextShape) shape;
                        sb.append(textShape.getText()).append("\n");
                    }
                });
            }
            return sb.toString();
        }
    }

    List<String> splitText(String text) {
        int chunkSize = ragConfig.getChunkSize();
        int chunkOverlap = ragConfig.getChunkOverlap();
        List<String> chunks = new ArrayList<>();

        for (int start = 0; start < text.length(); start += (chunkSize - chunkOverlap)) {
            int end = Math.min(start + chunkSize, text.length());
            chunks.add(text.substring(start, end));
            if (end == text.length()) {
                break;
            }
        }
        return chunks;
    }

    private void updateStatus(Long documentId, String status, String errorMsg) {
        RagDocument doc = new RagDocument();
        doc.setId(documentId);
        doc.setStatus(status);
        doc.setErrorMsg(errorMsg);
        ragDocumentMapper.updateRagDocument(doc);
    }

    private static String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            return Integer.toHexString(input.hashCode());
        }
    }
}
