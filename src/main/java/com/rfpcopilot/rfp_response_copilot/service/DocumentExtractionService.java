package com.rfpcopilot.rfp_response_copilot.service;

import java.io.IOException;
import java.nio.file.Path;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.stereotype.Service;

@Service
public class DocumentExtractionService {
	
	private final Tika tika = new Tika();
	
	public String extractText(String filePath) throws IOException, TikaException
	{
		Path path = Path.of(filePath);
		
		return tika.parseToString(path);
	}

}
