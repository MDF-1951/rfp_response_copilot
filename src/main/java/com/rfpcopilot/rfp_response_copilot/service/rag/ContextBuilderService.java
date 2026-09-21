package com.rfpcopilot.rfp_response_copilot.service.rag;

import java.util.List;

import org.springframework.stereotype.Service;

import com.rfpcopilot.rfp_response_copilot.dto.SearchResult;

@Service
public class ContextBuilderService {

    public String buildContext(List<SearchResult> results) {

        StringBuilder context = new StringBuilder();

        for (int i = 0; i < results.size(); i++) {

            SearchResult result = results.get(i);

            context.append("SOURCE ")
                    .append(i + 1)
                    .append("\n");

            context.append("Document: ")
                    .append(result.documentName())
                    .append("\n");

            context.append("Chunk: ")
                    .append(result.chunkNumber())
                    .append("\n");

            context.append("Content:\n")
                    .append(result.text())
                    .append("\n\n");
        }

        return context.toString();
    }
}