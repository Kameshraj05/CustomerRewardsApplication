package com.assignment.rewardsapplication.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Represents the details for creating a new customer and their transaction")
public class CustomerTransactionDTO {

	@Schema(description = "Unique identifier for the customer", example = "CUST001")
	@NotBlank(message = "Customer ID is required and cannot be empty.")
	@Size(min = 7, max = 7, message = "Customer ID must be 7 characters long.")
	@Pattern(regexp = "^CUST\\d{3}$", message = "Customer ID must start with 'CUST' followed by 3 digits (e.g., CUST001).")
	private String customerId;

	@Schema(description = "Name of the customer", example = "Kamesh Raj T")
	@NotBlank(message = "Customer name is required and cannot be empty.")
	private String customerName;

	@Schema(description = "Unique identifier for the transaction", example = "TRANS123")
	@NotBlank(message = "Transaction ID is required and cannot be empty.")
	private String transactionId;

	@Schema(description = "Amount of the transaction", example = "75.50")
	@NotNull(message = "Amount is required.")
	@Positive(message = "Amount must be a positive value.")
	private Double amount;

	@Schema(description = "Date and time of the transaction", example = "2024-05-14T15:45:00")
	@NotNull(message = "Transaction date is required.")
	private LocalDateTime transactionDate;
}