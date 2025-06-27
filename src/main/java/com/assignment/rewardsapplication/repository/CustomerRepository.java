package com.assignment.rewardsapplication.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.assignment.rewardsapplication.dto.Customer;

public interface CustomerRepository extends MongoRepository<Customer, String> {

	Customer findByCustomerId(String customerId);

}