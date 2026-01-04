package com.jpmc.midascore.utilities;

import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionKafkaListener {
    private static final Logger logger = LoggerFactory.getLogger(TransactionKafkaListener.class);

    @Value("${general.kafka-topic}")
    private String topic;

    @Autowired
    private TransactionService transactionService;

    private int transactionCount = 0;

    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-core-group")
    public void listen(Transaction transaction) {
        transactionCount++;
        logger.info("Received transaction #{}: {}", transactionCount, transaction);
        logger.info("Transaction amount: {}", transaction.getAmount());

        // Process the transaction through the service
        try {
            transactionService.processTransaction(transaction);
        } catch (Exception e) {
            logger.error("Error processing transaction: {}", transaction, e);
        }

    }
}