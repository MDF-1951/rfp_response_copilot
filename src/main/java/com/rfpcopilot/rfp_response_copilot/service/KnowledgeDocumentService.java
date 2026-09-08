package com.rfpcopilot.rfp_response_copilot.service;

import java.io.IOException;

import org.apache.tika.exception.TikaException;
import org.springframework.stereotype.Service;

import com.rfpcopilot.rfp_response_copilot.dto.KnowledgeDocDto;
import com.rfpcopilot.rfp_response_copilot.model.KnowledgeDocument;
import com.rfpcopilot.rfp_response_copilot.repository.KnowledgeDocumentRepository;
import com.rfpcopilot.rfp_response_copilot.service.rag.KnowledgeIndexingService;

@Service
public class KnowledgeDocumentService {
	
	FileStorageService fileService;
	KnowledgeDocumentRepository kdRepo;
	DocumentExtractionService docSer;
	KnowledgeIndexingService indexService;
	
	public KnowledgeDocumentService(FileStorageService fileService, KnowledgeDocumentRepository kdRepo,
			DocumentExtractionService docSer, KnowledgeIndexingService indexService) {
		super();
		this.fileService = fileService;
		this.kdRepo = kdRepo;
		this.docSer = docSer;
		this.indexService = indexService;
	}


	public String registerKnowledgeDocument(KnowledgeDocDto dto) throws IOException, TikaException
	{
		KnowledgeDocument kd = new KnowledgeDocument();
		
		kd.setDocName(dto.getDocName());
		
		KnowledgeDocument savedKd = kdRepo.save(kd);
		
		String path = fileService.storeKnowledgeDoc(savedKd.getId(), dto.getDocument());
		
		savedKd.setFilePath(path);
		
		kdRepo.save(savedKd);
		
		String text = docSer.extractText(path);
		
		indexService.indexDocument(dto.getDocName(),dto.getDocument().getName(), text);
		
		return "Document Chunked|Embedded and Indexed Successfully";
		
		
		
		
	}
	

}
