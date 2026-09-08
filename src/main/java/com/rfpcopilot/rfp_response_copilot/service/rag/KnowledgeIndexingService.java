package com.rfpcopilot.rfp_response_copilot.service.rag;

import java.util.List;

import org.springframework.stereotype.Service;

import com.rfpcopilot.rfp_response_copilot.model.KnowledgeChunk;
import com.rfpcopilot.rfp_response_copilot.repository.KnowledgeChunkRepository;

@Service
public class KnowledgeIndexingService {
	
	private final EmbeddingService embedService;
	private final TextChunkingService chunkService;
	private final KnowledgeChunkRepository kcRepo;
	
	public KnowledgeIndexingService(EmbeddingService embedService, TextChunkingService chunkService,
			KnowledgeChunkRepository kcRepo) {
		super();
		this.embedService = embedService;
		this.chunkService = chunkService;
		this.kcRepo = kcRepo;
	}
	
	public void indexDocument(String docId,String docName,String text)
	{
		List<String> chunks = chunkService.chunk(text);
		
		for(int i=0;i<chunks.size();i++)
		{
			String chunkText = chunks.get(i);
			
			List<Float> enbeddings = embedService.embedText(chunkText);
			
			KnowledgeChunk doc = new KnowledgeChunk();
			
			doc.setDocumentId(docId);
			doc.setDocumentName(docName);
			doc.setId(docId+"-"+i);
			doc.setText(chunkText);
			doc.setEmbedding(enbeddings);
			doc.setChunkNumber(i);
			
			kcRepo.save(doc);
			
		}
		
	}
	
	

}
