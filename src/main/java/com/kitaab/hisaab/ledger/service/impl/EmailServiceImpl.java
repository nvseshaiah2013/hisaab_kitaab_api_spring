package com.kitaab.hisaab.ledger.service.impl;

import com.kitaab.hisaab.ledger.entity.user.Email;
import com.kitaab.hisaab.ledger.service.EmailService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    /**
     * @param email email details
     * @return boolean
     */
    @Override
    public boolean sendEmail(Email email) {
        boolean sent = false;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email.getTo());
        message.setFrom(email.getFrom());
        message.setSubject(email.getSubject());
        message.setText(email.getTemplate());
        try {
            mailSender.send(message);
            sent = true;
        } catch (Exception exception) {
            log.error("Unable to send email: ", exception);
        }
        return sent;
    }
}
