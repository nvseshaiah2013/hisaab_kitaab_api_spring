package com.kitaab.hisaab.ledger.service.impl;

import com.kitaab.hisaab.ledger.constants.ExceptionEnum;
import com.kitaab.hisaab.ledger.dto.request.borrow.ItemBorrowRequest;
import com.kitaab.hisaab.ledger.dto.response.SuccessResponse;
import com.kitaab.hisaab.ledger.entity.borrow.BorrowItem;
import com.kitaab.hisaab.ledger.entity.borrow.BorrowStatus;
import com.kitaab.hisaab.ledger.entity.user.CustomUserDetails;
import com.kitaab.hisaab.ledger.entity.user.User;
import com.kitaab.hisaab.ledger.exception.FlowBreakerException;
import com.kitaab.hisaab.ledger.repository.BorrowRepository;
import com.kitaab.hisaab.ledger.repository.UserRepository;
import com.kitaab.hisaab.ledger.service.BorrowService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;
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
        var user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("Fetching the items given by the user : {} and id : {}", user.getUsername(), user.get("_id"));
        var list = borrowRepository.findAllByBorrower__id((String) user.get("_id"));
        log.info("Found {} records", list.size());
        var response = new SuccessResponse(HttpStatus.OK, "Fetched the list of given items");
        response.put("borrows", list);
        return response;
    }

    @Override
    public SuccessResponse getTakenBorrows() {
        var user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("Fetching the items taken by the user : {} and id : {}", user.getUsername(), user.get("_id"));
        var list = borrowRepository.findAllByBorowee__id((String) user.get("_id"));
        log.info("Found {} records", list.size());
        var response = new SuccessResponse(HttpStatus.OK, "Fetched the list of taken items");
        response.put("borrows", list);
        return response;
    }

    @Override
    public SuccessResponse createBorrow(ItemBorrowRequest borrow) {
        var user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("Creating a new item borrow request by user : {} with ex. return date : {}", user.getUsername(),
                borrow.getExpectedReturnDate());
        BorrowItem borrowItem = BorrowItem
                .builder()
                .withDescription(borrow.getDescription())
                .withExpectedReturnDate(borrow.getExpectedReturnDate())
                .withItemName(borrow.getItemName())
                .withBorrower((User) user.get("user"))
                .withPlace(borrow.getPlace())
                .withOccasion(borrow.getOccasion())
                .withStatus(BorrowStatus.PENDING)
                .withBorowee(userRepository.findByUsername(borrow.getBorowee()))
                .build();
        log.debug("Built the borrow Item object");

        return Optional.of(borrowRepository.save(borrowItem))
                .map(savedBorrow -> {
                    log.info("Borrow Saved successfully");
                    var response = new SuccessResponse(HttpStatus.CREATED, "Borrow Saved successfully");
                    response.put("savedBorrow", savedBorrow);
                    return response;
                })
                .orElseThrow(() -> new FlowBreakerException(ExceptionEnum.UNEXPECTED_EXCEPTION.getMessage(),
                ExceptionEnum.UNEXPECTED_EXCEPTION));
    }

    @Override
    public SuccessResponse updateBorrow(String borrowId, ItemBorrowRequest borrow)  {
        log.info("Finding the borrow record with borrow id: {}", borrowId);
        BorrowItem borrowedItem = (BorrowItem) borrowRepository.findById(borrowId)
                .orElseThrow(() -> new FlowBreakerException(ExceptionEnum.NO_BORROW_RECORD_FOUND.getFormattedMessage(borrowId),
                        ExceptionEnum.NO_BORROW_RECORD_FOUND));
        log.debug("Found the borrow with id : {}", borrowId);
        if (BorrowStatus.PENDING != borrowedItem.getStatus()) {
            log.error(ExceptionEnum.BORROW_RECORD_CANNOT_BE_UPDATED.getMessage(), borrowId);
            throw new FlowBreakerException(ExceptionEnum.BORROW_RECORD_CANNOT_BE_UPDATED.getFormattedMessage(borrowId),
                    ExceptionEnum.BORROW_RECORD_CANNOT_BE_UPDATED);
        }
        borrowedItem.setDescription(borrow.getDescription());
        borrowedItem.setItemName(borrow.getItemName());
        borrowedItem.setPlace(borrow.getPlace());
        borrowedItem.setOccasion(borrow.getOccasion());
        borrowedItem.setExpectedReturnDate(borrow.getExpectedReturnDate());

        return Optional.of(borrowRepository.save(borrowedItem))
                .map(savedBorrow -> {
                    log.info("Borrow updated successfully");
                    var response = new SuccessResponse(HttpStatus.OK, "Borrow updated successfully");
                    response.put("updatedBorrow", savedBorrow);
                    return response;
                })
                .orElseThrow(() -> new FlowBreakerException(ExceptionEnum.UNEXPECTED_EXCEPTION.getMessage(),
                        ExceptionEnum.UNEXPECTED_EXCEPTION));
    }

    @Override
    public SuccessResponse deleteBorrow(String borrowId) {
        log.info("Requested to delete borrow with id {}", borrowId);
        var borrow = borrowRepository.findById(borrowId)
                .orElseThrow(() -> new FlowBreakerException(ExceptionEnum.NO_BORROW_RECORD_FOUND.getFormattedMessage(borrowId),
                        ExceptionEnum.NO_BORROW_RECORD_FOUND));
        var user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!Objects.equals(borrow.getBorrower().get_id(), user.get("_id"))) {

            log.error(ExceptionEnum.USER_CANNOT_PERFORM_THE_ACTION_ON_THIS_BORROW_RECORD.getMessage(), borrowId, "DELETE", borrow
                    .getBorrower().getUsername() );
            throw new FlowBreakerException(ExceptionEnum.USER_CANNOT_PERFORM_THE_ACTION_ON_THIS_BORROW_RECORD.getFormattedMessage(borrowId,
                    "DELETE", borrow.getBorrower().getUsername()),
                    ExceptionEnum.USER_CANNOT_PERFORM_THE_ACTION_ON_THIS_BORROW_RECORD);
        }

        if (BorrowStatus.APPROVED == borrow.getStatus()) {

            log.error(ExceptionEnum.BORROW_RECORD_INVALID_STATE_FOR_REQUESTED_ACTION.getMessage(), borrowId, "DELETE");
            throw new FlowBreakerException(ExceptionEnum.BORROW_RECORD_INVALID_STATE_FOR_REQUESTED_ACTION.getFormattedMessage(borrowId, "DELETE"),
                    ExceptionEnum.BORROW_RECORD_INVALID_STATE_FOR_REQUESTED_ACTION);
        }

        log.debug("Deleting borrow with id {}", borrowId);
        borrowRepository.delete(borrow);
        log.debug("Deleted the borrow with id : {}", borrowId);
        return new SuccessResponse(HttpStatus.OK, "Deleted successfully");
    }

    @Override
    public SuccessResponse returnBorrow(String borrowId) {
        log.info("Requested to return the borrow with id {}", borrowId);

        return null;
    }

    @Override
    public SuccessResponse rejectBorrow(String borrowId) {
        log.info("Requested to reject borrow with id {}", borrowId);
        var borrow = borrowRepository.findById(borrowId)
                .orElseThrow(() -> new FlowBreakerException(ExceptionEnum.NO_BORROW_RECORD_FOUND.getFormattedMessage(borrowId),
                        ExceptionEnum.NO_BORROW_RECORD_FOUND));
        var user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!Objects.equals(borrow.getBorrower().get_id(), user.get("_id"))) {

            log.error(ExceptionEnum.USER_CANNOT_PERFORM_THE_ACTION_ON_THIS_BORROW_RECORD.getMessage(), borrowId, "REJECT", borrow
                    .getBorrower().getUsername());
            throw new FlowBreakerException(ExceptionEnum.USER_CANNOT_PERFORM_THE_ACTION_ON_THIS_BORROW_RECORD.getFormattedMessage(borrowId,
                    "REJECT", borrow.getBorrower().getUsername()),
                    ExceptionEnum.USER_CANNOT_PERFORM_THE_ACTION_ON_THIS_BORROW_RECORD);
        }

        if (BorrowStatus.PENDING != borrow.getStatus()) {

            log.error(ExceptionEnum.BORROW_RECORD_INVALID_STATE_FOR_REQUESTED_ACTION.getMessage(), borrowId, "REJECT");
            throw new FlowBreakerException(ExceptionEnum.BORROW_RECORD_INVALID_STATE_FOR_REQUESTED_ACTION.getFormattedMessage(borrowId, "REJECT"),
                    ExceptionEnum.BORROW_RECORD_INVALID_STATE_FOR_REQUESTED_ACTION);
        }

        log.debug("Rejecting borrow with id {}", borrowId);
        borrow.setStatus(BorrowStatus.REJECTED);
        borrowRepository.save(borrow);
        log.debug("Rejected the borrow with id : {}", borrowId);
        return new SuccessResponse(HttpStatus.OK, "Rejected Borrow successfully");
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
