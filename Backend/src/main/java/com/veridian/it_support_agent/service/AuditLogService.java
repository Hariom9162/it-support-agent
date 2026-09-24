package com.veridian.it_support_agent.service;

import com.veridian.it_support_agent.entity.AuditLog;
import com.veridian.it_support_agent.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public AuditLog saveAudit(
            String request,
            String question,
            String intent,
            String sourceUsed,
            String actionTaken,
            boolean escalated) {

        AuditLog auditLog = AuditLog.builder()
                .request(request)
                .question(question)
                .intent(intent)
                .sourceUsed(sourceUsed)
                .actionTaken(actionTaken)
                .escalated(escalated)
                .createdAt(LocalDateTime.now())
                .build();

        return auditLogRepository.save(auditLog);
    }
}