package com.assignment.rewardsapi.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data // Generates getters, setters, toString, equals, hashCode
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Represents the detailed reward information for a customer")
public class CustomerDetailsDTO {

    @Schema(description = "Unique identifier of the customer", example = "CUST001")
    private String id;

    @Schema(description = "Name of the customer", example = "John Doe")
    private String customerName;

    @Schema(description = "List of individual transactions for the customer")
    private List<TransactionDTO> transaction;

    @Schema(description = "List of monthly reward points for the customer")
    private List<MonthlyPointsDTO> monthlyPoints;

    @Schema(description = "Total reward points accumulated by the customer", example = "210")
    private int totalPoints;
}