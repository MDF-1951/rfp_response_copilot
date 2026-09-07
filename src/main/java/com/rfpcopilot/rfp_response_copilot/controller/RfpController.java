package com.rfpcopilot.rfp_response_copilot.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import org.apache.tika.exception.TikaException;
import org.springframework.http.MediaType;

import com.rfpcopilot.rfp_response_copilot.dto.CreateRfpRequest;
import com.rfpcopilot.rfp_response_copilot.model.Rfp;
import com.rfpcopilot.rfp_response_copilot.service.RfpService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/rfps")
public class RfpController {
	
	private RfpService rfpser;
	
	public RfpController(RfpService rfpser)
	{
		this.rfpser=rfpser;
	}
	
	@PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    //@ResponseStatus(HttpStatus.CREATED)
	public Rfp createRfp(@Valid @RequestPart("title") String title,
								@Valid @RequestPart("customerName") String customerName,
								@RequestPart("document") MultipartFile document) throws IOException, TikaException
	{
		
		CreateRfpRequest rfpreq = new CreateRfpRequest(title,customerName,document);
		return rfpser.createRfp(rfpreq);
	}


}
