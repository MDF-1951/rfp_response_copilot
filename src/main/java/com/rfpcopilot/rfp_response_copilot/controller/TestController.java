package com.rfpcopilot.rfp_response_copilot.controller;

import java.io.IOException;
import java.util.List;

import org.apache.tika.exception.TikaException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rfpcopilot.rfp_response_copilot.dto.ExtractedQuestion;
import com.rfpcopilot.rfp_response_copilot.model.Rfp;
import com.rfpcopilot.rfp_response_copilot.repository.RfpRepository;
import com.rfpcopilot.rfp_response_copilot.service.DocumentExtractionService;
import com.rfpcopilot.rfp_response_copilot.service.QuestionExtractionService;

@RestController
@RequestMapping("/test")
public class TestController {
	
	private final DocumentExtractionService docSer;
	private final RfpRepository rfpRepo;
	private final QuestionExtractionService qSer;
	
	public TestController(DocumentExtractionService docSer, RfpRepository rfpRepo, QuestionExtractionService qSer )
	{
		this.docSer=docSer;
		this.rfpRepo=rfpRepo;
		this.qSer=qSer;
	}
	
	
	@GetMapping("/docTika")
	public String testApacheTika(String rfpId) throws IOException, TikaException
	{
		Rfp rfp = rfpRepo.findById(Long.parseLong(rfpId)).orElseThrow();
		
		String extractedText = docSer.extractText(rfp.getFilePath());
		
		System.out.println(extractedText);
		
		return extractedText;
		
	}
	
	@GetMapping("/test-llm")
	public String testCallLLM()
	{
		return qSer.testLlm();
	}
	
	@GetMapping("/test-qextract")
	public List<ExtractedQuestion> testQuestionExtraction(String rfpId) throws IOException, TikaException
	{
		Rfp rfp = rfpRepo.findById(Long.parseLong(rfpId)).orElseThrow();
		
		String extractedText = docSer.extractText(rfp.getFilePath());
		
		List<ExtractedQuestion> list = qSer.extractQuestions(extractedText);
		
		for(ExtractedQuestion q: list)
		{
			q.toString();
		}
		
		return list;
	}

}
