package com.veridian.it_support_agent.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String request;

    @Column(columnDefinition = "TEXT")
    private String question;

    @Column
    private String intent;

    @Column(name = "source_used")
    private String sourceUsed;

    @Column(name = "action_taken", columnDefinition = "TEXT")
    private String actionTaken;

    @Column
    private boolean escalated;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}