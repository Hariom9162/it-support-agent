package com.veridian.it_support_agent.controller;

import com.veridian.it_support_agent.dto.AgentRequest;
import com.veridian.it_support_agent.dto.AgentResponse;
import com.veridian.it_support_agent.service.AgentService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@RequestMapping("/api/agent")
@CrossOrigin(origins = "http://localhost:5173")
public class AgentController {

    private final AgentService agentService;

    public AgentController(AgentService agentService) {
        this.agentService = agentService;
    }

    @PostMapping("/chat")
    public AgentResponse chat(@RequestBody AgentRequest request) {
        return agentService.processRequest(request);
    }
}