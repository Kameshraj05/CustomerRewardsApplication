package com.assignment.rewardsapplication.service.impl;

import com.assignment.rewardsapplication.dto.CustomerDetailsDTO;
import com.assignment.rewardsapplication.dto.CustomerTransactionDTO;

import java.time.LocalDate;

public interface RewardsService {
	void addCustomerTransaction(CustomerTransactionDTO dto);

	CustomerDetailsDTO getCustomerRewards(String customerId, LocalDate fromDate, LocalDate toDate);
}