package com.veridian.it_support_agent.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "employee_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "request_id", unique = true, nullable = false)
    private String requestId;

    @Column(nullable = false)
    private String employeeName;

    @Column(nullable = false)
    private String email;

    @Column(name = "date_opened", nullable = false)
    private LocalDate dateOpened;

    @Column(name = "request_text", columnDefinition = "TEXT", nullable = false)
    private String requestText;

    @Column(name = "initial_action", columnDefinition = "TEXT")
    private String initialAction;
}