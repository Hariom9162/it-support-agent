package com.veridian.it_support_agent.controller;

import com.veridian.it_support_agent.entity.Ticket;
import com.veridian.it_support_agent.service.EscalationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/escalations")
@CrossOrigin(origins = "*")
public class EscalationController {

    private final EscalationService escalationService;

    public EscalationController(EscalationService escalationService) {
        this.escalationService = escalationService;
    }

    @PostMapping
    public Ticket escalate(
            @RequestParam String employee,
            @RequestParam String issue) {

        return escalationService.escalate(employee, issue);
    }
}