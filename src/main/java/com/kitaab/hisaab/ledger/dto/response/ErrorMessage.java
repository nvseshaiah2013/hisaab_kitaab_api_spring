package com.kitaab.hisaab.ledger.dto.response;

import com.kitaab.hisaab.ledger.Model.Response;

public record ErrorMessage (String header, String message) implements Response { }