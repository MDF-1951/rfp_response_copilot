package com.rfpcopilot.rfp_response_copilot.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.rfpcopilot.rfp_response_copilot.dto.RagAnswer;
import com.rfpcopilot.rfp_response_copilot.model.Rfp;
import com.rfpcopilot.rfp_response_copilot.model.RfpQuestion;
import com.rfpcopilot.rfp_response_copilot.repository.RfpQuestionsRepository;
import com.rfpcopilot.rfp_response_copilot.repository.RfpRepository;
import com.rfpcopilot.rfp_response_copilot.service.rag.RagService;

@Service
public class RfpResponseService {

    private final RfpRepository rfpRepository;
    private final RfpQuestionsRepository questionRepository;
    private final RagService ragService;
    
    
	public RfpResponseService(RfpRepository rfpRepository, RfpQuestionsRepository questionRepository,
			RagService ragService) {
		super();
		this.rfpRepository = rfpRepository;
		this.questionRepository = questionRepository;
		this.ragService = ragService;
	}

	
	public void generateResponses(Long rfpId) {

	    Rfp rfp = rfpRepository.findById(rfpId)
	            .orElseThrow();

	    List<RfpQuestion> questions =
	            questionRepository.findByRfpId(rfpId);

	    for (RfpQuestion question : questions) {

	        RagAnswer ragAnswer =
	                ragService.answerQuestion(
	                        question.getQuestionText()
	                );

	        question.setAnswer(ragAnswer.answer());
	        question.setAnswerStatus(ragAnswer.status());

	        questionRepository.save(question);
	    }
	}
    
}
