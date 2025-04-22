package com.assignment.rewardsapi.repository;

import com.assignment.rewardsapi.model.Customer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    @DisplayName("Should save and retrieve customer by customerId")
    void testFindByCustomerId() {
        // Arrange
        Customer customer = new Customer();
        customer.setCustomerId("CUST001");
        customer.setCustomerName("Alice Smith");

        customerRepository.save(customer);

        // Act
        Customer foundCustomer = customerRepository.findByCustomerId("CUST001");

        // Assert
        assertThat(foundCustomer).isNotNull();
        assertThat(foundCustomer.getCustomerId()).isEqualTo("CUST001");
        assertThat(foundCustomer.getCustomerName()).isEqualTo("Alice Smith");
    }
}
