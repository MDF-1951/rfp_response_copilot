package com.rfpcopilot.rfp_response_copilot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rfpcopilot.rfp_response_copilot.model.RfpQuestion;

public interface RfpQuestionsRepository extends JpaRepository<RfpQuestion, Long> {
	
	List<RfpQuestion> findByRfpId(Long rfpId);

}
