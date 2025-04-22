package com.assignment.rewardsapi.controller;

import java.time.LocalDate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.assignment.rewardsapi.dto.CustomerDetailsDTO;
import com.assignment.rewardsapi.dto.CustomerTransactionDTO;
import com.assignment.rewardsapi.exception.CustomerNotFoundException;
import com.assignment.rewardsapi.service.RewardsService;

@Controller
@RequestMapping("/api/rewards")
public class RewardsController {

	private static final Logger log = LogManager.getLogger(RewardsController.class);
	
	@Autowired
	private RewardsService rewardsService;

	
	@PostMapping("/transaction")
    public ResponseEntity<String> saveCustomerTransaction(@RequestBody CustomerTransactionDTO dto) {
		rewardsService.saveCustomerAndTransaction(dto);
        return ResponseEntity.ok("Customer and Transaction saved successfully!");
    }
	
	@GetMapping("/customers/{customerId}")
	public ResponseEntity<?> getCustomerReward(
	        @PathVariable String customerId,
	        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
	        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

	    try {
	        log.info("Fetching reward points for customerId: " + customerId);
	        CustomerDetailsDTO customerDetailsDTO = rewardsService.calculateRewardPoints(customerId, fromDate, toDate);
	        return new ResponseEntity<>(customerDetailsDTO, HttpStatus.OK);

	    } catch (CustomerNotFoundException ex) {
	        log.error("Customer not found: " + ex.getMessage());
	        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);

	    } catch (IllegalArgumentException ex) {
	        log.error("Validation error: " + ex.getMessage());
	        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);

	    } catch (Exception ex) {
	        log.error("Unexpected error occurred while fetching rewards for customerId: " + customerId, ex);
	        return new ResponseEntity<>("Something went wrong. Please try again later.", HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}

}
