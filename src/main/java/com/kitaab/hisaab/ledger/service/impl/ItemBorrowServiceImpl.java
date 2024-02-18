package com.kitaab.hisaab.ledger.service.impl;

import com.kitaab.hisaab.ledger.constants.ExceptionEnum;
import com.kitaab.hisaab.ledger.dto.request.borrow.ItemBorrowRequest;
import com.kitaab.hisaab.ledger.dto.response.SuccessResponse;
import com.kitaab.hisaab.ledger.entity.borrow.BorrowItem;
import com.kitaab.hisaab.ledger.exception.FlowBreakerException;
import com.kitaab.hisaab.ledger.repository.BorrowRepository;
import com.kitaab.hisaab.ledger.repository.UserRepository;
import com.kitaab.hisaab.ledger.service.BorrowService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Qualifier("itemBorrowService")
@AllArgsConstructor
@Slf4j
public class ItemBorrowServiceImpl implements BorrowService<ItemBorrowRequest> {

    private final BorrowRepository borrowRepository;

    private final UserRepository userRepository;

    @Override
    public SuccessResponse getGivenBorrows() {
        var user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("Fetching the items given by the user : {}", user.getUsername());
        var list = borrowRepository.findAllByBorrower(userRepository.findByUsername(user.getUsername()));
        log.info("Found {} records", list.size());
        var response = new SuccessResponse(HttpStatus.OK, "Fetched the list of given items");
        response.put("borrows", list);
        return response;
    }

    @Override
    public SuccessResponse getTakenBorrows() {
        return null;
    }

    @Override
    public SuccessResponse createBorrow(ItemBorrowRequest borrow) {
        var user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("Creating a new item borrow request by user : {} with ex. return date : {}", user.getUsername(),
                borrow.getExpectedReturnDate());
        BorrowItem borrowItem = BorrowItem
                .builder()
                .withDescription(borrow.getDescription())
                .withExpectedReturnDate(borrow.getExpectedReturnDate())
                .withItemName(borrow.getItemName())
                .withBorrower(userRepository.findByUsername(user.getUsername()))
                .withPlace(borrow.getPlace())
                .withOccasion(borrow.getOccasion())
                .withBorowee(userRepository.findByUsername(borrow.getBorowee()))
                .build();
        log.debug("Built the borrow Item object");

        return Optional.of(borrowRepository.save(borrowItem))
                .map(savedBorrow -> {
                    var response = new SuccessResponse(HttpStatus.CREATED, "Borrow Saved successfully");
                    response.put("savedBorrow", response);
                    return response;
                })
                .orElseThrow(() -> new FlowBreakerException(ExceptionEnum.UNEXPECTED_EXCEPTION.getMessage(),
                ExceptionEnum.UNEXPECTED_EXCEPTION));
    }

    @Override
    public SuccessResponse updateBorrow(String borrowId, ItemBorrowRequest borrow) {
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
