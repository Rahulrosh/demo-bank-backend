package com.rahul.demo_bank.repository;

import com.rahul.demo_bank.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction,String> {

}
