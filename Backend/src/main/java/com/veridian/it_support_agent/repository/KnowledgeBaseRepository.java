package com.veridian.it_support_agent.repository;

import com.veridian.it_support_agent.entity.KnowledgeBase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KnowledgeBaseRepository
        extends JpaRepository<KnowledgeBase, Long> {

    List<KnowledgeBase> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(
            String title,
            String content
    );
}