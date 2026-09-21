package com.rfpcopilot.rfp_response_copilot.dto;

import java.util.List;

public record RagAnswer(
        String question,
        String answer,
        String status,
        List<SearchResult> sources
) {
}