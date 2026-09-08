package com.rfpcopilot.rfp_response_copilot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rfpcopilot.rfp_response_copilot.model.KnowledgeDocument;

public interface KnowledgeDocumentRepository extends JpaRepository<KnowledgeDocument, Long> {

}
