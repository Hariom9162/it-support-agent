package com.veridian.it_support_agent.controller;

import com.veridian.it_support_agent.entity.KnowledgeBase;
import com.veridian.it_support_agent.service.PolicySearchService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/policies")
@CrossOrigin(origins = "*")
public class KnowledgeBaseController {

    private final PolicySearchService policySearchService;

    public KnowledgeBaseController(PolicySearchService policySearchService) {
        this.policySearchService = policySearchService;
    }

    @GetMapping("/search")
    public List<KnowledgeBase> searchPolicies(
            @RequestParam String query) {

        return policySearchService.searchPolicies(query);
    }
}