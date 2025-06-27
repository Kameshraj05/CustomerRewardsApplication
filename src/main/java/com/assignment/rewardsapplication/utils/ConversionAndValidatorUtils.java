package com.assignment.rewardsapplication.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.assignment.rewardsapplication.dto.Customer;
import com.assignment.rewardsapplication.dto.CustomerDetailsDTO;
import com.assignment.rewardsapplication.dto.CustomerTransactionDTO;
import com.assignment.rewardsapplication.dto.Transaction;
import com.assignment.rewardsapplication.dto.TransactionDTO;

@Component
public class ConversionAndValidatorUtils {

	@Autowired
	private PointsCalculationUtils pointsCalculationUtils;

	public TransactionDTO convertToTransactionDTO(Transaction transaction) {
		TransactionDTO transactionDTO = new TransactionDTO();
		transactionDTO.setCustomerId(transaction.getCustomerId());
		transactionDTO.setTransactionId(transaction.getTransactionId());
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		transactionDTO.setTransactionDate(transaction.getTransactionDate().format(formatter));
		transactionDTO.setAmount(transaction.getAmount());
		return transactionDTO;
	}

	public CustomerDetailsDTO prepareCustomerDetailsDTO(Customer customer, List<Transaction> transactions) {

		CustomerDetailsDTO customerDetailsDTO = new CustomerDetailsDTO();
		customerDetailsDTO.setId(customer.getCustomerId());
		customerDetailsDTO.setCustomerName(customer.getCustomerName());

		List<TransactionDTO> transactionDTOs = transactions.stream().map(this::convertToTransactionDTO)
				.collect(Collectors.toList());
		customerDetailsDTO.setTransaction(transactionDTOs);
		customerDetailsDTO.setMonthlyPoints(
				pointsCalculationUtils.calculateMonthlyPoints(customer.getCustomerId(), transactions));
		customerDetailsDTO.setTotalPoints(pointsCalculationUtils.calculateTotalPoints(transactions));

		return customerDetailsDTO;
	}

	public Customer prepareCustomer(CustomerTransactionDTO customerTransactionDTO) {

		Customer customer = new Customer();
		customer.setCustomerId(customerTransactionDTO.getCustomerId());
		customer.setCustomerName(customerTransactionDTO.getCustomerName());
		return customer;
	}

	public Transaction prepareTransaction(CustomerTransactionDTO customerTransactionDTO) {

		Transaction transaction = new Transaction();
		transaction.setCustomerId(customerTransactionDTO.getCustomerId());
		transaction.setTransactionId(customerTransactionDTO.getTransactionId());
		transaction.setAmount(customerTransactionDTO.getAmount());
		transaction.setTransactionDate(customerTransactionDTO.getTransactionDate());

		return transaction;
	}

	public boolean isValidCustomerId(String customerId) {
		if (customerId == null || customerId.length() != 7)
			return false;
		return customerId.startsWith("CUST") && customerId.substring(4).matches("\\d+");
	}

	public boolean isValidDates(LocalDate fromDate, LocalDate toDate) {

		if ((fromDate != null && toDate == null) || (fromDate == null && toDate != null))
			return true;
		if ((fromDate != null && toDate != null) && fromDate.isAfter(toDate))
			return true;
		else
			return false;
	}
}