package com.rfpcopilot.rfp_response_copilot.service;

import java.io.IOException;
import java.util.List;

import org.apache.tika.exception.TikaException;
import org.springframework.stereotype.Service;

import com.rfpcopilot.rfp_response_copilot.dto.CreateRfpRequest;
import com.rfpcopilot.rfp_response_copilot.dto.ExtractedQuestion;
import com.rfpcopilot.rfp_response_copilot.model.Rfp;
import com.rfpcopilot.rfp_response_copilot.model.RfpQuestion;
import com.rfpcopilot.rfp_response_copilot.repository.RfpQuestionsRepository;
import com.rfpcopilot.rfp_response_copilot.repository.RfpRepository;

@Service
public class RfpService {
	
	private RfpRepository rfprepo;
	private FileStorageService fileStore;
	private DocumentExtractionService docSer;
	private QuestionExtractionService qSer;
	private RfpQuestionsRepository rfpQRepo;
	
	
	public RfpService(RfpRepository rfprepo, FileStorageService fileStore, DocumentExtractionService docSer, QuestionExtractionService qSer
			, RfpQuestionsRepository rfpQRepo)
	{
		this.rfprepo=rfprepo;
		this.fileStore = fileStore;
		this.docSer = docSer;
		this.qSer = qSer;
		this.rfpQRepo = rfpQRepo;
	}
	
	private void saveQuestions(List<ExtractedQuestion> questions,Rfp rfp)
	{
		List<RfpQuestion> rfpQuestion = questions.stream()
											.map(extracted -> {
												RfpQuestion rq = new RfpQuestion();
												rq.setQuestionText(extracted.getQuestionText());
												rq.setRfp(rfp);
												
												return rq;
											}).toList();
		
		rfpQRepo.saveAll(rfpQuestion);
		
	}
	
	public Rfp createRfp(CreateRfpRequest rfpreq) throws IOException, TikaException
	{
		Rfp rfp = new Rfp();
		
		rfp.setTitle(rfpreq.getTitle());
		rfp.setCustomerName(rfpreq.getCustomerName());
		
		Rfp savedRfp = rfprepo.save(rfp);
		
		String filePath = fileStore.store(savedRfp.getId(),rfpreq.getDocument());
		
		savedRfp.setFilePath(filePath);
		
		String extractedDoc = docSer.extractText(filePath);
		
		List<ExtractedQuestion> list = qSer.extractQuestions(extractedDoc);
		
		saveQuestions(list,savedRfp);
		
		
		
		for(ExtractedQuestion q: list)
		{
			q.toString();
		}
		
		return rfprepo.save(savedRfp);
		
		
	}

}
