package com.kitaab.hisaab.ledger.repository;

import com.kitaab.hisaab.ledger.entity.borrow.BorrowToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BorrowTokenRepository extends MongoRepository<BorrowToken, String> {

    List<BorrowToken> findAllByBorrowId(String borrowId);
}
