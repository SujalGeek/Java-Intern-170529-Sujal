package com.example.ai_integration_service.dto;

import lombok.Data;

@Data
public class MidTermRequest {
	
	private Long courseId;
	private int easyCount;
	private int mediumCount;
	private int hardCount;

}
