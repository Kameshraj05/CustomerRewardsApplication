package com.assignment.rewardsapi.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.assignment.rewardsapi.dto.CustomerDetailsDTO;
import com.assignment.rewardsapi.dto.CustomerTransactionDTO;
import com.assignment.rewardsapi.dto.MonthlyPointsDTO;
import com.assignment.rewardsapi.dto.TransactionDTO;
import com.assignment.rewardsapi.exception.CustomerNotFoundException;
import com.assignment.rewardsapi.model.Customer;
import com.assignment.rewardsapi.model.Transaction;
import com.assignment.rewardsapi.repository.CustomerRepository;
import com.assignment.rewardsapi.repository.TransactionRepository;
import com.google.gson.Gson;

@Service
public class RewardsService {
	
	private static final Logger log = LogManager.getLogger(RewardsService.class);
	private final Gson gson = new Gson();

	@Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private CustomerRepository customerRepository;

    public RewardsService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }
    
    public void saveCustomerAndTransaction(CustomerTransactionDTO dto) {
        Customer customer = new Customer();
        customer.setCustomerId(dto.getCustomerId());
        customer.setCustomerName(dto.getCustomerName());

        customerRepository.save(customer);

        Transaction transaction = new Transaction();
        transaction.setCustomerId(dto.getCustomerId());
        transaction.setTransactionId(dto.getTransactionId());
        transaction.setAmount(dto.getAmount());
        transaction.setTransactionDate(dto.getTransactionDate());

        transactionRepository.save(transaction);
    }
    
    public CustomerDetailsDTO calculateRewardPoints(String customerId, LocalDate fromDate, LocalDate toDate) {
        if (!isValidCustomerId(customerId)) {
            log.error("Validation failure with customerId: " + customerId);
            throw new IllegalArgumentException("Invalid Customer ID.");
        }

        if ((fromDate != null && toDate != null) && fromDate.isAfter(toDate)) {
            log.error("Invalid date range: fromDate = " + fromDate + ", toDate = " + toDate);
            throw new IllegalArgumentException("Invalid date range. From-date cannot be after to-date.");
        }

        Customer customer = customerRepository.findByCustomerId(customerId);
        List<Transaction> transactions;

        if (fromDate == null || toDate == null) {
            transactions = transactionRepository.findByCustomerId(customerId);
        } else {
            LocalDateTime from = fromDate.atStartOfDay();
            LocalDateTime to = toDate.atStartOfDay();
            transactions = transactionRepository.findByCustomerIdAndTransactionDateBetween(customerId, from, to);
        }

        if (customer == null || transactions == null || transactions.isEmpty()) {
            log.error("No transactions found for customerId: " + customerId + " from " + fromDate + " to " + toDate);
            throw new CustomerNotFoundException("No transactions found for the given inputs.");
        }

        // Construct DTO
        CustomerDetailsDTO dto = new CustomerDetailsDTO();
        dto.setId(customer.getCustomerId());
        dto.setCustomerName(customer.getCustomerName());

        List<TransactionDTO> transactionDTOs = transactions.stream().map(txn -> {
            TransactionDTO tDto = new TransactionDTO();
            tDto.setCustomerId(txn.getCustomerId());
            tDto.setTransactionId(txn.getTransactionId());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            tDto.setTransactionDate(txn.getTransactionDate().format(formatter));
            tDto.setAmount(txn.getAmount());
            return tDto;
        }).collect(Collectors.toList());

        dto.setTransaction(transactionDTOs);
        dto.setMonthlyPoints(calculateMonthlyPoints(customerId, transactions));
        dto.setTotalPoints(calculateTotalPoints(transactions));

        log.info("DTO Returning: " + gson.toJson(dto));
        return dto;
    }

    //Validate customer id
    public boolean isValidCustomerId(String value) {
        // Check for null or length less than 5
        if (value == null || value.length() != 7) {
            return false;
        }

        // Check if it starts with "CUST" and the rest are digits
        return value.startsWith("CUST") && value.substring(4).matches("\\d+");
    }

    //Calculate reward points based on purchase amount
    private int calculatePoints(double amount) {
        int points = 0;
        if (amount > 100) {
            points += (int) ((amount - 100) * 2); // 2 points over $100
            points += 50; // 1 point for $50–$100
        } else if (amount > 50) {
            points += (int) (amount - 50); // 1 point for $50–amount
        }
        return points;
    }

    //Calculate monthly points and transaction count and returns a map
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


    //Calculate total points based on the over all transactions
    public int calculateTotalPoints(List<Transaction> transactions) {
        return transactions.stream()
                .mapToInt(tx -> calculatePoints(tx.getAmount()))
                .sum();
    }
}

