package com.kitaab.hisaab.ledger.controller;

import com.kitaab.hisaab.ledger.dto.request.borrow.ItemBorrowRequest;
import com.kitaab.hisaab.ledger.dto.request.borrow.MoneyBorrowRequest;
import com.kitaab.hisaab.ledger.dto.request.borrow.SecretToken;
import com.kitaab.hisaab.ledger.dto.response.SuccessResponse;
import com.kitaab.hisaab.ledger.service.BorrowService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.kitaab.hisaab.ledger.constants.ApplicationConstants.BORROWS_BASE_URL;
import static com.kitaab.hisaab.ledger.constants.ApplicationConstants.BORROW_ITEMS;
import static com.kitaab.hisaab.ledger.constants.ApplicationConstants.BORROW_ITEMS_BY_ID;
import static com.kitaab.hisaab.ledger.constants.ApplicationConstants.REJECT_BORROW;
import static com.kitaab.hisaab.ledger.constants.ApplicationConstants.RETURN_BORROW_ITEM_BY_ID;
import static com.kitaab.hisaab.ledger.constants.ApplicationConstants.TAKEN_ITEMS;
import static com.kitaab.hisaab.ledger.constants.ApplicationConstants.VALIDATE_BORROW;
import static com.kitaab.hisaab.ledger.constants.ApplicationConstants.VALIDATE_RETURN;

@RestController
@RequestMapping(BORROWS_BASE_URL)
@Slf4j
@AllArgsConstructor
public class BorrowController {

    private final BorrowService<ItemBorrowRequest> itemBorrowService;

    private final BorrowService<MoneyBorrowRequest> moneyBorrowService;

    @PostMapping(BORROW_ITEMS)
    public ResponseEntity<SuccessResponse> borrowItem(@Valid @RequestBody ItemBorrowRequest borrowRequest) {
        log.info("Request to borrow item {} to user {}", borrowRequest.getItemName(), borrowRequest.getBorowee());
        return ResponseEntity.ok(itemBorrowService.createBorrow(borrowRequest));
    }

    @GetMapping(BORROW_ITEMS)
    public ResponseEntity<SuccessResponse> getGivenItems() {
        log.info("Request to fetch the given items");
        return ResponseEntity.ok(itemBorrowService.getGivenBorrows());
    }

    @GetMapping(TAKEN_ITEMS)
    public ResponseEntity<SuccessResponse> getTakenItems() {
        log.info("Request to fetch the taken items");
        return ResponseEntity.ok(itemBorrowService.getTakenBorrows());
    }

    @PutMapping(BORROW_ITEMS_BY_ID)
    public ResponseEntity<SuccessResponse> updateBorrowItem(@PathVariable("borrowId") String borrowId, @RequestBody ItemBorrowRequest updateBorrowRequest) {
        log.info("Request to update borrow with id: {}", borrowId);
        return ResponseEntity.ok(itemBorrowService.updateBorrow(borrowId, updateBorrowRequest));
    }

    @DeleteMapping(BORROW_ITEMS_BY_ID)
    public ResponseEntity<SuccessResponse> deleteBorrowById(@PathVariable("borrowId") String borrowId) {
        log.info("Request to delete borrow with id: {}", borrowId);
        return ResponseEntity.ok(itemBorrowService.deleteBorrow(borrowId));
    }

    @PostMapping(RETURN_BORROW_ITEM_BY_ID)
    public ResponseEntity<SuccessResponse> returnBorrowById(@PathVariable("borrowId") String borrowId) {
        log.info("Request to return the borrow by id: {}", borrowId);
        return ResponseEntity.ok(itemBorrowService.returnBorrow(borrowId));
    }

    @PostMapping(VALIDATE_BORROW)
    public ResponseEntity<SuccessResponse> validateBorrow(@PathVariable("borrowId") String borrowId, @RequestBody SecretToken secretToken) {
        log.info("Request to validate borrow by id: {}", borrowId);
        return ResponseEntity.ok(itemBorrowService.validateBorrow(borrowId, secretToken.getSecretToken()));
    }

    @PostMapping(VALIDATE_RETURN)
    public ResponseEntity<SuccessResponse> validateReturn(@PathVariable("borrowId") String borrowId, @RequestBody SecretToken secretToken) {
        log.info("Request to validate return by id: {}", borrowId);
        return ResponseEntity.ok(itemBorrowService.validateReturn(borrowId, secretToken.getSecretToken()));
    }

    @PostMapping(REJECT_BORROW)
    public ResponseEntity<SuccessResponse> rejectBorrow(@PathVariable("borrowId") String borrowId) {
        log.info("Request to reject borrow by id: {}", borrowId);
        return ResponseEntity.ok(itemBorrowService.rejectBorrow(borrowId));
    }
}
