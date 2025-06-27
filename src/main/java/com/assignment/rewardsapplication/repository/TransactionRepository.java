package com.assignment.rewardsapplication.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.assignment.rewardsapplication.dto.Transaction;

@Repository
public interface TransactionRepository extends MongoRepository<Transaction, String> {
	List<Transaction> findByCustomerId(String customerId);

	List<Transaction> findByCustomerIdAndTransactionDateBetween(String customerId, LocalDateTime fromDate,
			LocalDateTime toDate);

}