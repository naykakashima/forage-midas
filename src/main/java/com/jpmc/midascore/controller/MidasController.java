package com.jpmc.midascore.controller;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Balance;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.repository.UserRepository;
import com.jpmc.midascore.service.TransactionService;
import org.apache.catalina.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/balance")
public class MidasController {
    private static final Logger logger = LoggerFactory.getLogger(MidasController.class);

    private final UserRepository userRepository;

    @Autowired
    public MidasController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<Balance> getUserBalance(@RequestParam("userId") long userId) {
        logger.info("Balance query requested for user ID: {}", userId);

        UserRecord user = userRepository.findById(userId);

        if (user == null) {
            logger.info("User with ID {} not found, returning balance 0", userId);
            return ResponseEntity.ok(new Balance(0.0f));
        }

        float balance = user.getBalance();
        logger.info("User {} (ID: {}) has balance: {}", user.getName(), userId, balance);

        return ResponseEntity.ok(new Balance(balance));
    }



}
