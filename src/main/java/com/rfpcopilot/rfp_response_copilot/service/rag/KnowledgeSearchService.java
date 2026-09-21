package com.rfpcopilot.rfp_response_copilot.service.rag;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import com.rfpcopilot.rfp_response_copilot.dto.SearchResult;
import com.rfpcopilot.rfp_response_copilot.model.KnowledgeChunk;

import co.elastic.clients.elasticsearch._types.KnnSearch;

@Service
public class KnowledgeSearchService {
	
	ElasticsearchOperations elasticOp;
	EmbeddingService embeddingService;
	
	public KnowledgeSearchService(ElasticsearchOperations elasticOp,EmbeddingService embeddingService)
	{
		this.elasticOp=elasticOp;
		this.embeddingService=embeddingService;
	}
	
	public List<SearchResult> searchBM25(String queryText,int topk)
	{
		NativeQuery query = NativeQuery.builder()
				.withQuery(q->q
						.match(m->m
								.field("text")
								.query(queryText)
						)
				)
				.withPageable(PageRequest.of(0, topk))
				.build();
		
		SearchHits<KnowledgeChunk> searchHits = 
				elasticOp.search(query,KnowledgeChunk.class);
		
		return searchHits.getSearchHits()
		        .stream()
		        .map(hit -> {
		            KnowledgeChunk chunk = hit.getContent();

		            return new SearchResult(
		                    chunk.getId(),
		                    chunk.getText(),
		                    chunk.getDocumentId(),
		                    chunk.getDocumentName(),
		                    chunk.getChunkNumber(),
		                    hit.getScore()
		            );
		        })
		        .toList();
	}
	
	public List<SearchResult> searchVector(
            String queryText,
            int topK) {

		String searchText =
		        "Represent this sentence for searching relevant passages: "
		        + queryText;
		
        List<Float> queryEmbedding =
                embeddingService.embedText(searchText);

        KnnSearch knnSearch = KnnSearch.of(k -> k
                .field("embedding")
                .queryVector(queryEmbedding)
                .k(topK)
                .numCandidates(topK * 10)
        );

        NativeQuery query = NativeQuery.builder()
                .withKnnSearches(List.of(knnSearch))
                .build();

        SearchHits<KnowledgeChunk> searchHits =
                elasticOp.search(
                        query,
                        KnowledgeChunk.class
                );

        return searchHits.getSearchHits()
                .stream()
                .map(hit -> {
                    KnowledgeChunk chunk = hit.getContent();

                    return new SearchResult(
                            chunk.getId(),
                            chunk.getText(),
                            chunk.getDocumentId(),
                            chunk.getDocumentName(),
                            chunk.getChunkNumber(),
                            hit.getScore()
                    );
                })
                .toList();
    }

}
