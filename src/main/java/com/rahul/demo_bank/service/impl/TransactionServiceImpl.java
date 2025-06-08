package com.rahul.demo_bank.service.impl;

import com.rahul.demo_bank.dto.TransactionDto;
import com.rahul.demo_bank.entity.Transaction;
import com.rahul.demo_bank.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionServiceImpl implements  TransactionService {
   @Autowired
    TransactionRepository transactionRepository;

    @Override
    public void saveTransaction(TransactionDto transactionDto) {
            Transaction transaction=Transaction.builder()
                    .transactionType(transactionDto.getTransactionType())
                    .accountNumber(transactionDto.getAccountNumber())
                    .amount(transactionDto.getAmount())
                    .status("SUCCESS")
                    .build();
            transactionRepository.save(transaction);
            System.out.println("Transaction Saved Successfully");
    }
}
