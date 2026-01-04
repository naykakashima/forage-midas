package com.jpmc.midascore.service;

import com.jpmc.midascore.dto.Incentive;
import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
public class TransactionService {
    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    private final UserRepository userRepository;
    private final RestTemplate restTemplate;
    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionService(UserRepository userRepository, TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.restTemplate = new RestTemplate();
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public void processTransaction(Transaction transaction) {
        long senderId = transaction.getSenderId();
        long recipientId = transaction.getRecipientId();
        String senderName = userRepository.getNameById(senderId).getName();
        String recipientName = userRepository.getNameById(recipientId).getName();
        float amount = transaction.getAmount();

        logger.info("Processing transaction: {}", transaction);

        boolean isValid =  validateTransaction(transaction);
        if  (isValid) {
            float incentiveAmount = getIncentiveFromAPI(transaction);
            UserRecord sender = userRepository.findById(senderId);
            UserRecord recipient = userRepository.findById(recipientId);

            sender.setBalance(sender.getBalance() - amount);
            recipient.setBalance(recipient.getBalance() + amount + incentiveAmount);

            TransactionRecord record = TransactionRecord.fromTransaction(transaction, sender, recipient, isValid, incentiveAmount);
            transactionRepository.save(record);

            userRepository.save(recipient);
            userRepository.save(sender);

            logger.info("Sender Name: {}", senderName);
            logger.info("Sender Balance: {}", sender.getBalance());
            logger.info("Recipient Name: {}", recipientName);
            logger.info("Recipient Balance: {}", recipient.getBalance());
            logger.info("Transaction sent: {}", transaction);
        }
        else {
            logger.info("Transaction is invalid: {}", transaction);
        }


    }

    public boolean validateTransaction(Transaction transaction) {
        long recipientId = transaction.getRecipientId();
        long senderId = transaction.getSenderId();
        float amount = transaction.getAmount();
        float senderBalance = userRepository.getBalanceById(senderId).getBalance();

        if (!userRepository.existsById(recipientId)) {
            logger.info("Recipient not found. Transaction: {}", transaction);
            return false;
        }

        if (!userRepository.existsById(senderId)) {
            logger.info("Sender not found. Transaction: {}", transaction);
            return false;
        }

        if (amount <= 0 || amount > senderBalance) {
            logger.info("Amount not enough. Transaction: {}", transaction);
            return false;
        }

        return true;
    }

    private float getIncentiveFromAPI(Transaction transaction) {
        try {
            String url = "http://localhost:8080/incentive";
            Incentive response = restTemplate.postForObject(url, transaction, Incentive.class);

            if (response != null) {
                logger.info("Incentive API response: {}", response);
                return response.getAmount();
            } else {
                logger.warn("Incentive API returned null response");
                return 0.0f;
            }
        } catch (Exception e) {
            logger.error("Error calling Incentive API: {}", e.getMessage());
            return 0.0f;
        }
    }


}