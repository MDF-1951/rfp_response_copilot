package com.rfpcopilot.rfp_response_copilot.service;

import java.io.IOException;
import java.util.List;

import org.apache.tika.exception.TikaException;
import org.springframework.stereotype.Service;

import com.rfpcopilot.rfp_response_copilot.dto.CreateRfpRequest;
import com.rfpcopilot.rfp_response_copilot.dto.ExtractedQuestion;
import com.rfpcopilot.rfp_response_copilot.model.Rfp;
import com.rfpcopilot.rfp_response_copilot.repository.RfpRepository;

@Service
public class RfpService {
	
	private RfpRepository rfprepo;
	private FileStorageService fileStore;
	private DocumentExtractionService docSer;
	private QuestionExtractionService qSer;
	
	
	public RfpService(RfpRepository rfprepo, FileStorageService fileStore, DocumentExtractionService docSer, QuestionExtractionService qSer)
	{
		this.rfprepo=rfprepo;
		this.fileStore = fileStore;
		this.docSer = docSer;
		this.qSer = qSer;
	}
	
	public Rfp createRfp(CreateRfpRequest rfpreq) throws IOException, TikaException
	{
		Rfp rfp = new Rfp();
		
		rfp.setTitle(rfpreq.getTitle());
		rfp.setCustomerName(rfpreq.getCustomerName());
		
		Rfp savedRfp = rfprepo.save(rfp);
		
		String filePath = fileStore.store(savedRfp.getId(),rfpreq.getDocument());
		
		rfp.setFilePath(filePath);
		
		String extractedDoc = docSer.extractText(filePath);
		
		List<ExtractedQuestion> list = qSer.extractQuestions(extractedDoc);
		
		for(ExtractedQuestion q: list)
		{
			q.toString();
		}
		
		return rfprepo.save(rfp);
		
		
	}

}
