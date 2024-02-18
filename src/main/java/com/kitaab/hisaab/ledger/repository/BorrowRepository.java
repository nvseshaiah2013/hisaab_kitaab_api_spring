package com.kitaab.hisaab.ledger.repository;

import com.kitaab.hisaab.ledger.entity.borrow.Borrow;
import com.kitaab.hisaab.ledger.entity.borrow.BorrowItem;
import com.kitaab.hisaab.ledger.entity.user.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BorrowRepository extends MongoRepository<Borrow, String> {

    List<BorrowItem> findAllByBorrower__id(String id);

    List<BorrowItem> findAllByBorowee__id(String id);


}