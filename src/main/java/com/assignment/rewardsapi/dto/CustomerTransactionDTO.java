package com.assignment.rewardsapi.dto;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data // Generates getters, setters, toString, equals, hashCode
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Represents the details for creating a new customer and their transaction")
public class CustomerTransactionDTO {

    @Schema(description = "Unique identifier for the customer", example = "CUST001")
    private String customerId;

    @Schema(description = "Name of the customer", example = "John Doe")
    private String customerName;

    @Schema(description = "Unique identifier for the transaction", example = "TRANS123")
    private String transactionId;

    @Schema(description = "Amount of the transaction", example = "75.50")
    private Double amount;

    @Schema(description = "Date and time of the transaction", example = "2024-05-14T15:45:00")
    private LocalDateTime transactionDate;
}