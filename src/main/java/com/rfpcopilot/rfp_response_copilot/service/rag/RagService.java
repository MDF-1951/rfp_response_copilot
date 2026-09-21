package com.rfpcopilot.rfp_response_copilot.service.rag;


import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rfpcopilot.rfp_response_copilot.dto.RagAnswer;
import com.rfpcopilot.rfp_response_copilot.dto.RagLlmResponse;
import com.rfpcopilot.rfp_response_copilot.dto.SearchResult;

import dev.langchain4j.model.openai.OpenAiChatModel;

@Service
public class RagService {

    private final HybridSearchService hybridSearchService;
    private final ContextBuilderService contextBuilderService;
    private final OpenAiChatModel chatModel;
    private final ObjectMapper objectMapper;

    public RagService(
            HybridSearchService hybridSearchService,
            ContextBuilderService contextBuilderService,
            OpenAiChatModel chatModel,
            ObjectMapper objectMapper) {

        this.hybridSearchService = hybridSearchService;
        this.contextBuilderService = contextBuilderService;
        this.chatModel = chatModel;
        this.objectMapper = objectMapper;
    }

    public RagAnswer answerQuestion(String question) {

        List<SearchResult> results =
                hybridSearchService.search(question, 5);

        String context =
                contextBuilderService.buildContext(results);

        String prompt = buildPrompt(question, context);

        String response =
                chatModel.chat(prompt);
        
        System.out.println("========== RAG LLM RESPONSE ==========");
        System.out.println(response);
        System.out.println("======================================");

        response = cleanJson(response);

        try {

            RagLlmResponse llmResponse =
                    objectMapper.readValue(
                            response,
                            RagLlmResponse.class
                    );

            List<SearchResult> citedSources =
                    new ArrayList<>();

            for (Integer citation : llmResponse.citations()) {

                int index = citation - 1;

                if (index >= 0 && index < results.size()) {
                    citedSources.add(results.get(index));
                }
            }

            return new RagAnswer(
                    question,
                    llmResponse.answer(),
                    llmResponse.status(),
                    citedSources
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to parse RAG response. LLM returned:\n"
                            + response,
                    e
            );
        }
    }

    private String buildPrompt(
            String question,
            String context) {

        return """
                You are an enterprise RFP response assistant.

                Answer the user's question using ONLY the provided evidence.

                Do not use outside knowledge.

                If the evidence does not contain enough information
                to answer the question, return INSUFFICIENT_EVIDENCE.

                Every factual statement in the answer must be supported
                by the provided sources.

                Cite the sources using their SOURCE numbers.

                Return ONLY valid JSON in this format:

                {
                  "answer": "your answer",
                  "status": "SUPPORTED",
                  "citations": [1, 2]
                }

                The status must be either:
                - SUPPORTED
                - INSUFFICIENT_EVIDENCE

                User Question:
                %s

                Evidence:
                %s
                """.formatted(question, context);
    }

    private String cleanJson(String response) {

        return response
                .replace("```json", "")
                .replace("```", "")
                .trim();
    }
}