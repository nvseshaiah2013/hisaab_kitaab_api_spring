package com.kitaab.hisaab.ledger.service;


import com.kitaab.hisaab.ledger.entity.user.Email;

public interface EmailService {
    boolean sendEmail(Email email);
}
