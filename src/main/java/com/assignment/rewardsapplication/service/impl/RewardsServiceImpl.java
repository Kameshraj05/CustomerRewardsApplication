package com.assignment.rewardsapplication.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.assignment.rewardsapplication.dto.Customer;
import com.assignment.rewardsapplication.dto.CustomerDetailsDTO;
import com.assignment.rewardsapplication.dto.CustomerTransactionDTO;
import com.assignment.rewardsapplication.dto.Transaction;
import com.assignment.rewardsapplication.exception.CustomerNotFoundException;
import com.assignment.rewardsapplication.repository.CustomerRepository;
import com.assignment.rewardsapplication.repository.TransactionRepository;
import com.assignment.rewardsapplication.utils.ConversionAndValidatorUtils;
import com.google.gson.Gson;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Service
public class RewardsServiceImpl implements RewardsService {

	private static final Logger log = LogManager.getLogger(RewardsServiceImpl.class);
	private final Gson gson = new Gson();

	@Autowired
	private TransactionRepository transactionRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private ConversionAndValidatorUtils conversionAndValidatorUtils;

	@Override
	@CircuitBreaker(name = "customerService", fallbackMethod = "saveCustomerAndTransactionFallback")
	public void addCustomerTransaction(CustomerTransactionDTO customerTransactionDTO) {
		log.info("In Service layer : " + customerTransactionDTO);

		Customer existingCustomer = customerRepository.findByCustomerId(customerTransactionDTO.getCustomerId());

		if (existingCustomer == null) {
			Customer newCustomer = conversionAndValidatorUtils.prepareCustomer(customerTransactionDTO);
			log.info("Saving new customer : " + newCustomer);
			customerRepository.save(newCustomer);
		}
		Transaction transaction = conversionAndValidatorUtils.prepareTransaction(customerTransactionDTO);
		log.info("Transaction before saving : " + transaction);
		transactionRepository.save(transaction);

		log.info("Transaction saved successfully with {} for customer Id {}", customerTransactionDTO.getTransactionId(),
				customerTransactionDTO.getCustomerId());
	}

	public void saveCustomerAndTransactionFallback(CustomerTransactionDTO dto, Throwable t) {
		log.error("Fallback for saveCustomerAndTransaction. CustomerId: {}, TransactionId: {}, Error: {}",
				dto.getCustomerId(), dto.getTransactionId(), t.getMessage());
	}

	@Override
	@CircuitBreaker(name = "rewardCalculationService", fallbackMethod = "calculateRewardPointsFallback")
	public CustomerDetailsDTO getCustomerRewards(String customerId, LocalDate fromDate, LocalDate toDate) {

		List<Transaction> transactions;

		if (!conversionAndValidatorUtils.isValidCustomerId(customerId)) {
			log.error("Validation failure with customerId: " + customerId);
			throw new IllegalArgumentException("Invalid Customer ID.");
		}

		if (conversionAndValidatorUtils.isValidDates(fromDate, toDate)) {
			log.error("Invalid date range: fromDate = " + fromDate + ", toDate = " + toDate);
			throw new IllegalArgumentException("Invalid date range. From-date should be before to-date.");
		}

		Customer customer = customerRepository.findByCustomerId(customerId);

		if (fromDate == null || toDate == null) {
			transactions = transactionRepository.findByCustomerId(customerId);

		} else {
			LocalDateTime from = fromDate.atStartOfDay();
			LocalDateTime to = toDate.atStartOfDay().plusDays(1).minusNanos(1);
			transactions = transactionRepository.findByCustomerIdAndTransactionDateBetween(customerId, from, to);
		}

		if (customer == null || transactions == null || transactions.isEmpty()) {
			log.error("No transactions found for customerId: " + customerId + " from " + fromDate + " to " + toDate);
			throw new CustomerNotFoundException("No transactions found for the given inputs.");
		}

		CustomerDetailsDTO dto = conversionAndValidatorUtils.prepareCustomerDetailsDTO(customer, transactions);

		log.info("Successfully calculated reward points for customer ID '{}'. DTO: {}", customerId, gson.toJson(dto));
		return dto;
	}

	public void calculateRewardPointsFallback(String customerId, LocalDate fromDate, LocalDate toDate, Throwable t) {
		log.error("Fallback for calculateRewardPoints. CustomerId: {}, FromDate: {}, ToDate: {}, Error: {}", customerId,
				fromDate, toDate, t.getMessage());
	}
}