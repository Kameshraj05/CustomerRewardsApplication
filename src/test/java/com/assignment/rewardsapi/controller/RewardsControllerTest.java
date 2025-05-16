package com.assignment.rewardsapi.controller;

import com.assignment.rewardsapi.dto.CustomerDetailsDTO;
import com.assignment.rewardsapi.dto.CustomerTransactionDTO;
import com.assignment.rewardsapi.exception.CustomerNotFoundException;
import com.assignment.rewardsapi.service.RewardsService;

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
import static org.mockito.Mockito.when;

public class RewardsControllerTest {

    @Mock
    private RewardsService rewardsService;

    @InjectMocks
    private RewardsController rewardsController;

    @SuppressWarnings("deprecation")
	@BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        //Necessary for the from() method in the controller.
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Test
    public void testSaveCustomerTransaction() {
        // Mock the service method to do nothing
        CustomerTransactionDTO dto = new CustomerTransactionDTO();
        ResponseEntity<String> response = rewardsController.saveCustomerTransaction(dto);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Customer and Transaction saved successfully!", response.getBody());
    }

    @Test
    public void testGetCustomerReward_Success() {
        // Mock the service method to return a CustomerDetailsDTO
        String customerId = "CUST001";
        LocalDate fromDate = LocalDate.parse("2025-01-01");
        LocalDate toDate = LocalDate.parse("2025-03-31");

        CustomerDetailsDTO expectedDto = new CustomerDetailsDTO();
        expectedDto.setId(customerId);
        when(rewardsService.calculateRewardPoints(customerId, fromDate, toDate)).thenReturn(expectedDto);

        ResponseEntity<?> response = rewardsController.getCustomerReward(customerId, fromDate, toDate);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedDto, response.getBody());
    }

    @Test
    public void testGetCustomerReward_CustomerNotFound() {
        // Mock the service method to throw a CustomerNotFoundException
        String customerId = "CUST123";
        LocalDate fromDate = LocalDate.parse("2024-01-01");
        LocalDate toDate = LocalDate.parse("2024-03-31");

        when(rewardsService.calculateRewardPoints(customerId, fromDate, toDate))
                .thenThrow(new CustomerNotFoundException("Customer not found"));

        ResponseEntity<?> response = rewardsController.getCustomerReward(customerId, fromDate, toDate);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Customer not found", response.getBody());
    }

     @Test
    public void testGetCustomerReward_InvalidDateRange() {
        // Mock the service method to throw an IllegalArgumentException for invalid date range
        String customerId = "CUST001";
        LocalDate fromDate = LocalDate.parse("2025-03-31");
        LocalDate toDate = LocalDate.parse("2025-01-01"); // Intentionally invalid

        when(rewardsService.calculateRewardPoints(customerId, fromDate, toDate))
                .thenThrow(new IllegalArgumentException("Invalid date range. From-date cannot be after to-date."));

        ResponseEntity<?> response = rewardsController.getCustomerReward(customerId, fromDate, toDate);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid date range. From-date cannot be after to-date.", response.getBody());
    }

    @Test
    public void testGetCustomerReward_UnexpectedError() {
        // Mock the service method to throw an Exception
        String customerId = "CUST001";
        LocalDate fromDate = LocalDate.parse("2025-01-01");
        LocalDate toDate = LocalDate.parse("2025-03-31");

        when(rewardsService.calculateRewardPoints(customerId, fromDate, toDate))
                .thenThrow(new RuntimeException("Simulated unexpected error"));

        ResponseEntity<?> response = rewardsController.getCustomerReward(customerId, fromDate, toDate);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Something went wrong. Please try again later.", response.getBody());
    }
}
