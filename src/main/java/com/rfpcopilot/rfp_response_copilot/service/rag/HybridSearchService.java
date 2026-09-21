package com.rfpcopilot.rfp_response_copilot.service.rag;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.rfpcopilot.rfp_response_copilot.dto.SearchResult;
import com.rfpcopilot.rfp_response_copilot.model.KnowledgeChunk;

@Service
public class HybridSearchService {

    private final KnowledgeSearchService searchService;

    private static final int RRF_K = 60;

    public HybridSearchService(KnowledgeSearchService searchService) {
        this.searchService = searchService;
    }

    public List<SearchResult> search(String query, int topK) {

        List<SearchResult> bm25Results =
                searchService.searchBM25(query, 10);

        List<SearchResult> vectorResults =
                searchService.searchVector(query, 10);

        Map<String, Double> rrfScores = new HashMap<>();

        Map<String, SearchResult> resultsById = new HashMap<>();

        addScores(bm25Results, rrfScores, resultsById);

        addScores(vectorResults, rrfScores, resultsById);

        return rrfScores.entrySet()
                .stream()
                .sorted(
                        Map.Entry.<String, Double>comparingByValue()
                                .reversed()
                )
                .limit(topK)
                .map(entry -> {

                    SearchResult result =
                            resultsById.get(entry.getKey());

                    return new SearchResult(
                            result.id(),
                            result.text(),
                            result.documentId(),
                            result.documentName(),
                            result.chunkNumber(),
                            entry.getValue().floatValue()
                    );
                })
                .toList();
    }

    private void addScores(
            List<SearchResult> results,
            Map<String, Double> rrfScores,
            Map<String, SearchResult> resultsById) {

        for (int i = 0; i < results.size(); i++) {

            SearchResult result = results.get(i);

            int rank = i + 1;

            double score =
                    1.0 / (RRF_K + rank);

            rrfScores.merge(
                    result.id(),
                    score,
                    Double::sum
            );

            resultsById.put(
                    result.id(),
                    result
            );
        }
    }
}