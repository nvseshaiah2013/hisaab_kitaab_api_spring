package com.kitaab.hisaab.ledger.service.impl;

import com.kitaab.hisaab.ledger.constants.ExceptionEnum;
import com.kitaab.hisaab.ledger.dto.request.borrow.ItemBorrowRequest;
import com.kitaab.hisaab.ledger.dto.response.SuccessResponse;
import com.kitaab.hisaab.ledger.entity.borrow.Borrow;
import com.kitaab.hisaab.ledger.entity.borrow.BorrowItem;
import com.kitaab.hisaab.ledger.entity.borrow.BorrowStatus;
import com.kitaab.hisaab.ledger.entity.borrow.BorrowToken;
import com.kitaab.hisaab.ledger.entity.user.CustomUserDetails;
import com.kitaab.hisaab.ledger.entity.user.User;
import com.kitaab.hisaab.ledger.exception.FlowBreakerException;
import com.kitaab.hisaab.ledger.repository.BorrowRepository;
import com.kitaab.hisaab.ledger.repository.BorrowTokenRepository;
import com.kitaab.hisaab.ledger.repository.UserRepository;
import com.kitaab.hisaab.ledger.service.BorrowService;
import com.kitaab.hisaab.ledger.service.BorrowTokenService;
import com.kitaab.hisaab.ledger.util.SecretTokenUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;

import static com.kitaab.hisaab.ledger.constants.ApplicationConstants.*;

@Service
@Qualifier("itemBorrowService")
@AllArgsConstructor
@Slf4j
public class ItemBorrowServiceImpl implements BorrowService<ItemBorrowRequest> {

    private final BorrowRepository borrowRepository;

    private final UserRepository userRepository;

    private final BorrowTokenRepository tokenRepository;

    private final BorrowTokenService tokenService;

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
                    log.info("Generating secret token for borrow: {}", savedBorrow.getId());
                    tokenService.generateToken(savedBorrow);
                    log.debug("Secret token generated for borrow: {}", savedBorrow.getId());
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
                    log.info("Generating secret token for borrow: {}", savedBorrow.getId());
                    tokenService.generateToken(savedBorrow);
                    log.debug("Secret token generated for borrow: {}", savedBorrow.getId());
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

        if (!Objects.equals(borrow.getBorrower().get_id(), user.get("_id")) && !Objects.equals(borrow.getBorowee().get_id(), user.get("_id"))) {

            log.error(ExceptionEnum.USER_CANNOT_PERFORM_THE_ACTION_ON_THIS_BORROW_RECORD.getMessage(), borrowId, DELETE_ACTION, user.get("_id"));
            throw new FlowBreakerException(ExceptionEnum.USER_CANNOT_PERFORM_THE_ACTION_ON_THIS_BORROW_RECORD.getFormattedMessage(borrowId,
                    DELETE_ACTION, String.valueOf(user.get("_id"))),
                    ExceptionEnum.USER_CANNOT_PERFORM_THE_ACTION_ON_THIS_BORROW_RECORD);
        }

        if (BorrowStatus.APPROVED == borrow.getStatus()) {

            log.error(ExceptionEnum.BORROW_RECORD_INVALID_STATE_FOR_REQUESTED_ACTION.getMessage(), borrowId, DELETE_ACTION);
            throw new FlowBreakerException(ExceptionEnum.BORROW_RECORD_INVALID_STATE_FOR_REQUESTED_ACTION.getFormattedMessage(borrowId, DELETE_ACTION),
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
        var borrow = borrowRepository.findById(borrowId)
                .orElseThrow(() -> new FlowBreakerException(ExceptionEnum.NO_BORROW_RECORD_FOUND.getFormattedMessage(borrowId),
                        ExceptionEnum.NO_BORROW_RECORD_FOUND));

        var user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!Objects.equals(borrow.getBorowee().get_id(), user.get("_id"))) {

            log.error(ExceptionEnum.USER_CANNOT_PERFORM_THE_ACTION_ON_THIS_BORROW_RECORD.getMessage(), borrowId, RETURN_ACTION, borrow
                    .getBorowee().getUsername() );
            throw new FlowBreakerException(ExceptionEnum.USER_CANNOT_PERFORM_THE_ACTION_ON_THIS_BORROW_RECORD.getFormattedMessage(borrowId,
                    RETURN_ACTION, borrow.getBorowee().getUsername()),
                    ExceptionEnum.USER_CANNOT_PERFORM_THE_ACTION_ON_THIS_BORROW_RECORD);
        }

        if (BorrowStatus.APPROVED != borrow.getStatus()) {

            log.error(ExceptionEnum.BORROW_RECORD_INVALID_STATE_FOR_REQUESTED_ACTION.getMessage(), borrowId, RETURN_ACTION);
            throw new FlowBreakerException(ExceptionEnum.BORROW_RECORD_INVALID_STATE_FOR_REQUESTED_ACTION.getFormattedMessage(borrowId, RETURN_ACTION),
                    ExceptionEnum.BORROW_RECORD_INVALID_STATE_FOR_REQUESTED_ACTION);
        }

        log.debug("Generating secret token for borrow: {}", borrow.getId());
        var borrowToken = tokenService.generateToken(borrow);

        log.debug("Saved the secret token with document id :{}", borrowToken.getId());

        return new SuccessResponse(HttpStatus.CREATED, "Created the secret token for returning back the item.");
    }

