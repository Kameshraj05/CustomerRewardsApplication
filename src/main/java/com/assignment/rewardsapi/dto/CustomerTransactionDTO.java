package com.assignment.rewardsapi.dto;


import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Generates getters, setters, toString, equals, hashCode
@NoArgsConstructor
@AllArgsConstructor
public class CustomerTransactionDTO {

    private String customerId;
    private String customerName;
    private String transactionId;
    private Double amount;
    private LocalDateTime transactionDate;
}
