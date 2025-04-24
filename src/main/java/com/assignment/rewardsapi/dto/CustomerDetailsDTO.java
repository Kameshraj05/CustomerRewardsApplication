package com.assignment.rewardsapi.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Generates getters, setters, toString, equals, hashCode
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDetailsDTO {
    private String id;
    private String customerName;
    private List<TransactionDTO> transaction;
    private List<MonthlyPointsDTO> monthlyPoints;
    private int totalPoints;

}
