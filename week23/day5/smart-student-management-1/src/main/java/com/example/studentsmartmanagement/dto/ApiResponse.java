package com.example.studentsmartmanagement.dto;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {

	private boolean success;
	private String message;
	private T data;
	private LocalDateTime timestamp;
	private int status;
	
	public static<T> ApiResponse<T> success(T data,String message,HttpStatus status)
	{
		return ApiResponse.<T>builder()
				.success(true)
				.message(message)
				.data(data)
				.status(status.value())
				.timestamp(LocalDateTime.now())
				.build();
	}
	
	public static <T> ApiResponse<T> error(String message, HttpStatus status) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .data(null)
                .status(status.value())
                .timestamp(LocalDateTime.now())
                .build();
	}
}
