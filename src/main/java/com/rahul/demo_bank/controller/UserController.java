package com.rahul.demo_bank.controller;

import com.rahul.demo_bank.dto.*;
import com.rahul.demo_bank.service.impl.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@Tag(name="User Account Management APIs")
public class UserController {
    @Autowired
    UserService userService;
    @Operation(
            summary = "create new Account",
            description="Create new user and assigining account number"
    )
    @ApiResponse(
            responseCode = "201",
            description = "HTTP status 201 Created"
    )

    @PostMapping
    public BankResponse createAccount(@RequestBody UserRequest userRequest){
        return userService.createAccount(userRequest);

    }

    @PostMapping("/login")
    public BankResponse login(@RequestBody LoginDto loginDto){
        return userService.login(loginDto);
    }


    @GetMapping("/balanceEnquiry")
    public BankResponse balanceEnquiry(@RequestBody EnquiryRequest request){
        return userService.balanceEnquiry(request);
    }
    @GetMapping("/nameEnquiry")
    public String nameEnquiry(@RequestBody EnquiryRequest request){
        return userService.nameEnquiry(request);
    }
    @PostMapping("/credit")
    public BankResponse creditAccount(@RequestBody CreditDebitRequest request){
        return userService.creditAccount(request);
    }
    @PostMapping("/debit")
    public BankResponse debitAccount(@RequestBody CreditDebitRequest request){
        return userService.debitAccount(request);
    }
    @PostMapping("/transfer")
    public BankResponse transfer(@RequestBody TransferRequest request){
        return userService.transfer(request);
    }
}
