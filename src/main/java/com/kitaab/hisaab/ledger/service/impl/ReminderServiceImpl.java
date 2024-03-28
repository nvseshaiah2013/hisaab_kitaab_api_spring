package com.kitaab.hisaab.ledger.service.impl;

import com.kitaab.hisaab.ledger.dto.request.reminder.SendReminderRequest;
import com.kitaab.hisaab.ledger.dto.response.SuccessResponse;
import com.kitaab.hisaab.ledger.entity.Reminder;
import com.kitaab.hisaab.ledger.repository.ReminderRepository;
import com.kitaab.hisaab.ledger.service.ReminderService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class ReminderServiceImpl implements ReminderService {

    private final ReminderRepository reminderRepository;

    @Override
    public Page<Reminder> showSentReminders(int pageNo, int pageSize) {

        log.info("Fetching sent reminders for user: {}", userId);
        PageRequest pageRequest = PageRequest.of(pageNo,pageSize);

        return reminderRepository.findAllByBorrow_borrower__id(userId,pageRequest);
    }

    @Override
    public SuccessResponse sendReminder(SendReminderRequest reminderRequest) {
        return null;
    }

    @Override
    public SuccessResponse readReminder(String reminderId) {
        return new SuccessResponse();
    }

    @Override
    public SuccessResponse deleteReminder(String reminderId) {

    }
}
