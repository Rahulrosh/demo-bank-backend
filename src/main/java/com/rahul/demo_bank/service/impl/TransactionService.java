package com.rahul.demo_bank.service.impl;

import com.rahul.demo_bank.dto.TransactionDto;
import com.rahul.demo_bank.entity.Transaction;
import org.springframework.stereotype.Service;


public interface TransactionService {
    void saveTransaction(TransactionDto transactionDto);
}
