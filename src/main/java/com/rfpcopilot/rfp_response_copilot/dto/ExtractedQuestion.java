package com.rfpcopilot.rfp_response_copilot.dto;

public class ExtractedQuestion {
	
	private String questionText;
	private String section;
	private String questionNumber;
	public ExtractedQuestion(String questionText, String section, String questionNumber) {
		super();
		this.questionText = questionText;
		this.section = section;
		this.questionNumber = questionNumber;
	}
	
	
	
	public String getQuestionText() {
		return questionText;
	}
	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}



	public String getSection() {
		return section;
	}
	public void setSection(String section) {
		this.section = section;
	}
	public String getQuestionNumber() {
		return questionNumber;
	}
	public void setQuestionNumber(String questionNumber) {
		this.questionNumber = questionNumber;
	}
	@Override
	public String toString() {
		return "ExtractedQuestion [question=" + questionText + ", section=" + section + ", questionNumber=" + questionNumber
				+ "]";
	}
	
	
	
	

}
