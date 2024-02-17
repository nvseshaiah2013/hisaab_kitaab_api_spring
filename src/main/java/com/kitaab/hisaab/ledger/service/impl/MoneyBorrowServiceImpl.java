package com.kitaab.hisaab.ledger.service.impl;

import com.kitaab.hisaab.ledger.dto.request.borrow.MoneyBorrowRequest;
import com.kitaab.hisaab.ledger.dto.response.SuccessResponse;
import com.kitaab.hisaab.ledger.service.BorrowService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Qualifier("moneyBorrowService")
public class MoneyBorrowServiceImpl implements BorrowService<MoneyBorrowRequest> {

    @Override
    public SuccessResponse getGivenBorrows() {
        return null;
    }

    @Override
    public SuccessResponse getTakenBorrows() {
        return null;
    }

    @Override
    public SuccessResponse createBorrow(MoneyBorrowRequest borrow) {
        return null;
    }

    @Override
    public SuccessResponse updateBorrow(String borrowId, MoneyBorrowRequest borrow) {
        return null;
    }

    @Override
    public SuccessResponse deleteBorrow(String borrowId) {
        return null;
    }

    @Override
    public SuccessResponse returnBorrow(String borrowId) {
        return null;
    }

    @Override
    public SuccessResponse rejectBorrow(String borrowId) {
        return null;
    }

    @Override
    public SuccessResponse validateBorrow(String borrowId, String secretCode) {
        return null;
    }

    @Override
    public SuccessResponse validateReturn(String borrowId, String secretCode) {
        return null;
    }
}
