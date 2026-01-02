package com.jpmc.midascore.service;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
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

    @Autowired
    public TransactionService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.restTemplate = new RestTemplate();
    }

    @Transactional
    public void processTransaction(Transaction transaction) {
        long senderId = transaction.getSenderId();
        long recipientId = transaction.getRecipientId();
        String senderName = userRepository.getNameById(senderId).getName();
        String recipientName = userRepository.getNameById(recipientId).getName();
        float senderBalance = userRepository.getBalanceById(senderId).getBalance();
        float recipientBalance = userRepository.getBalanceById(recipientId).getBalance();
        float amount = transaction.getAmount();

        logger.info("Processing transaction: {}", transaction);

        ValidateTransaction(transaction);

        UserRecord sender = userRepository.findById(senderId);
        UserRecord recipient = userRepository.findById(recipientId);

        sender.setBalance(senderBalance - amount);
        userRepository.save(sender);
        logger.info("Sender Name: {}", senderName);

        recipient.setBalance(recipientBalance + amount);
        userRepository.save(recipient);
        logger.info("Recipient Name: {}", recipientName);

        logger.info("Recipient Balance: {}", recipientBalance);
        logger.info("Transaction sent: {}", transaction);
    }

    public boolean ValidateTransaction(Transaction transaction) {
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

        if (amount < 0 && amount >= senderBalance) {
            logger.info("Amount not enough. Transaction: {}", transaction);
            return false;
        }

        return true;
    }


}