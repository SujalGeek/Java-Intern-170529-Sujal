package com.example.enotes.handler;

import java.util.List;

import com.example.enotes.dto.NotesDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotesResponse {

	private List<NotesDto> notes;
	
	private Integer pageNo;
	
	private Integer pageSize;
	
	private Long totalElements;
	
	private Integer totalPages;
	
	private Boolean isFirst;
	
	private Boolean isLast;
	
}
