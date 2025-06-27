package com.assignment.rewardsapplication.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import com.assignment.rewardsapplication.dto.Customer;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class CustomerRepositoryTest {

	@Autowired
	private CustomerRepository customerRepository;

	@Test
	@DisplayName("Should save and retrieve customer by customerId")
	void testFindByCustomerId() {
		Customer customer = new Customer();
		customer.setCustomerId("CUST001");
		customer.setCustomerName("Alice Smith");

		customerRepository.save(customer);
		Customer foundCustomer = customerRepository.findByCustomerId("CUST001");

		assertThat(foundCustomer).isNotNull();
		assertThat(foundCustomer.getCustomerId()).isEqualTo("CUST001");
		assertThat(foundCustomer.getCustomerName()).isEqualTo("Alice Smith");
	}
}