    @Override
    public SuccessResponse rejectBorrow(String borrowId) {
        log.info("Requested to reject borrow with id {}", borrowId);
        var borrow = borrowRepository.findById(borrowId)
                .orElseThrow(() -> new FlowBreakerException(ExceptionEnum.NO_BORROW_RECORD_FOUND.getFormattedMessage(borrowId),
                        ExceptionEnum.NO_BORROW_RECORD_FOUND));
        var user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!Objects.equals(borrow.getBorrower().get_id(), user.get("_id"))) {

            log.error(ExceptionEnum.USER_CANNOT_PERFORM_THE_ACTION_ON_THIS_BORROW_RECORD.getMessage(), borrowId, REJECT_ACTION, borrow
                    .getBorrower().getUsername());
            throw new FlowBreakerException(ExceptionEnum.USER_CANNOT_PERFORM_THE_ACTION_ON_THIS_BORROW_RECORD.getFormattedMessage(borrowId,
                    REJECT_ACTION, borrow.getBorrower().getUsername()),
                    ExceptionEnum.USER_CANNOT_PERFORM_THE_ACTION_ON_THIS_BORROW_RECORD);
        }

        if (BorrowStatus.PENDING != borrow.getStatus()) {

            log.error(ExceptionEnum.BORROW_RECORD_INVALID_STATE_FOR_REQUESTED_ACTION.getMessage(), borrowId, REJECT_ACTION);
            throw new FlowBreakerException(ExceptionEnum.BORROW_RECORD_INVALID_STATE_FOR_REQUESTED_ACTION.getFormattedMessage(borrowId, REJECT_ACTION),
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
        log.info("Validating the giving of item for borrow id: {}", borrowId);
        var borrow = borrowRepository.findById(borrowId)
                .orElseThrow(() -> new FlowBreakerException(ExceptionEnum.NO_BORROW_RECORD_FOUND.getFormattedMessage(borrowId),
                        ExceptionEnum.NO_BORROW_RECORD_FOUND));
        var user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!Objects.equals(borrow.getBorrower().get_id(), user.get("_id"))) {

            log.error(ExceptionEnum.USER_CANNOT_PERFORM_THE_ACTION_ON_THIS_BORROW_RECORD.getMessage(), borrowId, VALIDATE_BORROW_ACTION, borrow
                    .getBorrower().getUsername());
            throw new FlowBreakerException(ExceptionEnum.USER_CANNOT_PERFORM_THE_ACTION_ON_THIS_BORROW_RECORD.getFormattedMessage(borrowId,
                    VALIDATE_BORROW_ACTION, borrow.getBorrower().getUsername()),
                    ExceptionEnum.USER_CANNOT_PERFORM_THE_ACTION_ON_THIS_BORROW_RECORD);
        }

        log.debug("Fetching the secret tokens for the borrow: {}", borrowId);
        var tokens = tokenRepository
                .findAllByBorrow_id(borrowId)
                .stream()
                .sorted(Comparator.comparing(BorrowToken::getCreatedDate).reversed()).toList();
        var localDate = LocalDateTime.now();

        if (tokens.isEmpty() || localDate.isAfter(tokens.get(0).getCreatedDate().plusMinutes(tokens.get(0).getValidity()))) {
            log.error(ExceptionEnum.BORROW_TOKEN_EXPIRED_OR_TOKEN_NOT_FOUND.getMessage());
            tokenRepository.deleteAll(tokens);
            throw new FlowBreakerException(ExceptionEnum.BORROW_TOKEN_EXPIRED_OR_TOKEN_NOT_FOUND.getErrorCode(),
                    ExceptionEnum.BORROW_TOKEN_EXPIRED_OR_TOKEN_NOT_FOUND);
        }

        if (!Objects.equals(secretCode, tokens.get(0).getSecretToken())) {
            log.error(ExceptionEnum.BORROW_TOKEN_INVALID.getMessage());
            throw new FlowBreakerException(ExceptionEnum.BORROW_TOKEN_INVALID.getMessage(), ExceptionEnum.BORROW_TOKEN_INVALID);
        }

        log.debug("Secret code is valid. Setting the borrow: {} to APPROVED state", borrowId);
        borrow.setStatus(BorrowStatus.APPROVED);
        borrowRepository.save(borrow);

        log.debug("Deleting the secret token: {}", tokens.get(0).getId());
        tokenRepository.delete(tokens.get(0));
        return new SuccessResponse(HttpStatus.OK, "Borrow validated successfully.");
    }

