package com.rahul.demo_bank.service.impl;

import com.rahul.demo_bank.config.JwtTokenProvider;
import com.rahul.demo_bank.dto.*;
import com.rahul.demo_bank.entity.Role;
import com.rahul.demo_bank.entity.User;
import com.rahul.demo_bank.repository.UserRepository;
import com.rahul.demo_bank.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    UserRepository userRepository;
    @Autowired
    EmailService emailService;
    @Autowired
    TransactionService transactionService;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtTokenProvider jwtTokenProvider;
    @Override
    public BankResponse createAccount(UserRequest userRequest) {
        // create account saving new user to DB
        //check if user already exist
        if(userRepository.existsByEmail(userRequest.getEmail())) {
            return  BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();

        }
            User newUser = User.builder()
                    .firstName(userRequest.getFirstName())
                    .lastName(userRequest.getLastName())
                    .otherName(userRequest.getOtherName())
                    .gender(userRequest.getGender())
                    .address(userRequest.getAddress())
                    .stateOfOrigin(userRequest.getStateOfOrigin())
                    .accountNumber(AccountUtils.generateAccountNumber())
                    .accountBalance(BigDecimal.ZERO)
                    .email(userRequest.getEmail())
                    .password(passwordEncoder.encode(userRequest.getPassword()))
                    .phoneNumber(userRequest.getPhoneNumber())
                    .alternativePhoneNumber(userRequest.getAlternativePhoneNumber())
                    .status("ACTIVE")
                    .role(Role.valueOf("ROLE_ADMIN"))
                    .build();
            User savedUser=userRepository.save(newUser);
            //send email alert
        EmailDetails emailDetails= EmailDetails.builder()
                .recipient(savedUser.getEmail())
                .subject("ACCOUNT CREATION")
                .messageBody("Congratulations! Your Account has been successfully created" +
                        "\n Your Account details\n" +
                        "Account name:"+savedUser.getFirstName()+" "+savedUser.getLastName()+" "+savedUser.getOtherName()+"\n" +
                        "Account Number: "+savedUser.getAccountNumber())
                .build();
            emailService.sendemailAlert(emailDetails);

            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_CREATION_SUCCESS)
                    .responseMessage(AccountUtils.ACCOUNT_CREATION_MESSAGE)
                    .accountInfo(AccountInfo.builder()
                            .accountBalance(savedUser.getAccountBalance())
                            .accountNumber(savedUser.getAccountNumber())
                            .accountName(savedUser.getFirstName()+" "+savedUser.getLastName()+" "+savedUser.getOtherName())
                            .build())
                    .build();

    }


    public BankResponse login(LoginDto loginDto){
        Authentication authentication=null;
        authentication= authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(),loginDto.getPassword())
        );
        EmailDetails loginAlert =EmailDetails.builder()
                .subject("You're logged in")
                .recipient(loginDto.getEmail())
                .messageBody("You logged into your account if you did not initiate this request, please contact your bank")
                .build();

        emailService.sendemailAlert(loginAlert);
        return BankResponse.builder()
                .responseCode("login success")
                .responseMessage(jwtTokenProvider.generateToken(authentication))
                .build();
    }
    @Override
    public BankResponse balanceEnquiry(EnquiryRequest request) {
        //check if the provided account exists in DB
        boolean isAccountExist=userRepository.existsByAccountNumber(request.getAccountNumber());
        if(!isAccountExist){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User foundUser=userRepository.findByAccountNumber(request.getAccountNumber());
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_FOUND_CODE)
                .responseMessage(AccountUtils.ACCOUNT_FOUND_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(foundUser.getAccountBalance())
                        .accountNumber(request.getAccountNumber())
                        .accountName(foundUser.getFirstName()+" "+foundUser.getLastName()+" "+foundUser.getOtherName())
                        .build())
                .build();
    }

    @Override
    public String nameEnquiry(EnquiryRequest request) {
        boolean isAccountExist=userRepository.existsByAccountNumber(request.getAccountNumber());
        if(!isAccountExist){
            return AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE;
        }
        User foundUser=userRepository.findByAccountNumber(request.getAccountNumber());
        return foundUser.getFirstName()+" "+foundUser.getLastName()+" "+foundUser.getOtherName();
    }

    @Override
    public BankResponse creditAccount(CreditDebitRequest request) {
        // checking if the account exist
        boolean isAccountExist=userRepository.existsByAccountNumber(request.getAccountNumber());
        if(!isAccountExist){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User userToCredit=userRepository.findByAccountNumber(request.getAccountNumber());
        userToCredit.setAccountBalance(userToCredit.getAccountBalance().add(request.getAmount()));
        userRepository.save(userToCredit);
        //save transaction
        TransactionDto transactionDto= TransactionDto.builder()
                .accountNumber(userToCredit.getAccountNumber())
                .transactionType("CREDIT")
                .amount(request.getAmount())
                .build();
        transactionService.saveTransaction(transactionDto);

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREDITED_SUCCESS_CODE)
                .responseMessage(AccountUtils.ACCOUNT_CREDITED_SUCCESS_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountName(userToCredit.getFirstName()+" "+userToCredit.getLastName()+" "+userToCredit.getOtherName())
                        .accountBalance(userToCredit.getAccountBalance())
                        .accountNumber(userToCredit.getAccountNumber())
                        .build())
                .build();
    }

    @Override
    public BankResponse debitAccount(CreditDebitRequest request) {
        boolean isAccountExist=userRepository.existsByAccountNumber(request.getAccountNumber());
        if(!isAccountExist){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User userToDebit=userRepository.findByAccountNumber(request.getAccountNumber());
//        int availableBalance=Integer.parseInt(userToDebit.getAccountBalance().toString());
//        int debitAmount=Integer.parseInt(request.getAmount().toString());
        if(userToDebit.getAccountBalance().compareTo(request.getAmount())<0){
            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        else{
            userToDebit.setAccountBalance(userToDebit.getAccountBalance().subtract(request.getAmount()));
            userRepository.save(userToDebit);

            TransactionDto transactionDto= TransactionDto.builder()
                    .accountNumber(userToDebit.getAccountNumber())
                    .transactionType("DEBIT")
                    .amount(request.getAmount())
                    .build();
            transactionService.saveTransaction(transactionDto);

            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_DEBITED_SUCCESS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_DEBITED_SUCCESS_MESSAGE)
                    .accountInfo(AccountInfo.builder()
                            .accountNumber(request.getAccountNumber())
                            .accountName(userToDebit.getFirstName()+" "+ userToDebit.getLastName()+" "+userToDebit.getOtherName())
                            .accountBalance(userToDebit.getAccountBalance())
                            .build())
                    .build();
        }
    }

    @Override
    public BankResponse transfer(TransferRequest request) {
        //get the account to debit
        //check if the amount debiting is more than the current balance
        //debit thee account
        //get the account to credit
        //creddit the account
        boolean isDestinationAccountExist=userRepository.existsByAccountNumber(request.getSourceAccountNumber());
        if(!isDestinationAccountExist){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User sourceAccountUser=userRepository.findByAccountNumber(request.getSourceAccountNumber());
        if(request.getAmount().compareTo(sourceAccountUser.getAccountBalance())>0){
            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        sourceAccountUser.setAccountBalance(sourceAccountUser.getAccountBalance().subtract(request.getAmount()));
        String sourceUserName=sourceAccountUser.getFirstName()+" "+sourceAccountUser.getLastName()+" "+sourceAccountUser.getOtherName();
        userRepository.save(sourceAccountUser);

        TransactionDto transactionDto= TransactionDto.builder()
                .accountNumber(sourceAccountUser.getAccountNumber())
                .transactionType("DEBIT")
                .amount(request.getAmount())
                .build();
        transactionService.saveTransaction(transactionDto);

        EmailDetails debitAlert= EmailDetails.builder()
                .subject("DEBIT ALERT")
                .recipient(sourceAccountUser.getEmail())
                .messageBody("The sum of "+request.getAmount()+" has been deducted from your account!" +
                        "\n Your current balance is "+sourceAccountUser.getAccountBalance())
                .build();
        emailService.sendemailAlert(debitAlert);

        User destinationAccountUser=userRepository.findByAccountNumber(request.getDestinationAccountNumber());
//        String recipientName=destinationAccountUser.getFirstName()+" "+destinationAccountUser.getLastName()+" "+destinationAccountUser.getOtherName();
        destinationAccountUser.setAccountBalance(destinationAccountUser.getAccountBalance().add(request.getAmount()));
        userRepository.save(destinationAccountUser);

        TransactionDto transactionDto1= TransactionDto.builder()
                .accountNumber(destinationAccountUser.getAccountNumber())
                .transactionType("CREDIT")
                .amount(request.getAmount())
                .build();
        transactionService.saveTransaction(transactionDto);

        EmailDetails creditAlert= EmailDetails.builder()
                .subject("DEBIT ALERT")
                .recipient(sourceAccountUser.getEmail())
                .messageBody("The sum of "+request.getAmount()+" has been sent to your account from " +sourceUserName+
                        "\n Your current balance is "+destinationAccountUser.getAccountBalance())
                .build();
        emailService.sendemailAlert(creditAlert);
        return BankResponse.builder()
                .responseCode(AccountUtils.TRANSFER_SUCCESS_CODE)
                .responseMessage(AccountUtils.TRANSFER_SUCCESS_MESSAGE)
                .accountInfo(null)
                .build();

    }

    public static void main(String[] args) {
        UserServiceImpl userService=new UserServiceImpl();
        System.out.println(userService.passwordEncoder.encode("1234"));
    }
}
