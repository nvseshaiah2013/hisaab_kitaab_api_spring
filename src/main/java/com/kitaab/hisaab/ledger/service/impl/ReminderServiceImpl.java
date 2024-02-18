package com.kitaab.hisaab.ledger.service.impl;

import com.kitaab.hisaab.ledger.dto.request.reminder.SendReminderRequest;
import com.kitaab.hisaab.ledger.dto.response.SuccessResponse;
import com.kitaab.hisaab.ledger.entity.Reminder;
import com.kitaab.hisaab.ledger.repository.ReminderRepository;
import com.kitaab.hisaab.ledger.service.ReminderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class ReminderServiceImpl implements ReminderService {

    @Autowired
    private ReminderRepository reminderRepo;

    @Override
    public Page<Reminder> showSentReminders(String userId , int pageNo, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNo,pageSize);
        return Optional.ofNullable(reminderRepo.findByBorrowerId(userId,pageRequest))
                .orElseThrow(()-> new UsernameNotFoundException("Borrower with given id doesn't exists"));
    }

    @Override
    public SuccessResponse sendReminder(SendReminderRequest reminderRequest) {
        return null;
    }

    @Override
    public SuccessResponse readReminder(String reminderId) {
        return Optional.of(reminderRepo.findById(reminderId)).get()
                .map(reminder -> new SuccessResponse(HttpStatus.OK,String.format("Successfully fetch the reminders for reminderId %s",reminderId),reminder))
                .orElseThrow(() -> new RuntimeException(String.format("No reminders found for the given reminderId %s",reminderId)));
    }

    @Override
    public SuccessResponse deleteReminder(String reminderId) {
        Reminder reminder = Optional.of(reminderRepo.findById(reminderId)).get()
                .orElseThrow(() -> new RuntimeException(String.format("No reminder found for reminderId %s",reminderId)));
        reminderRepo.deleteById(reminderId);
        return new SuccessResponse(HttpStatus.OK,"Successfully deleted",reminder);
    }
}
