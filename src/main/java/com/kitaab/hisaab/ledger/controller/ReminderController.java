package com.kitaab.hisaab.ledger.controller;


import com.kitaab.hisaab.ledger.dto.request.reminder.SendReminderRequest;
import com.kitaab.hisaab.ledger.dto.response.SuccessResponse;
import com.kitaab.hisaab.ledger.entity.Reminder;
import com.kitaab.hisaab.ledger.repository.UserRepository;
import com.kitaab.hisaab.ledger.service.ReminderService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("api/users/")
@AllArgsConstructor
public class ReminderController {

    private ReminderService reminderService;

    private UserRepository userRepository;

    @GetMapping("/sent")
    public Page<Reminder> showSentReminders(@RequestParam(defaultValue = "1", required = false) int pageNo,
                                            @RequestParam(defaultValue = "10", required = false) int pageSize,
                                            @RequestParam String userId) {
        return reminderService.showSentReminders(userId, pageNo, pageSize);
    }

    @PostMapping("/sent")
    public ResponseEntity<SuccessResponse> sendReminder(@RequestBody SendReminderRequest reminderRequest) {
        return ResponseEntity.ok().body(reminderService.sendReminder(reminderRequest));

    }

    @GetMapping("/received")
    public SuccessResponse getReceivedReminder(String id) {
        return new SuccessResponse(HttpStatus.OK, "ajkdhas", Map.of());
    }

    @DeleteMapping("/reminder/reminderId")
    public SuccessResponse readReminder(@RequestParam String reminderId) {
        return new SuccessResponse(HttpStatus.OK, "ajkdhas", Map.of());
    }

    @DeleteMapping("/delete/reminderId")
    public ResponseEntity<SuccessResponse> deleteReminder(@RequestParam String reminderId) {
        return ResponseEntity.ok().body(reminderService.deleteReminder(reminderId));
    }

}
