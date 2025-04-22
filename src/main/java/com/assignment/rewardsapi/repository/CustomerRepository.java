package com.assignment.rewardsapi.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.assignment.rewardsapi.model.Customer;

public interface CustomerRepository extends MongoRepository<Customer, String> {
	
	Customer findByCustomerId(String customerId);
}
