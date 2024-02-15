package com.kitaab.hisaab.ledger.service.impl;

import com.kitaab.hisaab.ledger.dto.request.reminer.SendReminderRequest;
import com.kitaab.hisaab.ledger.dto.response.SuccessResponse;
import com.kitaab.hisaab.ledger.entity.Reminder;
import com.kitaab.hisaab.ledger.repository.ReminderRepository;
import com.kitaab.hisaab.ledger.service.ReminderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.util.Optional;

@Service
@Slf4j
public class ReminderServiceImpl implements ReminderService {

    @Autowired
    private ReminderRepository repository;

    @Override
    public Page<Reminder> showSentReminders(String userId , int pageNo, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNo,pageSize);
        return Optional.ofNullable(repository.findByBorrowerId(userId,pageRequest))
                .orElseThrow(()-> new UsernameNotFoundException("Borrower with given id doesn't exists"));
    }

    @Override
    public SuccessResponse sendReminder(SendReminderRequest reminderRequest) {
        return null;
    }

    @Override
    public SuccessResponse readReminder(String reminderId) {
        return null;
    }

    @Override
    public SuccessResponse deleteReminder(String reminderId) {
        return null;
    }
}
