package com.kitaab.hisaab.ledger.service;

import com.kitaab.hisaab.ledger.dto.response.SuccessResponse;
import com.kitaab.hisaab.ledger.entity.borrow.Borrow;
import com.kitaab.hisaab.ledger.entity.borrow.BorrowToken;

public interface BorrowTokenService {
    SuccessResponse generateTokenResponse(String borrowId);
    SuccessResponse getToken(String borrowId);
    BorrowToken generateToken(Borrow borrow);
}
