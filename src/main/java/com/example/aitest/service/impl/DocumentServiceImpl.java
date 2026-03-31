package com.example.aitest.service.impl;

import com.example.aitest.common.ResultCode;
import com.example.aitest.config.BusinessException;
import com.example.aitest.entity.ApplicationDocument;
import com.example.aitest.entity.DocumentType;
import com.example.aitest.mapper.ApplicationDocumentMapper;
import com.example.aitest.service.DocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 申请材料服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final ApplicationDocumentMapper applicationDocumentMapper;

    @Override
    public ApplicationDocument findById(Long id) {
        log.info("查询申请材料，id: {}", id);
        ApplicationDocument document = applicationDocumentMapper.findById(id);
        if (document == null) {
            throw new BusinessException("材料不存在");
        }
        return document;
    }

    @Override
    public List<ApplicationDocument> findByApplicationId(Long applicationId) {
        log.info("查询申请材料列表，applicationId: {}", applicationId);
        return applicationDocumentMapper.findByApplicationId(applicationId);
    }

    @Override
    @Transactional
    public ApplicationDocument upload(ApplicationDocument document) {
        log.info("上传申请材料，applicationId: {}, docType: {}",
                 document.getApplicationId(), document.getDocType());

        // 验证材料类型
        try {
            DocumentType.valueOf(document.getDocType());
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ResultCode.DOCUMENT_TYPE_INVALID);
        }

        // 检查文件大小限制（假设限制为 10MB）
        long maxSize = 10 * 1024 * 1024;
        if (document.getFileSize() != null && document.getFileSize() > maxSize) {
            throw new BusinessException(ResultCode.FILE_SIZE_EXCEEDED);
        }

        document.setUploadedAt(LocalDateTime.now());
        applicationDocumentMapper.insert(document);

        log.info("申请材料上传成功，id: {}", document.getId());
        return document;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("删除申请材料，id: {}", id);

        ApplicationDocument document = applicationDocumentMapper.findById(id);
        if (document == null) {
            throw new BusinessException("材料不存在");
        }

        applicationDocumentMapper.deleteById(id);

        log.info("申请材料删除成功，id: {}", id);
    }

    @Override
    @Transactional
    public void deleteByApplicationId(Long applicationId) {
        log.info("删除申请的所有材料，applicationId: {}", applicationId);

        applicationDocumentMapper.deleteByApplicationId(applicationId);

        log.info("申请材料删除成功，applicationId: {}", applicationId);
    }
}
