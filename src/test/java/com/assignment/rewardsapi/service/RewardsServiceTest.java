package com.assignment.rewardsapi.service;

import com.assignment.rewardsapi.dto.CustomerDetailsDTO;
import com.assignment.rewardsapi.dto.CustomerTransactionDTO;
import com.assignment.rewardsapi.dto.MonthlyPointsDTO;
import com.assignment.rewardsapi.exception.CustomerNotFoundException;
import com.assignment.rewardsapi.model.Customer;
import com.assignment.rewardsapi.model.Transaction;
import com.assignment.rewardsapi.repository.CustomerRepository;
import com.assignment.rewardsapi.repository.TransactionRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class RewardsServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private RewardsService rewardsService;

    @SuppressWarnings("deprecation")
	@BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSaveCustomerAndTransaction() {
        CustomerTransactionDTO dto = new CustomerTransactionDTO();
        dto.setCustomerId("CUST123");
        dto.setCustomerName("Test Customer");
        dto.setTransactionId("TX123");
        dto.setAmount(100.0);
        dto.setTransactionDate(LocalDateTime.now());

        Customer savedCustomer = new Customer();
        savedCustomer.setCustomerId(dto.getCustomerId());
        savedCustomer.setCustomerName(dto.getCustomerName());

        Transaction savedTransaction = new Transaction();
        savedTransaction.setTransactionId(dto.getTransactionId());
        savedTransaction.setCustomerId(dto.getCustomerId());
        savedTransaction.setAmount(dto.getAmount());
        savedTransaction.setTransactionDate(dto.getTransactionDate());

        when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

        rewardsService.saveCustomerAndTransaction(dto);
    }

    @Test
    public void testCalculateRewardPoints_ExistingCustomerWithTransactions() {
        String customerId = "CUST001";
        LocalDate fromDate = LocalDate.parse("2025-01-01");
        LocalDate toDate = LocalDate.parse("2025-03-31");

        Customer customer = new Customer();
        customer.setCustomerId(customerId);
        customer.setCustomerName("Alice Smith");

        List<Transaction> transactions = new ArrayList<>();
        Transaction transaction1 = new Transaction();
        transaction1.setTransactionId("TXN001");
        transaction1.setCustomerId(customerId);
        transaction1.setAmount(120.0);
        transaction1.setTransactionDate(LocalDateTime.parse("2025-01-15T10:00:00"));

        Transaction transaction2 = new Transaction();
        transaction2.setTransactionId("TXN002");
        transaction2.setCustomerId(customerId);
        transaction2.setAmount(75.0);
        transaction2.setTransactionDate(LocalDateTime.parse("2025-02-20T14:30:00"));

        transactions.add(transaction1);
        transactions.add(transaction2);

        when(customerRepository.findByCustomerId(customerId)).thenReturn(customer);
        when(transactionRepository.findByCustomerIdAndTransactionDateBetween(customerId, fromDate.atStartOfDay(), toDate.atStartOfDay()))
                .thenReturn(transactions);

        CustomerDetailsDTO result = rewardsService.calculateRewardPoints(customerId, fromDate, toDate);

        assertEquals(customerId, result.getId());
        assertEquals("Alice Smith", result.getCustomerName());
        assertEquals(2, result.getTransaction().size());
        assertEquals(90 + 25, result.getTotalPoints());

        List<MonthlyPointsDTO> monthlyPoints = result.getMonthlyPoints();
        assertEquals(2, monthlyPoints.size());
        assertEquals(2025, monthlyPoints.get(0).getYear());
        assertEquals("JANUARY", monthlyPoints.get(0).getMonth());
        assertEquals(90, monthlyPoints.get(0).getPoints());
        assertEquals(2025, monthlyPoints.get(1).getYear());
        assertEquals("FEBRUARY", monthlyPoints.get(1).getMonth());
        assertEquals(25, monthlyPoints.get(1).getPoints());
    }

    @Test
    public void testCalculateRewardPoints_CustomerNotFound() {
        String customerId = "CUST123";
        LocalDate fromDate = LocalDate.parse("2025-01-01");
        LocalDate toDate = LocalDate.parse("2025-03-31");

        when(customerRepository.findByCustomerId(customerId)).thenReturn(null);

        Exception exception = assertThrows(CustomerNotFoundException.class, () -> {
            rewardsService.calculateRewardPoints(customerId, fromDate, toDate);
        });

        assertEquals("No transactions found for the given inputs.", exception.getMessage());
    }

    @Test
    public void testCalculateRewardPoints_InvalidCustomerIdFormat() {
        String invalidCustomerId = "123";
        LocalDate fromDate = LocalDate.parse("2025-01-01");
        LocalDate toDate = LocalDate.parse("2025-03-31");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            rewardsService.calculateRewardPoints(invalidCustomerId, fromDate, toDate);
        });

        assertEquals("Invalid Customer ID.", exception.getMessage());
    }

    @Test
    public void testCalculateRewardPoints_InvalidDateRange() {
        String customerId = "CUST001";
        LocalDate fromDate = LocalDate.parse("2025-03-31");
        LocalDate toDate = LocalDate.parse("2025-01-01");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            rewardsService.calculateRewardPoints(customerId, fromDate, toDate);
        });

        assertEquals("Invalid date range. From-date cannot be after to-date.", exception.getMessage());
    }

    // -------------------- Circuit Breaker Tests --------------------

    @Test
    public void testCalculateRewardPoints_CustomerRepositoryThrowsException() {
        String customerId = "CUST001";
        LocalDate fromDate = LocalDate.of(2025, 1, 1);
        LocalDate toDate = LocalDate.of(2025, 3, 31);

        when(customerRepository.findByCustomerId(customerId)).thenThrow(new RuntimeException("Database unavailable"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            rewardsService.calculateRewardPoints(customerId, fromDate, toDate);
        });

        assertEquals("Database unavailable", exception.getMessage());
    }

    @Test
    public void testCalculateRewardPoints_TransactionRepositoryThrowsException() {
        String customerId = "CUST001";
        LocalDate fromDate = LocalDate.of(2025, 1, 1);
        LocalDate toDate = LocalDate.of(2025, 3, 31);

        Customer customer = new Customer();
        customer.setCustomerId(customerId);
        customer.setCustomerName("Test");

        when(customerRepository.findByCustomerId(customerId)).thenReturn(customer);
        when(transactionRepository.findByCustomerIdAndTransactionDateBetween(
                eq(customerId),
                eq(fromDate.atStartOfDay()),
                eq(toDate.atStartOfDay())
        )).thenThrow(new RuntimeException("Transaction DB failure"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            rewardsService.calculateRewardPoints(customerId, fromDate, toDate);
        });

        assertEquals("Transaction DB failure", exception.getMessage());
    }

    @Test
    public void testSaveCustomerAndTransaction_CustomerRepositoryThrowsException() {
        CustomerTransactionDTO dto = new CustomerTransactionDTO();
        dto.setCustomerId("CUST123");
        dto.setCustomerName("John Doe");
        dto.setTransactionId("TXN001");
        dto.setAmount(150.0);
        dto.setTransactionDate(LocalDateTime.now());

        when(customerRepository.save(any(Customer.class))).thenThrow(new RuntimeException("Customer DB not reachable"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            rewardsService.saveCustomerAndTransaction(dto);
        });

        assertEquals("Customer DB not reachable", exception.getMessage());
    }

    @Test
    public void testSaveCustomerAndTransaction_TransactionRepositoryThrowsException() {
        CustomerTransactionDTO dto = new CustomerTransactionDTO();
        dto.setCustomerId("CUST123");
        dto.setCustomerName("John Doe");
        dto.setTransactionId("TXN001");
        dto.setAmount(150.0);
        dto.setTransactionDate(LocalDateTime.now());

        Customer customer = new Customer();
        customer.setCustomerId(dto.getCustomerId());
        customer.setCustomerName(dto.getCustomerName());

        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        when(transactionRepository.save(any(Transaction.class))).thenThrow(new RuntimeException("Transaction DB error"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            rewardsService.saveCustomerAndTransaction(dto);
        });

        assertEquals("Transaction DB error", exception.getMessage());
    }
}
