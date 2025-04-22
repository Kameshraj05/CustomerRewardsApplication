package com.assignment.rewardsapi.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Generates getters, setters, toString, equals, hashCode
@NoArgsConstructor
@AllArgsConstructor
@Document(collection= "customer")
public class Customer {
	
	@Id
	private String id;
	
	@Field("customer_id")
	private String customerId;
	
	@Field("customer_name")
	private String customerName;
}
