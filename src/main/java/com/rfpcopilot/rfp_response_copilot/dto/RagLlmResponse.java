package com.rfpcopilot.rfp_response_copilot.dto;

import java.util.List;

public record RagLlmResponse(
        String answer,
        String status,
        List<Integer> citations
) {
}