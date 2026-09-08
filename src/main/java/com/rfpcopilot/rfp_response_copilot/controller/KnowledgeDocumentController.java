package com.rfpcopilot.rfp_response_copilot.controller;

import java.io.IOException;

import org.apache.tika.exception.TikaException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.rfpcopilot.rfp_response_copilot.dto.KnowledgeDocDto;
import com.rfpcopilot.rfp_response_copilot.service.KnowledgeDocumentService;

@RestController
@RequestMapping("/knowledgeDocument")
public class KnowledgeDocumentController {
	
	KnowledgeDocumentService kdService;
	
	public KnowledgeDocumentController(KnowledgeDocumentService kdService) {
		super();
		this.kdService = kdService;
	}



	@PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public String registerKD(@RequestPart("docName") String docName, @RequestPart("document") MultipartFile document) throws IOException, TikaException
	{
		KnowledgeDocDto dto = new KnowledgeDocDto(docName,document);
		
		return kdService.registerKnowledgeDocument(dto);
	}

}
