package com.rfpcopilot.rfp_response_copilot.service;

import org.springframework.stereotype.Service;

@Service
public class HealthService {
	
	public String healthCheck()
	{
		return "RFP API working properly";
	}

}
