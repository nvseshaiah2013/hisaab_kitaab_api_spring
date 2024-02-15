package com.kitaab.hisaab.ledger.entity.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(setterPrefix = "with")
@AllArgsConstructor
public class EmailDetails {
    private String action;
    private String to;
    private String subject;
    private String template;
    private String token;
    private String name;
    private String baseUrl;
}
