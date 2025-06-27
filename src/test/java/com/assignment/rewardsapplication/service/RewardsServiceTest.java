package com.assignment.rewardsapplication.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import com.assignment.rewardsapplication.dto.Customer;
import com.assignment.rewardsapplication.dto.CustomerDetailsDTO;
import com.assignment.rewardsapplication.dto.CustomerTransactionDTO;
import com.assignment.rewardsapplication.dto.MonthlyPointsDTO;
import com.assignment.rewardsapplication.dto.Transaction;
import com.assignment.rewardsapplication.dto.TransactionDTO;
import com.assignment.rewardsapplication.exception.CustomerNotFoundException;
import com.assignment.rewardsapplication.repository.CustomerRepository;
import com.assignment.rewardsapplication.repository.TransactionRepository;
import com.assignment.rewardsapplication.service.impl.RewardsServiceImpl;
import com.assignment.rewardsapplication.utils.ConversionAndValidatorUtils;
import com.assignment.rewardsapplication.utils.PointsCalculationUtils;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class RewardsServiceTest {

	@MockBean
	private TransactionRepository transactionRepository;

	@MockBean
	private CustomerRepository customerRepository;

	@MockBean
	private ConversionAndValidatorUtils conversionAndValidatorUtils;

	@MockBean
	private PointsCalculationUtils pointsCalculationUtils;

	@Autowired
	private RewardsServiceImpl rewardsService;

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	@Test
	public void testSaveCustomerAndTransaction() {
		CustomerTransactionDTO dto = new CustomerTransactionDTO();
		dto.setCustomerId("CUST123");
		dto.setCustomerName("Test Customer");
		dto.setTransactionId("TX123");
		dto.setAmount(100.0);
		dto.setTransactionDate(LocalDateTime.now());

		Customer newCustomer = new Customer();
		newCustomer.setCustomerId(dto.getCustomerId());
		newCustomer.setCustomerName(dto.getCustomerName());

		Transaction newTransaction = new Transaction();
		newTransaction.setTransactionId(dto.getTransactionId());
		newTransaction.setCustomerId(dto.getCustomerId());
		newTransaction.setAmount(dto.getAmount());
		newTransaction.setTransactionDate(dto.getTransactionDate());

		when(customerRepository.findByCustomerId(dto.getCustomerId())).thenReturn(null);
		when(conversionAndValidatorUtils.prepareCustomer(dto)).thenReturn(newCustomer);
		when(conversionAndValidatorUtils.prepareTransaction(dto)).thenReturn(newTransaction);
		when(customerRepository.save(any(Customer.class))).thenReturn(newCustomer);
		when(transactionRepository.save(any(Transaction.class))).thenReturn(newTransaction);

		rewardsService.addCustomerTransaction(dto);

		verify(customerRepository, times(1)).findByCustomerId(dto.getCustomerId());
		verify(conversionAndValidatorUtils, times(1)).prepareCustomer(dto);
		verify(customerRepository, times(1)).save(newCustomer);
		verify(conversionAndValidatorUtils, times(1)).prepareTransaction(dto);
		verify(transactionRepository, times(1)).save(newTransaction);
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

		List<TransactionDTO> transactionDTOs = new ArrayList<>();
		TransactionDTO transactionDTO1 = new TransactionDTO();
		transactionDTO1.setTransactionId("TXN001");
		transactionDTO1.setCustomerId(customerId);
		transactionDTO1.setAmount(120.0);
		transactionDTO1.setTransactionDate(transaction1.getTransactionDate().format(DATE_FORMATTER));

		TransactionDTO transactionDTO2 = new TransactionDTO();
		transactionDTO2.setTransactionId("TXN002");
		transactionDTO2.setCustomerId(customerId);
		transactionDTO2.setAmount(75.0);
		transactionDTO2.setTransactionDate(transaction2.getTransactionDate().format(DATE_FORMATTER));

		transactionDTOs.add(transactionDTO1);
		transactionDTOs.add(transactionDTO2);

		when(conversionAndValidatorUtils.isValidCustomerId(customerId)).thenReturn(true);
		when(conversionAndValidatorUtils.isValidDates(fromDate, toDate)).thenReturn(false);
		when(customerRepository.findByCustomerId(customerId)).thenReturn(customer);
		when(transactionRepository.findByCustomerIdAndTransactionDateBetween(eq(customerId), any(LocalDateTime.class),
				any(LocalDateTime.class))).thenReturn(transactions);

		when(conversionAndValidatorUtils.convertToTransactionDTO(transaction1)).thenReturn(transactionDTO1);
		when(conversionAndValidatorUtils.convertToTransactionDTO(transaction2)).thenReturn(transactionDTO2);

		when(pointsCalculationUtils.calculatePoints(120.0)).thenReturn(90);
		when(pointsCalculationUtils.calculatePoints(75.0)).thenReturn(25);
		when(pointsCalculationUtils.calculateTotalPoints(transactions)).thenReturn(90 + 25);
		List<MonthlyPointsDTO> monthlyPointsList = new ArrayList<>();
		monthlyPointsList.add(new MonthlyPointsDTO(2025, "JANUARY", 90));
		monthlyPointsList.add(new MonthlyPointsDTO(2025, "FEBRUARY", 25));
		when(pointsCalculationUtils.calculateMonthlyPoints(customerId, transactions)).thenReturn(monthlyPointsList);

		CustomerDetailsDTO expectedDto = new CustomerDetailsDTO();
		expectedDto.setId(customerId);
		expectedDto.setCustomerName("Alice Smith");
		expectedDto.setTotalPoints(90 + 25);
		expectedDto.setTransaction(transactionDTOs);
		expectedDto.setMonthlyPoints(monthlyPointsList);
		when(conversionAndValidatorUtils.prepareCustomerDetailsDTO(eq(customer), eq(transactions)))
				.thenReturn(expectedDto);

		CustomerDetailsDTO result = rewardsService.getCustomerRewards(customerId, fromDate, toDate);

		assertNotNull(result);
		assertEquals(customerId, result.getId());
		assertEquals("Alice Smith", result.getCustomerName());
		assertEquals(2, result.getTransaction().size());
		assertEquals(90 + 25, result.getTotalPoints());

		List<MonthlyPointsDTO> monthlyPoints = result.getMonthlyPoints();
		assertNotNull(monthlyPoints);
		assertEquals(2, monthlyPoints.size());
		assertEquals(2025, monthlyPoints.get(0).getYear());
		assertEquals("JANUARY", monthlyPoints.get(0).getMonth());
		assertEquals(90, monthlyPoints.get(0).getPoints());
		assertEquals(2025, monthlyPoints.get(1).getYear());
		assertEquals("FEBRUARY", monthlyPoints.get(1).getMonth());
		assertEquals(25, monthlyPoints.get(1).getPoints());

		verify(conversionAndValidatorUtils, times(1)).isValidCustomerId(customerId);
		verify(conversionAndValidatorUtils, times(1)).isValidDates(fromDate, toDate);
		verify(customerRepository, times(1)).findByCustomerId(customerId);
		verify(transactionRepository, times(1)).findByCustomerIdAndTransactionDateBetween(eq(customerId),
				any(LocalDateTime.class), any(LocalDateTime.class));

		verify(conversionAndValidatorUtils, times(1)).prepareCustomerDetailsDTO(customer, transactions);
	}

	@Test
	public void testCalculateRewardPoints_CustomerNotFound() {
		String customerId = "CUST123";
		LocalDate fromDate = LocalDate.parse("2025-01-01");
		LocalDate toDate = LocalDate.parse("2025-03-31");

		when(conversionAndValidatorUtils.isValidCustomerId(customerId)).thenReturn(true);
		when(conversionAndValidatorUtils.isValidDates(fromDate, toDate)).thenReturn(false);
		when(customerRepository.findByCustomerId(customerId)).thenReturn(null);

		when(transactionRepository.findByCustomerIdAndTransactionDateBetween(eq(customerId), any(LocalDateTime.class),
				any(LocalDateTime.class))).thenReturn(Collections.emptyList());

		Exception exception = assertThrows(CustomerNotFoundException.class, () -> {
			rewardsService.getCustomerRewards(customerId, fromDate, toDate);
		});

		assertEquals("No transactions found for the given inputs.", exception.getMessage());
		verify(customerRepository, times(1)).findByCustomerId(customerId);
		verify(transactionRepository, times(1)).findByCustomerIdAndTransactionDateBetween(eq(customerId),
				any(LocalDateTime.class), any(LocalDateTime.class));
		verify(conversionAndValidatorUtils, never()).prepareCustomerDetailsDTO(any(Customer.class), anyList());
	}

	@Test
	public void testCalculateRewardPoints_InvalidCustomerIdFormat() {
		String invalidCustomerId = "123";
		LocalDate fromDate = LocalDate.parse("2025-01-01");
		LocalDate toDate = LocalDate.parse("2025-03-31");

		when(conversionAndValidatorUtils.isValidCustomerId(invalidCustomerId)).thenReturn(false);

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			rewardsService.getCustomerRewards(invalidCustomerId, fromDate, toDate);
		});

		assertEquals("Invalid Customer ID.", exception.getMessage());
		verify(conversionAndValidatorUtils, times(1)).isValidCustomerId(invalidCustomerId);
		verify(customerRepository, never()).findByCustomerId(anyString());
	}

	@Test
	public void testCalculateRewardPoints_InvalidDateRange() {
		String customerId = "CUST001";
		LocalDate fromDate = LocalDate.parse("2025-03-31");
		LocalDate toDate = LocalDate.parse("2025-01-01");

		when(conversionAndValidatorUtils.isValidCustomerId(customerId)).thenReturn(true);
		when(conversionAndValidatorUtils.isValidDates(fromDate, toDate)).thenReturn(true);

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			rewardsService.getCustomerRewards(customerId, fromDate, toDate);
		});

		assertEquals("Invalid date range. From-date should be before to-date.", exception.getMessage());
		verify(conversionAndValidatorUtils, times(1)).isValidCustomerId(customerId);
		verify(conversionAndValidatorUtils, times(1)).isValidDates(fromDate, toDate);
		verify(customerRepository, never()).findByCustomerId(anyString());
	}

	@Test
	public void testCalculateRewardPoints_CustomerRepositoryThrowsException() {
		String customerId = "CUST001";
		LocalDate fromDate = LocalDate.of(2025, 1, 1);
		LocalDate toDate = LocalDate.of(2025, 3, 31);

		when(conversionAndValidatorUtils.isValidCustomerId(customerId)).thenReturn(true);
		when(conversionAndValidatorUtils.isValidDates(fromDate, toDate)).thenReturn(false);

		when(customerRepository.findByCustomerId(customerId)).thenThrow(new RuntimeException("Database unavailable"));

		Exception exception = assertThrows(RuntimeException.class, () -> {
			rewardsService.getCustomerRewards(customerId, fromDate, toDate);
		});

		assertEquals("Database unavailable", exception.getMessage());

		verify(conversionAndValidatorUtils, times(1)).isValidCustomerId(customerId);
		verify(conversionAndValidatorUtils, times(1)).isValidDates(fromDate, toDate);
		verify(customerRepository, times(1)).findByCustomerId(customerId);
	}

	@Test
	public void testCalculateRewardPoints_TransactionRepositoryThrowsException() {
		String customerId = "CUST001";
		LocalDate fromDate = LocalDate.of(2025, 1, 1);
		LocalDate toDate = LocalDate.of(2025, 3, 31);

		Customer customer = new Customer();
		customer.setCustomerId(customerId);
		customer.setCustomerName("Test Customer");

		when(conversionAndValidatorUtils.isValidCustomerId(customerId)).thenReturn(true);
		when(conversionAndValidatorUtils.isValidDates(fromDate, toDate)).thenReturn(false);
		when(customerRepository.findByCustomerId(customerId)).thenReturn(customer);

		when(transactionRepository.findByCustomerIdAndTransactionDateBetween(eq(customerId), any(LocalDateTime.class),
				any(LocalDateTime.class))).thenThrow(new RuntimeException("Transaction DB failure"));

		Exception exception = assertThrows(RuntimeException.class, () -> {
			rewardsService.getCustomerRewards(customerId, fromDate, toDate);
		});

		assertEquals("Transaction DB failure", exception.getMessage());

		verify(conversionAndValidatorUtils, times(1)).isValidCustomerId(customerId);
		verify(conversionAndValidatorUtils, times(1)).isValidDates(fromDate, toDate);
		verify(customerRepository, times(1)).findByCustomerId(customerId);
		verify(transactionRepository, times(1)).findByCustomerIdAndTransactionDateBetween(eq(customerId),
				any(LocalDateTime.class), any(LocalDateTime.class));
	}

	@Test
	public void testSaveCustomerAndTransaction_CustomerRepositoryThrowsException() {
		CustomerTransactionDTO dto = new CustomerTransactionDTO();
		dto.setCustomerId("CUST123");
		dto.setCustomerName("John Doe");
		dto.setTransactionId("TXN001");
		dto.setAmount(150.0);
		dto.setTransactionDate(LocalDateTime.now());

		when(customerRepository.findByCustomerId(dto.getCustomerId())).thenReturn(null);
		when(conversionAndValidatorUtils.prepareCustomer(dto)).thenReturn(new Customer());

		when(customerRepository.save(any(Customer.class))).thenThrow(new RuntimeException("Customer DB not reachable"));

		Exception exception = assertThrows(RuntimeException.class, () -> {
			rewardsService.addCustomerTransaction(dto);
		});

		assertEquals("Customer DB not reachable", exception.getMessage());

		verify(customerRepository, times(1)).findByCustomerId(dto.getCustomerId());
		verify(conversionAndValidatorUtils, times(1)).prepareCustomer(dto);
		verify(customerRepository, times(1)).save(any(Customer.class));
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

		when(customerRepository.findByCustomerId(dto.getCustomerId())).thenReturn(customer);
		when(conversionAndValidatorUtils.prepareTransaction(dto)).thenReturn(new Transaction());

		when(transactionRepository.save(any(Transaction.class)))
				.thenThrow(new RuntimeException("Transaction DB error"));

		Exception exception = assertThrows(RuntimeException.class, () -> {
			rewardsService.addCustomerTransaction(dto);
		});

		assertEquals("Transaction DB error", exception.getMessage());

		verify(customerRepository, times(1)).findByCustomerId(dto.getCustomerId());
		verify(conversionAndValidatorUtils, times(1)).prepareTransaction(dto);
		verify(transactionRepository, times(1)).save(any(Transaction.class));
	}
}
