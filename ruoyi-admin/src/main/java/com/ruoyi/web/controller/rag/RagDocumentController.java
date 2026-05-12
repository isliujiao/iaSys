package com.ruoyi.web.controller.rag;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.RagDocument;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.system.service.IRagDocumentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/rag/document")
@RequiredArgsConstructor
public class RagDocumentController extends BaseController {

    private final IRagDocumentService ragDocumentService;

    @GetMapping("/list")
    public TableDataInfo list(RagDocument ragDocument) {
        startPage();
        List<RagDocument> list = ragDocumentService.selectRagDocumentList(ragDocument);
        return getDataTable(list);
    }

    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id) {
        return success(ragDocumentService.selectRagDocumentById(id));
    }

    @PreAuthorize("@ss.hasRole('admin')")
    @PostMapping("/upload")
    public AjaxResult upload(@RequestParam("file") MultipartFile file) {
        try {
            // 获取基础上传路径
            String uploadBasePath = RuoYiConfig.getUploadPath();
            String ragDir = uploadBasePath + "/rag";
            
            // 上传文件（FileUploadUtils.upload返回的是带/profile前缀的URL相对路径，用于前端访问）
            String urlPath = FileUploadUtils.upload(ragDir, file);

            // Get file extension
            String originalName = file.getOriginalFilename();
            String fileType = originalName != null && originalName.contains(".")
                    ? originalName.substring(originalName.lastIndexOf(".") + 1).toLowerCase()
                    : "";

            // 构建真实的绝对路径用于后续文本提取（去掉URL路径中的/profile前缀）
            // urlPath格式: /profile/upload/rag/2026/05/12/xxx.docx
            // 实际文件路径: D:/ruoyi/uploadPath/upload/rag/2026/05/12/xxx.docx
            String absoluteFilePath = RuoYiConfig.getProfile() + urlPath.substring(Constants.RESOURCE_PREFIX.length());
            
            // Create document record
            RagDocument doc = ragDocumentService.uploadDocument(
                    originalName,
                    absoluteFilePath,
                    fileType,
                    file.getSize(),
                    SecurityUtils.getUsername());

            // Process asynchronously (in production, use @Async or MQ)
            new Thread(() -> ragDocumentService.processDocument(doc.getId())).start();

            return success(doc);
        } catch (Exception e) {
            return error("上传失败: " + e.getMessage());
        }
    }

    @PreAuthorize("@ss.hasRole('admin')")
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable Long id) {
        ragDocumentService.deleteDocument(id);
        return success();
    }

    @PreAuthorize("@ss.hasRole('admin')")
    @PostMapping("/reprocess/{id}")
    public AjaxResult reprocess(@PathVariable Long id) {
        new Thread(() -> ragDocumentService.processDocument(id)).start();
        return success();
    }
}
