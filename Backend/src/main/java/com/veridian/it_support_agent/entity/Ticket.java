package com.veridian.it_support_agent.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tickets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ticket_id", unique = true, nullable = false)
    private String ticketId;

    @Column(nullable = false)
    private String employee;

    @Column(name = "issue_summary", columnDefinition = "TEXT", nullable = false)
    private String issueSummary;

    @Column(nullable = false)
    private String status;
}