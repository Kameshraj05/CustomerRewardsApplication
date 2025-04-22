package com.assignment.rewardsapi.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Generates getters, setters, toString, equals, hashCode
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyPointsDTO {
    private int year;
    private String month;
    private int points;

}
