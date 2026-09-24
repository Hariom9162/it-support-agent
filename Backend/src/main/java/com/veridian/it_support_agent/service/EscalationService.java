package com.veridian.it_support_agent.service;

import com.veridian.it_support_agent.entity.Ticket;
import com.veridian.it_support_agent.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class EscalationService {

    private final TicketRepository ticketRepository;

    public EscalationService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public Ticket escalate(String employee, String issueSummary) {

        Ticket ticket = Ticket.builder()
                .ticketId("TKT-" + UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
                        .toUpperCase())
                .employee(employee)
                .issueSummary(issueSummary)
                .status("Escalated - Human Review Required")
                .build();

        return ticketRepository.save(ticket);
    }
}