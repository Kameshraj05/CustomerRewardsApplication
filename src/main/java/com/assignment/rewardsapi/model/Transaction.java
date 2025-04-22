package com.assignment.rewardsapi.model;

import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Generates getters, setters, toString, equals, hashCode
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "transactions")
public class Transaction {

    @Id
    private String id;

    @NotNull
    @Field("customer_id")
    private String customerId;
    
    @NotNull
    @Field("transaction_id")
    private String transactionId;
    

    @NotNull
    @Positive
    @Field("amount")
    private Double amount;

    @NotNull
    @Field("transaction_date")
    private LocalDateTime transactionDate;

}
