package com.rfpcopilot.rfp_response_copilot.service.rag;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class TextChunkingService {
	
	
	private static final int CHUNK_SIZE = 1000;
	private static final int OVERLAP = 200;
	
	public List<String> chunk(String text)
	{
		List<String> chunks = new ArrayList<>();
		
		if(text==null || text.isBlank())
			return chunks;
		
		int start = 0;
		
		while(start<text.length())
		{
			int end = Math.min(start+CHUNK_SIZE, text.length());
			
			chunks.add(text.substring(start,end));
			
			if (end == text.length())
	            break;
			
			start = end-OVERLAP;
			
		}
		
		return chunks;
	}

}
