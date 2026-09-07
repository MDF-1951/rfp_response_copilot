package com.rfpcopilot.rfp_response_copilot.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {
	
	private final Path storageLocation = Paths.get("storage/rfps");
	
	public String store(Long rfpId, MultipartFile document) throws IOException
	{
		Path rfpDirectory = storageLocation.resolve("rfp_"+rfpId);
		
		Files.createDirectories(rfpDirectory);
		
		String originalName = document.getOriginalFilename();
		
		Path destination = rfpDirectory.resolve(originalName);
		
		Files.copy(document.getInputStream(), destination,
					StandardCopyOption.REPLACE_EXISTING);
		
		return destination.toString();
	}

}
