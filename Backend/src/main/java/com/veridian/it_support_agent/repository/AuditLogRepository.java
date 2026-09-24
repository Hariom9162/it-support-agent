package com.veridian.it_support_agent.repository;

import com.veridian.it_support_agent.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}