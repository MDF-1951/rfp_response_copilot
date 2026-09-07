package com.rfpcopilot.rfp_response_copilot.dto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;

public class CreateRfpRequest {
	
	@NotBlank(message = "RFP Title is required")
	private String title;
	
	@NotBlank(message = "RFP Corporation Name required")
	private String customerName;
	
	private MultipartFile document;

	public CreateRfpRequest(@NotBlank(message = "RFP Title is required") String title,
			@NotBlank(message = "RFP Corporation Name required") String customerName, MultipartFile document) {
		super();
		this.title = title;
		this.customerName = customerName;
		this.document=document;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public MultipartFile getDocument() {
		return document;
	}

	public void setDocument(MultipartFile document) {
		this.document = document;
	}
	
	
	
	

}
