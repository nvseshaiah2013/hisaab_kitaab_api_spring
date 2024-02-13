package com.kitaab.hisaab.ledger.dto.response;

public interface Response{

    boolean getStatus();

    String getMessage();

    Object getPayload();
}
