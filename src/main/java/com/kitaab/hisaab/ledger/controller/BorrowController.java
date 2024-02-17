package com.kitaab.hisaab.ledger.controller;

import com.kitaab.hisaab.ledger.dto.request.borrow.ItemBorrowRequest;
import com.kitaab.hisaab.ledger.dto.request.borrow.MoneyBorrowRequest;
import com.kitaab.hisaab.ledger.dto.response.SuccessResponse;
import com.kitaab.hisaab.ledger.service.BorrowService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.kitaab.hisaab.ledger.constants.ApplicationConstants.BORROWS_BASE_URL;
import static com.kitaab.hisaab.ledger.constants.ApplicationConstants.BORROW_ITEMS;

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
}
