package com.assignment.rewardsapi.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data // Generates getters, setters, toString, equals, hashCode
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Represents a single transaction record")
public class TransactionDTO {

    @Schema(description = "Unique identifier for the transaction", example = "TXN123")
    private String transactionId;

    @Schema(description = "Identifier of the customer who made the transaction", example = "CUST001")
    private String customerId;

    @Schema(description = "Amount of the transaction", example = "75.50")
    private double amount;

    @Schema(description = "Date of the transaction (YYYY-MM-DD)", example = "2024-05-14")
    private String transactionDate;
}