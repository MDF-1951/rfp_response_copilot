package com.rfpcopilot.rfp_response_copilot.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import org.apache.tika.exception.TikaException;
import org.springframework.http.MediaType;

import com.rfpcopilot.rfp_response_copilot.dto.CreateRfpRequest;
import com.rfpcopilot.rfp_response_copilot.model.Rfp;
import com.rfpcopilot.rfp_response_copilot.model.RfpQuestion;
import com.rfpcopilot.rfp_response_copilot.service.RfpResponseService;
import com.rfpcopilot.rfp_response_copilot.service.RfpService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/rfps")
public class RfpController {
	
	private RfpService rfpser;
	private final RfpResponseService rfpResponseSer;
	
	public RfpController(RfpService rfpser,RfpResponseService rfpResponseSer)
	{
		this.rfpser=rfpser;
		this.rfpResponseSer=rfpResponseSer;
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
	
	@PostMapping("/generateresponse")
	public String generateResponse(@RequestParam String id)
	{
		rfpResponseSer.generateResponses(Long.parseLong(id));
		
		return "Answers Generated Successfully";
	}
	
	@GetMapping("/getAnswers")
	public List<RfpQuestion> getAnswers(@RequestParam String id)
	{
		return rfpser.getQAnswers(Long.parseLong(id));
	}


}
