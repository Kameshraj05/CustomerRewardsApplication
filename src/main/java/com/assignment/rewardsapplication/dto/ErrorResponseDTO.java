package com.assignment.rewardsapplication.dto;

import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseDTO {

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
	private LocalDateTime timestamp;
	private HttpStatus status;
	private int statusCode;
	private String error;
	private String message;

	public ErrorResponseDTO(HttpStatus status, String message) {
		this.timestamp = LocalDateTime.now();
		this.status = status;
		this.statusCode = status.value();
		this.error = status.getReasonPhrase();
		this.message = message;
	}
}