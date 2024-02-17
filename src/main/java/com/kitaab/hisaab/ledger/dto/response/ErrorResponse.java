package com.kitaab.hisaab.ledger.dto.response;

import java.util.Date;

public record ErrorResponse(Date timeStamp, String errorMessage, String errorCode, String traceId, String spanId) {}
