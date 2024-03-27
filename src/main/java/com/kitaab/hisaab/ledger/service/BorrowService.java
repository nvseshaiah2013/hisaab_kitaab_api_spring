package com.kitaab.hisaab.ledger.service;

import com.kitaab.hisaab.ledger.dto.response.SuccessResponse;

public interface BorrowService<T> {

    SuccessResponse getGivenBorrows();

    SuccessResponse getTakenBorrows();

    SuccessResponse createBorrow(T borrow);

    SuccessResponse updateBorrow(String borrowId, T borrow);

    SuccessResponse deleteBorrow(String borrowId);

    SuccessResponse returnBorrow(String borrowId);

    SuccessResponse rejectBorrow(String borrowId);

    SuccessResponse validateBorrow(String borrowId, String secretCode);

    SuccessResponse validateReturn(String borrowId, String secretCode);
}
