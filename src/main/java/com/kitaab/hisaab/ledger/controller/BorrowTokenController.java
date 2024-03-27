package com.kitaab.hisaab.ledger.controller;

import com.kitaab.hisaab.ledger.dto.response.SuccessResponse;
import com.kitaab.hisaab.ledger.entity.borrow.BorrowToken;
import com.kitaab.hisaab.ledger.service.BorrowTokenService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.kitaab.hisaab.ledger.constants.ApplicationConstants.*;


@RestController
@RequestMapping(TOKEN_BASE_URL)
@AllArgsConstructor
@Slf4j
public class BorrowTokenController {

    BorrowTokenService tokenService;

    @GetMapping(GET_TOKEN)
    ResponseEntity<SuccessResponse> getToken(@PathVariable String borrowId) {
        log.info("getting the token for borrowId: {}", borrowId);
        return ResponseEntity.ok(tokenService.getToken(borrowId));
    }

    @GetMapping(GENERATE_TOKEN)
    ResponseEntity<SuccessResponse> generateToken(@PathVariable String borrowId) {
        log.info("generating new token for borrowId: {}", borrowId);
        return ResponseEntity.ok(tokenService.generateTokenResponse(borrowId));
    }

}
