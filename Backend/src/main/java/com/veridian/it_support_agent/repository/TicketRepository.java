package com.veridian.it_support_agent.repository;

import com.veridian.it_support_agent.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    Optional<Ticket> findByTicketId(String ticketId);
}