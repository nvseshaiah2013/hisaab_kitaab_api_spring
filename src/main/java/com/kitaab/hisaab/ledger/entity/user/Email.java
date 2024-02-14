package com.kitaab.hisaab.ledger.entity.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(setterPrefix = "with")
@AllArgsConstructor
public class Email {
    private String from ;
    private String to;
    private String subject;
    private String template;
}
