package com.assignment.rewardsapplication.utils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.assignment.rewardsapplication.dto.MonthlyPointsDTO;
import com.assignment.rewardsapplication.dto.Transaction;

@Component
public class PointsCalculationUtils {

	public int calculateTotalPoints(List<Transaction> transactions) {
		return transactions.stream().mapToInt(tx -> calculatePoints(tx.getAmount())).sum();
	}

	public int calculatePoints(double amount) {
		int points = 0;
		if (amount > 100) {
			points += (int) ((amount - 100) * 2);
			points += 50;
		} else if (amount > 50) {
			points += (int) (amount - 50);
		}
		return points;
	}

	public List<MonthlyPointsDTO> calculateMonthlyPoints(String customerId, List<Transaction> transactions) {
		Map<String, MonthlyPointsDTO> monthlyPointsMap = new LinkedHashMap<>();

		for (Transaction transaction : transactions) {
			LocalDate transactionDate = transaction.getTransactionDate().toLocalDate();
			int points = calculatePoints(transaction.getAmount());

			String monthKey = transactionDate.getYear() + "-" + transactionDate.getMonth().toString();

			MonthlyPointsDTO monthlyPointDTO = monthlyPointsMap.get(monthKey);

			if (monthlyPointDTO == null) {
				monthlyPointDTO = new MonthlyPointsDTO();
				monthlyPointDTO.setYear(transactionDate.getYear());
				monthlyPointDTO.setMonth(transactionDate.getMonth().toString());
				monthlyPointDTO.setPoints(points);
			} else {
				monthlyPointDTO.setPoints(monthlyPointDTO.getPoints() + points);
			}

			monthlyPointsMap.put(monthKey, monthlyPointDTO);
		}

		return new ArrayList<>(monthlyPointsMap.values());
	}

}