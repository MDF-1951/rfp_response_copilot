package com.rfpcopilot.rfp_response_copilot.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.rfpcopilot.rfp_response_copilot.dto.ExtractedQuestion;
import com.rfpcopilot.rfp_response_copilot.dto.ExtractedQuestions;

import dev.langchain4j.model.openai.OpenAiChatModel;
import tools.jackson.databind.ObjectMapper;

@Service
public class QuestionExtractionService {
	
	private final OpenAiChatModel chatModel;
	private final ObjectMapper objMapper;
	
	public QuestionExtractionService(OpenAiChatModel chatModel, ObjectMapper objMapper)
	{
		this.chatModel = chatModel;
		this.objMapper = objMapper;
	}
	
	public String testLlm()
	{
		return chatModel.chat("Explain what an RFP is in one sentence.");
	}
	
	public List<ExtractedQuestion> extractQuestions(String docText)
	{
		String prompt = """
		        You are an expert RFP question extraction system.

		        Analyze the RFP document below and extract every item that requires
		        the vendor/respondent to provide information, an answer, explanation,
		        document, evidence, proposal, or other response.

		        IMPORTANT:
		        - Do NOT only look for sentences ending with '?'.
		        - RFP requirements written as instructions such as
		          "Provide...", "Describe...", "Submit...", "Explain...",
		          "List...", "Specify..." should also be extracted when they
		          require a substantive vendor response.
		        - Ignore purely administrative instructions such as submission
		          deadlines, number of copies, file format, mailing instructions,
		          signatures, etc.
		        - Preserve the original meaning of each requirement.
		        - Extract as many relevant response items as possible.
		        - Do not invent questions that are not present in the document.

		        For every extracted item return:
		        - questionText
		        - section
		        - questionNumber

		        Return ONLY valid JSON in this exact structure:

		        {
		          "questions": [
		            {
		              "questionText": "...",
		              "section": "...",
		              "questionNumber": "..."
		            }
		          ]
		        }

		        RFP DOCUMENT:
		        %s
		        """.formatted(docText);
		
		String response = chatModel.chat(prompt);
		
		response = response
		        .replace("```json", "")
		        .replace("```", "")
		        .trim();
		
		System.out.println("LLM RESPONSE:");
		System.out.println(response);
		
		
		
		ExtractedQuestions extractedQuestions = objMapper.readValue(response,ExtractedQuestions.class);
		
		return extractedQuestions.questions();
	}

}
