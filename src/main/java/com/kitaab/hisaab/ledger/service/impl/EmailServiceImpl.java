package com.kitaab.hisaab.ledger.service.impl;

import com.kitaab.hisaab.ledger.constants.EmailConstants;
import com.kitaab.hisaab.ledger.constants.EmailTypeEnum;
import com.kitaab.hisaab.ledger.entity.user.EmailDetails;
import com.kitaab.hisaab.ledger.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.CharEncoding;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    @NonNull
    private final JavaMailSender mailSender;
    @NonNull
    private final TemplateEngine templateEngine;

    @Value("spring.mail.username")
    private String fromEmail;


    /**
     * @param emailDetails email details
     * @param type         email type
     */
    @Async
    @Override
    public CompletableFuture<Boolean> sendEmail(EmailDetails emailDetails, EmailTypeEnum type) {
        return CompletableFuture.completedFuture(switch (type) {
            case RESET_PASSWORD -> sendResetPasswordEmail(emailDetails);
            case LOG_IN -> {
                log.info("Login");
                yield true;
            }
            case SIGN_UP -> {
                log.info("SIGN_UP");
                yield false;
            }
            case BORROW -> {
                log.info("BORROW");
                yield false;
            }
            case LEND -> {
                log.info("LEND");
                yield false;
            }
        });
    }

    /**
     * @param emailDetails
     */
    public boolean sendResetPasswordEmail(EmailDetails emailDetails) {
        boolean isSent = false;
        try {
            Map<String, Object> properties = constructPropertiesForForgotPassword(emailDetails);
            String template = populateValuesToTemplate(EmailConstants.RESET_PASSWORD_FILE, properties);
            emailDetails.setTemplate(template);
            emailDetails.setSubject(EmailConstants.RESET_PASSWORD_EMAIL_SUBJECT);
            MimeMessage message = createMessageForEmail(emailDetails, true);
            mailSender.send(message);
            isSent = true;
        } catch (Exception exception) {
            log.error("Unable to send email: ", exception);
        }
        return isSent;
    }

    /**
     * @param emailDetails
     * @return
     */
    private Map<String, Object> constructPropertiesForForgotPassword(EmailDetails emailDetails) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("greetings", EmailConstants.getGreetingsText(emailDetails.getName()));
        properties.put("token", emailDetails.getToken());
        properties.put("name", emailDetails.getName());
        properties.put("title", EmailConstants.RESET_PASSWORD);
        properties.put("baseUrl", emailDetails.getBaseUrl());
        return properties;
    }

    /**
     * @param emailDetails all details about email
     * @param isHtml       if the template is html ot text
     * @return MimeMessage con
     * @throws MessagingException
     */
    private MimeMessage createMessageForEmail(EmailDetails emailDetails, boolean isHtml) throws MessagingException {
        MimeMessageHelper messageHelper = new MimeMessageHelper(mailSender.createMimeMessage(),
                true,
                CharEncoding.UTF_8);
        messageHelper.setTo(InternetAddress.parse(emailDetails.getTo()));
        messageHelper.setFrom(fromEmail);
        messageHelper.setSubject(emailDetails.getSubject());
        messageHelper.setText(emailDetails.getTemplate(), isHtml);
        return messageHelper.getMimeMessage();
    }


    /**
     * @param templateFile fileName for the required email
     * @param properties   properties to be used inside the email template
     * @return String value containing the email template with all values from properties
     */
    private String populateValuesToTemplate(String templateFile, Map<String, Object> properties) {
        return this.templateEngine.process(templateFile, new Context(Locale.getDefault(), properties));
    }
}
