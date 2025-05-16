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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.assignment.rewardsapi.dto.CustomerDetailsDTO;
import com.assignment.rewardsapi.dto.CustomerTransactionDTO;
import com.assignment.rewardsapi.exception.CustomerNotFoundException;
import com.assignment.rewardsapi.service.RewardsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;

@Tag(name = "Rewards Application", description = "Operations related to customer rewards and transactions")
@Controller
@RequestMapping("/api/rewards")
public class RewardsController {

    private static final Logger log = LogManager.getLogger(RewardsController.class);

    @Autowired
    private RewardsService rewardsService;


    @Operation(summary = "Record a new customer transaction", description = "Endpoint to save customer details and their transaction.")
    @ApiResponse(responseCode = "200", description = "Success",
            content = @Content(mediaType = "text/plain", schema = @Schema(type = "string", example = "Customer and Transaction saved successfully!")))
    @ApiResponse(responseCode = "400", description = "Failure",
    		content = @Content(mediaType = "text/plain", schema = @Schema(type = "string", example = "Invalid Request Payload!")))
    @PostMapping("/transaction")
    public ResponseEntity<String> saveCustomerTransaction(
    		@org.springframework.web.bind.annotation.RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = CustomerTransactionDTO.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Example Payload",
                                            description = "Exmaple payload to test the API",
                                            value = "{\"customerId\": \"CUST001\", \"customerName\": \"Alice Smith\", \"transactionId\": \"TXN123\", \"amount\": 75.50, \"transactionDate\": \"2025-04-01T10:00:00\"}"
                                    )
                            }
                    ))
            CustomerTransactionDTO dto) {
    	log.info("Calling service to save customer data : " + dto);
    	if(dto != null)
        rewardsService.saveCustomerAndTransaction(dto);
        return ResponseEntity.ok("Customer and Transaction saved successfully!");
    }

    @Operation(summary = "Get reward points for a customer", description = "Retrieves reward points, transaction details, and monthly points for a specific customer, optionally within a date range.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved customer reward details",
            content = @Content(schema = @Schema(implementation = CustomerDetailsDTO.class)))
    @ApiResponse(responseCode = "400", description = "Invalid Customer ID or date range")
    @ApiResponse(responseCode = "404", description = "Customer not found or no transactions found for the given criteria",
            content = @Content(schema = @Schema(type = "string")))
    @ApiResponse(responseCode = "500", description = "Unexpected error occurred")
    @GetMapping("/customers/{customerId}")
    public ResponseEntity<?> getCustomerReward(
            @Parameter(description = "ID of the customer to retrieve rewards for", required = true, example = "CUST001")
            @PathVariable String customerId,
            @Parameter(description = "Start date for filtering transactions (YYYY-MM-DD)", example = "2025-01-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @Parameter(description = "End date for filtering transactions (YYYY-MM-DD)", example = "2025-03-31")
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