package com.kitaab.hisaab.ledger.controller;


import com.kitaab.hisaab.ledger.dto.request.reminder.SendReminderRequest;
import com.kitaab.hisaab.ledger.dto.response.SuccessResponse;
import com.kitaab.hisaab.ledger.entity.Reminder;
import com.kitaab.hisaab.ledger.service.ReminderService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/reminder")
@AllArgsConstructor
public class ReminderController {

    private ReminderService reminderService;

    @GetMapping("/sent")
    public Page<Reminder> showSentReminders(@RequestParam(defaultValue = "1", required = false) int pageNo,
                                            @RequestParam(defaultValue = "10", required = false) int pageSize) {
        return reminderService.showSentReminders(pageNo, pageSize);
    }

    @PostMapping("/sent")
    public ResponseEntity<SuccessResponse> sendReminder(@RequestBody SendReminderRequest reminderRequest) {
        return ResponseEntity.ok().body(reminderService.sendReminder(reminderRequest));

    }

    @GetMapping("/received")
    public SuccessResponse getReceivedReminder(String id) {
        return new SuccessResponse(HttpStatus.OK, "ajkdhas", Map.of());
    }

    @DeleteMapping("/reminder/{reminderId}")
    public SuccessResponse readReminder(@RequestParam String reminderId) {
        return new SuccessResponse(HttpStatus.OK, "ajkdhas", Map.of());
    }

    @DeleteMapping("/delete/{reminderId}")
    public ResponseEntity<SuccessResponse> deleteReminder(@RequestParam String reminderId) {
        return ResponseEntity.ok().body(reminderService.deleteReminder(reminderId));
    }

}
