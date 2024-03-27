package com.kitaab.hisaab.ledger.service.impl;

import com.kitaab.hisaab.ledger.constants.ExceptionEnum;
import com.kitaab.hisaab.ledger.dto.response.SuccessResponse;
import com.kitaab.hisaab.ledger.entity.borrow.Borrow;
import com.kitaab.hisaab.ledger.entity.borrow.BorrowToken;
import com.kitaab.hisaab.ledger.entity.user.CustomUserDetails;
import com.kitaab.hisaab.ledger.exception.FlowBreakerException;
import com.kitaab.hisaab.ledger.repository.BorrowRepository;
import com.kitaab.hisaab.ledger.repository.BorrowTokenRepository;
import com.kitaab.hisaab.ledger.service.BorrowTokenService;
import com.kitaab.hisaab.ledger.util.SecretTokenUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.kitaab.hisaab.ledger.constants.ApplicationConstants.VALIDATE_BORROW_ACTION;

@Slf4j
@Service
@AllArgsConstructor
public class BorrowTokenServiceImpl implements BorrowTokenService {

    private final BorrowTokenRepository tokenRepository;
    private final BorrowRepository borrowRepository;
    /**
     * @return
     */
    @Override
    public SuccessResponse generateTokenResponse(String borrowId) {
        Borrow borrow = validateBorrowAndGet(borrowId);

        BorrowToken token = generateToken(borrow);
        return new SuccessResponse(HttpStatus.OK, MessageFormat.format("New Secret Token generated for borrowId: {0}", borrowId), Map.of("secretToken", token));
    }

    /**
     * @param borrowId
     * @return
     */
    @Override
    public SuccessResponse getToken(String borrowId) {

        Borrow borrow = validateBorrowAndGet(borrowId);
        log.debug("Finding the secret tokens for the borrow: {}", borrowId);
        List<BorrowToken> tokens = tokenRepository
                .findAllByBorrow_id(borrowId)
                .stream()
                .sorted(Comparator.comparing(BorrowToken::getCreatedDate).reversed()).toList();

        LocalDateTime now = LocalDateTime.now();

        if (tokens.isEmpty() || now.isAfter(tokens.get(0).getCreatedDate().plusMinutes(tokens.get(0).getValidity()))) {
            log.error(ExceptionEnum.BORROW_TOKEN_EXPIRED_OR_TOKEN_NOT_FOUND.getMessage());
            tokenRepository.deleteAll(tokens);
            throw new FlowBreakerException(ExceptionEnum.BORROW_TOKEN_EXPIRED_OR_TOKEN_NOT_FOUND.getErrorCode(),
                    ExceptionEnum.BORROW_TOKEN_EXPIRED_OR_TOKEN_NOT_FOUND);
        }

        return new SuccessResponse(HttpStatus.OK, MessageFormat.format("Secret Token found for borrowId: {0}",borrowId),
                Map.of("secretToken", tokens.get(0)));
    }

    /**
     * @param borrow
     * @return
     */
    @Override
    public BorrowToken generateToken(Borrow borrow) {
        log.debug("Generating the secret token for borrow with id :{}", borrow.getId());
        var secretToken = SecretTokenUtil.generateRandomToken();

        log.debug("Building the token document");
        return tokenRepository.save(BorrowToken.builder()
                .validity(5L)
                .secretToken(secretToken)
                .borrow(borrow)
                .build());
    }

    private Borrow validateBorrowAndGet(String borrowId) {
        Borrow borrow = borrowRepository.findById(borrowId)
                .orElseThrow(() -> new FlowBreakerException(ExceptionEnum.NO_BORROW_RECORD_FOUND.getFormattedMessage(borrowId),
                        ExceptionEnum.NO_BORROW_RECORD_FOUND));
        CustomUserDetails user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!Objects.equals(borrow.getBorrower().get_id(), user.get("_id"))) {
            log.error(ExceptionEnum.USER_CANNOT_PERFORM_THE_ACTION_ON_THIS_BORROW_RECORD.getMessage(),
                    borrowId, VALIDATE_BORROW_ACTION, borrow.getBorrower().getUsername());
            throw new FlowBreakerException(ExceptionEnum.USER_CANNOT_PERFORM_THE_ACTION_ON_THIS_BORROW_RECORD.getFormattedMessage(borrowId,
                    VALIDATE_BORROW_ACTION, borrow.getBorrower().getUsername()), ExceptionEnum.USER_CANNOT_PERFORM_THE_ACTION_ON_THIS_BORROW_RECORD);
        }
        return borrow;
    }
}
