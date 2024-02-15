package com.kitaab.hisaab.ledger.service;


import com.kitaab.hisaab.ledger.constants.EmailTypeEnum;
import com.kitaab.hisaab.ledger.entity.user.EmailDetails;

import java.util.concurrent.CompletableFuture;

public interface EmailService {
    CompletableFuture<Boolean> sendEmail(EmailDetails email, EmailTypeEnum type);
}
