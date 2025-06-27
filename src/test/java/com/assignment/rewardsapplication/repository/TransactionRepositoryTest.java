package com.assignment.rewardsapplication.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import com.assignment.rewardsapplication.dto.Transaction;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class TransactionRepositoryTest {

	@Autowired
	private TransactionRepository transactionRepository;

	@Test
	@DisplayName("Should save and retrieve transactions by customerId")
	void testFindByCustomerId() {

		Transaction txn1 = new Transaction();
		txn1.setCustomerId("CUST001");
		txn1.setAmount(120.50);
		txn1.setTransactionDate(LocalDateTime.now());

		Transaction txn2 = new Transaction();
		txn2.setCustomerId("CUST001");
		txn2.setAmount(45.00);
		txn2.setTransactionDate(LocalDateTime.now().minusDays(5));

		transactionRepository.save(txn1);
		transactionRepository.save(txn2);

		List<Transaction> transactions = transactionRepository.findByCustomerId("CUST001");

		assertThat(transactions).hasSize(2);
	}

	@Test
	@DisplayName("Should retrieve only transactions within last 3 months")
	void testFindByCustomerIdAndTransactionDateBetween() {
		String customerId = "CUST002";
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime threeMonthsAgo = now.minusMonths(3);
		LocalDateTime fourMonthsAgo = now.minusMonths(4);

		Transaction recentTxn = new Transaction();
		recentTxn.setCustomerId(customerId);
		recentTxn.setAmount(99.99);
		recentTxn.setTransactionDate(now.minusDays(10));

		Transaction oldTxn = new Transaction();
		oldTxn.setCustomerId(customerId);
		oldTxn.setAmount(45.00);
		oldTxn.setTransactionDate(fourMonthsAgo);

		transactionRepository.save(recentTxn);
		transactionRepository.save(oldTxn);

		List<Transaction> recentTransactions = transactionRepository
				.findByCustomerIdAndTransactionDateBetween(customerId, threeMonthsAgo, now);

		assertThat(recentTransactions).hasSize(1);
		assertThat(recentTransactions.get(0).getAmount()).isEqualTo(99.99);
	}
}