    @Override
    public SuccessResponse validateReturn(String borrowId, String secretCode) {
        log.info("Validating the return process for borrow id: {}", borrowId);
        var borrow = borrowRepository.findById(borrowId)
                .orElseThrow(() -> new FlowBreakerException(ExceptionEnum.NO_BORROW_RECORD_FOUND.getFormattedMessage(borrowId),
                        ExceptionEnum.NO_BORROW_RECORD_FOUND));
        var user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!Objects.equals(borrow.getBorowee().get_id(), user.get("_id"))) {

            log.error(ExceptionEnum.USER_CANNOT_PERFORM_THE_ACTION_ON_THIS_BORROW_RECORD.getMessage(), borrowId, VALIDATE_RETURN_ACTION, borrow
                    .getBorowee().getUsername());
            throw new FlowBreakerException(ExceptionEnum.USER_CANNOT_PERFORM_THE_ACTION_ON_THIS_BORROW_RECORD.getFormattedMessage(borrowId,
                    VALIDATE_RETURN_ACTION, borrow.getBorowee().getUsername()),
                    ExceptionEnum.USER_CANNOT_PERFORM_THE_ACTION_ON_THIS_BORROW_RECORD);
        }

        log.debug("Fetching the secret tokens for the borrow: {}", borrowId);
        var tokens = tokenRepository
                .findAllByBorrow_id(borrowId)
                .stream()
                .sorted(Comparator.comparing(BorrowToken::getCreatedDate).reversed()).toList();
        var localDate = LocalDateTime.now();

        if (tokens.isEmpty() || localDate.isAfter(tokens.get(0).getCreatedDate().plusMinutes(tokens.get(0).getValidity()))) {
            log.error(ExceptionEnum.BORROW_TOKEN_EXPIRED_OR_TOKEN_NOT_FOUND.getMessage());
            tokenRepository.deleteAll(tokens);
            throw new FlowBreakerException(ExceptionEnum.BORROW_TOKEN_EXPIRED_OR_TOKEN_NOT_FOUND.getErrorCode(),
                    ExceptionEnum.BORROW_TOKEN_EXPIRED_OR_TOKEN_NOT_FOUND);
        }

        if (!Objects.equals(secretCode, tokens.get(0).getSecretToken())) {
            log.error(ExceptionEnum.BORROW_TOKEN_INVALID.getMessage());
            throw new FlowBreakerException(ExceptionEnum.BORROW_TOKEN_INVALID.getMessage(), ExceptionEnum.BORROW_TOKEN_INVALID);
        }

        log.debug("Secret code is valid. Setting the borrow: {} to RETURNED state", borrowId);
        borrow.setStatus(BorrowStatus.RETURNED);
        borrowRepository.save(borrow);

        log.debug("Deleting the secret token: {}", tokens.get(0).getId());
        tokenRepository.delete(tokens.get(0));
        return new SuccessResponse(HttpStatus.OK, "Borrow returned successfully.");
    }


}