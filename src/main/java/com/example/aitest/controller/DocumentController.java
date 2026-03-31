package com.example.aitest.controller;

import com.example.aitest.common.Result;
import com.example.aitest.entity.ApplicationDocument;
import com.example.aitest.service.DocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 申请材料管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    // 文件存储路径
    private static final String UPLOAD_DIR = "uploads/applications/";

    /**
     * 获取申请材料列表
     */
    @GetMapping("/{applicationId}/documents")
    public Result<List<ApplicationDocument>> list(@PathVariable Long applicationId) {
        List<ApplicationDocument> list = documentService.findByApplicationId(applicationId);
        return Result.success(list);
    }

    /**
     * 上传材料
     */
    @PostMapping("/{applicationId}/documents")
    public Result<ApplicationDocument> upload(@PathVariable Long applicationId,
                                              @RequestParam("docType") String docType,
                                              @RequestParam("file") MultipartFile file) {
        try {
            // 创建上传目录
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 生成文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : "";
            String filename = UUID.randomUUID().toString() + extension;

            // 保存文件
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath);

            // 创建材料记录
            ApplicationDocument document = new ApplicationDocument();
            document.setApplicationId(applicationId);
            document.setDocType(docType);
            document.setDocName(originalFilename);
            document.setFilePath(filePath.toString());
            document.setFileSize(file.getSize());
            document.setUploadedBy(1L); // 当前用户 ID，实际应从 session 获取

            ApplicationDocument saved = documentService.upload(document);
            return Result.success(saved);

        } catch (IOException e) {
            log.error("文件上传失败", e);
            return Result.error("文件上传失败：" + e.getMessage());
        }
    }

    /**
     * 删除材料
     */
    @DeleteMapping("/{applicationId}/documents/{docId}")
    public Result<Void> delete(@PathVariable Long applicationId,
                               @PathVariable Long docId) {
        documentService.delete(docId);
        return Result.success();
    }
}
