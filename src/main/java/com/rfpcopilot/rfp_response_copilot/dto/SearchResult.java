package com.rfpcopilot.rfp_response_copilot.dto;

import com.rfpcopilot.rfp_response_copilot.model.KnowledgeChunk;

public record SearchResult(String id,
        String text,
        String documentId,
        String documentName,
        Integer chunkNumber,
        float score) {

}
