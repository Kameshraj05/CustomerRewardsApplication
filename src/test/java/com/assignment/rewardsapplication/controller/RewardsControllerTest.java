package com.assignment.rewardsapplication.controller;

import com.assignment.rewardsapplication.dto.CustomerDetailsDTO;
import com.assignment.rewardsapplication.dto.CustomerTransactionDTO;
import com.assignment.rewardsapplication.exception.CustomerNotFoundException;
import com.assignment.rewardsapplication.service.impl.RewardsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class RewardsControllerTest {

	@Mock
	private RewardsService rewardsService;

	@InjectMocks
	private RewardsController rewardsController;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		MockHttpServletRequest request = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
	}

	@Test
	public void testSaveCustomerTransaction() {
		CustomerTransactionDTO dto = new CustomerTransactionDTO();
		ResponseEntity<String> response = rewardsController.addCustomerTransaction(dto);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("Customer and Transaction saved successfully!", response.getBody());
	}

	@Test
	public void testGetCustomerReward_Success() {
		String customerId = "CUST001";
		LocalDate fromDate = LocalDate.parse("2025-01-01");
		LocalDate toDate = LocalDate.parse("2025-03-31");

		CustomerDetailsDTO expectedDto = new CustomerDetailsDTO();
		expectedDto.setId(customerId);

		when(rewardsService.getCustomerRewards(customerId, fromDate, toDate)).thenReturn(expectedDto);

		ResponseEntity<CustomerDetailsDTO> response = rewardsController.getCustomerReward(customerId, fromDate, toDate);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(expectedDto, response.getBody());
	}

	@Test
	public void testGetCustomerReward_CustomerNotFound() {
		String customerId = "CUST123";
		LocalDate fromDate = LocalDate.parse("2024-01-01");
		LocalDate toDate = LocalDate.parse("2024-03-31");
		String expectedMessage = "No transactions found for the given inputs.";

		when(rewardsService.getCustomerRewards(customerId, fromDate, toDate))
				.thenThrow(new CustomerNotFoundException(expectedMessage));

		CustomerNotFoundException thrown = assertThrows(CustomerNotFoundException.class,
				() -> rewardsController.getCustomerReward(customerId, fromDate, toDate));
		assertEquals(expectedMessage, thrown.getMessage());
	}

	@Test
	public void testGetCustomerReward_InvalidDateRange() {
		String customerId = "CUST001";
		LocalDate fromDate = LocalDate.parse("2025-03-31");
		LocalDate toDate = LocalDate.parse("2025-01-01");
		String expectedMessage = "Invalid date range. From-date should be before to-date.";

		when(rewardsService.getCustomerRewards(customerId, fromDate, toDate))
				.thenThrow(new IllegalArgumentException(expectedMessage));

		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
				() -> rewardsController.getCustomerReward(customerId, fromDate, toDate));
		assertEquals(expectedMessage, thrown.getMessage());
	}

	@Test
	public void testGetCustomerReward_UnexpectedError() {
		String customerId = "CUST001";
		LocalDate fromDate = LocalDate.parse("2025-01-01");
		LocalDate toDate = LocalDate.parse("2025-03-31");
		String expectedMessage = "Simulated unexpected error";

		when(rewardsService.getCustomerRewards(customerId, fromDate, toDate))
				.thenThrow(new RuntimeException(expectedMessage));

		RuntimeException thrown = assertThrows(RuntimeException.class,
				() -> rewardsController.getCustomerReward(customerId, fromDate, toDate));
		assertEquals(expectedMessage, thrown.getMessage());
	}
}