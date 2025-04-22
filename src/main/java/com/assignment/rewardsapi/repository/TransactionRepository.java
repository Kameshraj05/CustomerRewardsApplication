package com.assignment.rewardsapi.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.assignment.rewardsapi.model.Transaction;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends MongoRepository<Transaction, String> {
    List<Transaction> findByCustomerId(String customerId);

    // Optimized query to get only recent transactions (last 3 months)
    List<Transaction> findByCustomerIdAndTransactionDateBetween(String customerId, LocalDateTime fromDate, LocalDateTime toDate);
    
}
