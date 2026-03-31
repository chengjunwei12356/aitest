package com.example.aitest.service.impl;

import com.example.aitest.config.BusinessException;
import com.example.aitest.entity.ApprovalRecord;
import com.example.aitest.mapper.ApprovalRecordMapper;
import com.example.aitest.service.ApprovalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 审批记录服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ApprovalServiceImpl implements ApprovalService {

    private final ApprovalRecordMapper approvalRecordMapper;

    @Override
    public ApprovalRecord findById(Long id) {
        log.info("查询审批记录，id: {}", id);
        ApprovalRecord record = approvalRecordMapper.findById(id);
        if (record == null) {
            throw new BusinessException("审批记录不存在");
        }
        return record;
    }

    @Override
    public List<ApprovalRecord> findByApplicationId(Long applicationId) {
        log.info("查询审批记录列表，applicationId: {}", applicationId);
        return approvalRecordMapper.findByApplicationId(applicationId);
    }

    @Override
    @Transactional
    public ApprovalRecord create(ApprovalRecord record) {
        log.info("创建审批记录，applicationId: {}, stage: {}, action: {}",
                 record.getApplicationId(), record.getStage(), record.getAction());

        record.setCreatedAt(LocalDateTime.now());
        approvalRecordMapper.insert(record);

        log.info("审批记录创建成功，id: {}", record.getId());
        return record;
    }
}
