package com.rfpcopilot.rfp_response_copilot.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class RfpQuestion {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	
	private String questionText;
	
	@ManyToOne
	@JoinColumn(name="rfp_id",nullable=false)
	private Rfp rfp;
	
	@Column(columnDefinition = "TEXT")
	private String answer;

	private String answerStatus;
	
	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public String getAnswerStatus() {
		return answerStatus;
	}

	public void setAnswerStatus(String answerStatus) {
		this.answerStatus = answerStatus;
	}

	public long getId()
	{
		return id;
	}

	public String getQuestionText() {
		return questionText;
	}

	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}

	public Rfp getRfp() {
		return rfp;
	}

	public void setRfp(Rfp rfp) {
		this.rfp = rfp;
	}
	
	
	
	

}
