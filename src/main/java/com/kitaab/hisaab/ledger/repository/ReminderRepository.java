package com.kitaab.hisaab.ledger.repository;

import com.kitaab.hisaab.ledger.entity.Reminder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReminderRepository extends MongoRepository<Reminder, String> {

    Page<Reminder> findAllByBorrow_borrower__id(String userId, PageRequest pageRequest);

    Page<Reminder> findAllByBorrow_borowee__id(String userId, PageRequest pageRequest);
}
