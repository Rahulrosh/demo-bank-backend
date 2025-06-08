package com.rahul.demo_bank.utils;

import java.time.Year;

public class AccountUtils {


    public static final String ACCOUNT_EXISTS_CODE="001";
    public static final String ACCOUNT_EXISTS_MESSAGE="This user already has an account created";
    public static  final String ACCOUNT_CREATION_SUCCESS="002";
    public static final String ACCOUNT_CREATION_MESSAGE="Account Successfully created";
    public static final String ACCOUNT_NOT_EXIST_CODE="003";
    public static final String ACCOUNT_NOT_EXIST_MESSAGE="User with the provided account number does not exist";
    public static final String ACCOUNT_FOUND_CODE="004";
    public static final String ACCOUNT_FOUND_MESSAGE="Account found with provided account number";
    public static final String ACCOUNT_CREDITED_SUCCESS_CODE="005";
    public static final String  ACCOUNT_CREDITED_SUCCESS_MESSAGE="Account successfully credited";
    public static final String INSUFFICIENT_BALANCE_CODE="006" ;
    public static final String INSUFFICIENT_BALANCE_MESSAGE="Insufficient Balance";
    public static final String ACCOUNT_DEBITED_SUCCESS_CODE="007";
    public static final String ACCOUNT_DEBITED_SUCCESS_MESSAGE="Account debited successfully";
    public  static final String TRANSFER_SUCCESS_CODE="008";
    public static final String TRANSFER_SUCCESS_MESSAGE="Transfer successful";
    public static String generateAccountNumber(){
        //random six digits
        Year currentYear=Year.now();
        int min=100000;
        int max=999999;
        //generate a random number between min and max
        int randNumber=(int)Math.floor(Math.random()*(max-min+1) + min);
        //convert current and random number to Strings,then concatenate
        String year=String.valueOf(currentYear);
        String randomNumber=String.valueOf(randNumber);
        StringBuilder accountNumber=new StringBuilder();

        return accountNumber.append(year).append(randomNumber).toString();
    }
}
