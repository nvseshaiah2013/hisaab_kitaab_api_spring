package com.kitaab.hisaab.ledger.controller;

import com.kitaab.hisaab.ledger.dto.response.SuccessResponse;
import com.kitaab.hisaab.ledger.service.BorrowTokenService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.kitaab.hisaab.ledger.constants.ApplicationConstants.BORROW_ID;
import static com.kitaab.hisaab.ledger.constants.ApplicationConstants.TOKEN_BASE_URL;


@RestController
@RequestMapping(TOKEN_BASE_URL)
@AllArgsConstructor
@Slf4j
public class BorrowTokenController {

    private BorrowTokenService tokenService;

    @GetMapping(BORROW_ID)
    ResponseEntity<SuccessResponse> getToken(@PathVariable String borrowId) {
        log.info("getting the token for borrowId: {}", borrowId);
        return ResponseEntity.ok(tokenService.getToken(borrowId));
    }

    @PostMapping(BORROW_ID)
    ResponseEntity<SuccessResponse> generateToken(@PathVariable String borrowId) {
        log.info("generating new token for borrowId: {}", borrowId);
        return ResponseEntity.ok(tokenService.generateTokenResponse(borrowId));
    }

}
