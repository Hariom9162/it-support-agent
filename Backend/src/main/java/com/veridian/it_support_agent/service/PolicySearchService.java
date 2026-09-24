package com.veridian.it_support_agent.service;

import com.veridian.it_support_agent.entity.KnowledgeBase;
import com.veridian.it_support_agent.repository.KnowledgeBaseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PolicySearchService {

    private final KnowledgeBaseRepository knowledgeBaseRepository;

    public PolicySearchService(KnowledgeBaseRepository knowledgeBaseRepository) {
        this.knowledgeBaseRepository = knowledgeBaseRepository;
    }

    public List<KnowledgeBase> searchPolicies(String query) {

        if (query == null || query.trim().isEmpty()) {
            return List.of();
        }

        return knowledgeBaseRepository
                .findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(
                        query.trim(),
                        query.trim()
                );
    }
}