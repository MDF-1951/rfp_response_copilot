package com.rfpcopilot.rfp_response_copilot.service.rag;

import java.util.List;

import org.springframework.stereotype.Service;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.bgesmallenv15.BgeSmallEnV15EmbeddingModel;


@Service
public class EmbeddingService {
	
	
	private final EmbeddingModel embedModel;
	
	public EmbeddingService() {
		
		this.embedModel = new BgeSmallEnV15EmbeddingModel();
	}
	
	public List<Float> embedText(String text)
	{
		Embedding embedding = embedModel.embed(text).content();
		
		return embedding.vectorAsList();
	}

}
