package com.rfpcopilot.rfp_response_copilot.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rfpcopilot.rfp_response_copilot.service.HealthService;

@RestController
public class HealthController {
	
	private final HealthService service;
	
	public HealthController(HealthService service)
	{
		this.service=service;
	}
	
	@GetMapping("/api/health")
	public String healthCheck()
	{
		return service.healthCheck();
	}

}
