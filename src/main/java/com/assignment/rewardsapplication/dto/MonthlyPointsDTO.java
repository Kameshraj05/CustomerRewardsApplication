package com.assignment.rewardsapplication.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Represents the reward points earned in a specific month")
public class MonthlyPointsDTO {

	@Schema(description = "Year of the month", example = "2025")
	private int year;

	@Schema(description = "Month of the year", example = "MAY")
	private String month;

	@Schema(description = "Total reward points earned in that month", example = "75")
	private int points;
}