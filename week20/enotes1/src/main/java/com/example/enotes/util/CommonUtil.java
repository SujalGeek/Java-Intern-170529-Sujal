package com.example.enotes.util;

import org.apache.commons.io.FilenameUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.enotes.handler.GenericResponse;

public class CommonUtil {

	public static ResponseEntity<?> createBuildResponse(Object data,HttpStatus status)
	{
		GenericResponse response = GenericResponse.builder()
				.responseStatus(status)
				.status("Success")
				.message("Success")
				.data(data)
				.build();
		
		return response.create();
	}
	public static ResponseEntity<?> createBuildResponseMessage(String message,HttpStatus status)
	{
		GenericResponse response = GenericResponse.builder()
				.responseStatus(status)
				.status("Success")
				.message(message)
				.build();
		
		return response.create();
	}
	
	
	public static ResponseEntity<?> createErrorResponse(Object data,HttpStatus status)
	{
		GenericResponse response = GenericResponse.builder()
				.responseStatus(status)
				.status("Failed")
				.message("Failed")
				.data(data)
				.build();
		
		return response.create();
	}
	
	public static ResponseEntity<?> createErrorResponseMessage(String message,HttpStatus status)
	{
		GenericResponse response = GenericResponse.builder()
				.responseStatus(status)
				.status("Failed")
				.message(message)
				.build();
		
		return response.create();
	}

	public static String getContentType(String originalFileName)
	{
		String extension = FilenameUtils.getExtension(originalFileName).toLowerCase();
		
		switch(extension)
		{
		case "pdf":
			return "application/pdf";
		case "xlsx":
			return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
			
		case "txt":
			return "text/plain";
			
		case "png":
			return "image/png";
		case "jpeg":
		case "jpg":
			return "image/jpeg";
			
		default:
			return "application/octet-stream";
		}
	}
}
