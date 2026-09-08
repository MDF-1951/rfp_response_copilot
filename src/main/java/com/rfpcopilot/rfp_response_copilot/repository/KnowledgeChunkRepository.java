package com.rfpcopilot.rfp_response_copilot.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.rfpcopilot.rfp_response_copilot.model.KnowledgeChunk;

public interface KnowledgeChunkRepository extends ElasticsearchRepository<KnowledgeChunk, String> {

}
