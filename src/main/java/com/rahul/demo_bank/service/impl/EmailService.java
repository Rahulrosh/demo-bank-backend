package com.rahul.demo_bank.service.impl;

import com.rahul.demo_bank.dto.EmailDetails;

public interface EmailService {
    void sendemailAlert(EmailDetails emailDetails);
    void sendEmailWithAttachment(EmailDetails emailDetails);
}
