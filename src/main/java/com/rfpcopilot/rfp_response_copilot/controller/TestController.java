package com.rfpcopilot.rfp_response_copilot.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.apache.tika.exception.TikaException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.rfpcopilot.rfp_response_copilot.dto.ExtractedQuestion;
import com.rfpcopilot.rfp_response_copilot.model.Rfp;
import com.rfpcopilot.rfp_response_copilot.repository.RfpRepository;
import com.rfpcopilot.rfp_response_copilot.service.DocumentExtractionService;
import com.rfpcopilot.rfp_response_copilot.service.QuestionExtractionService;
import com.rfpcopilot.rfp_response_copilot.service.rag.EmbeddingService;
import com.rfpcopilot.rfp_response_copilot.service.rag.KnowledgeIndexingService;

@RestController
@RequestMapping("/test")
public class TestController {
	
	private final DocumentExtractionService docSer;
	private final RfpRepository rfpRepo;
	private final QuestionExtractionService qSer;
	private final EmbeddingService eSer;
	private final KnowledgeIndexingService knowledgeIndexingService;
	
	private final Path storage = Paths.get("storage/test");
	
	public TestController(DocumentExtractionService docSer, RfpRepository rfpRepo, QuestionExtractionService qSer, 
			EmbeddingService eSer, KnowledgeIndexingService knowledgeIndexingService)
	{
		this.docSer=docSer;
		this.rfpRepo=rfpRepo;
		this.qSer=qSer;
		this.eSer=eSer;
		this.knowledgeIndexingService=knowledgeIndexingService;
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
	
	@GetMapping("/embedding")
	public List<Float> testEmbeddingModel(String text)
	{
		return eSer.embedText(text);
	}
	
	@GetMapping("/indexing")
	public String testIndex() {

	    String text = """
	            Our company uses AES-256 encryption
	            to protect customer data at rest.

	            Customer information is encrypted
	            before being stored in our databases.

	            We conduct annual security audits
	            to ensure compliance with security standards.
	            """;

	    knowledgeIndexingService.indexDocument(
	            "test-doc-1",
	            "security-policy.txt",
	            text
	    );

	    return "Document indexed successfully";
	}
	
	@PostMapping("/docTika/document")
	public String testTikaWithDocument(MultipartFile document) throws IOException, TikaException
	{
		Files.createDirectories(storage);

		Path destination = storage.resolve(document.getName());
		
		Files.copy(document.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
		
		String extract = docSer.extractText(destination.toString());
		
		System.out.println(extract);
		
		return extract;
	}

}
