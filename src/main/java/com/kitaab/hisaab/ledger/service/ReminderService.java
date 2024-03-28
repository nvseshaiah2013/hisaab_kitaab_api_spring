package com.kitaab.hisaab.ledger.service;

import com.kitaab.hisaab.ledger.dto.request.reminder.SendReminderRequest;
import com.kitaab.hisaab.ledger.dto.response.SuccessResponse;
import com.kitaab.hisaab.ledger.entity.Reminder;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface ReminderService {

    Page<Reminder> showSentReminders(int pageNumber, int pageSize);

    SuccessResponse sendReminder(SendReminderRequest reminderRequest);

    SuccessResponse readReminder(String reminderId);

    SuccessResponse deleteReminder(String reminderId);

}