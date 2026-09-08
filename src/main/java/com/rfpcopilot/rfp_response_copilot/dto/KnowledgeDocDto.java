package com.rfpcopilot.rfp_response_copilot.dto;

import org.springframework.web.multipart.MultipartFile;

public class KnowledgeDocDto {
	
	private String docName;
	private MultipartFile document;
	
	
	public KnowledgeDocDto(String docName, MultipartFile document) {
		super();
		this.docName = docName;
		this.document = document;
	}
	
	
	public String getDocName() {
		return docName;
	}
	public void setDocName(String docName) {
		this.docName = docName;
	}
	public MultipartFile getDocument() {
		return document;
	}
	public void setDocument(MultipartFile document) {
		this.document = document;
	}
	
	

}
