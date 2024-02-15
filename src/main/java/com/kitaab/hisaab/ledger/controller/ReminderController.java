package com.kitaab.hisaab.ledger.controller;

import com.kitaab.hisaab.ledger.Model.Response;
import com.kitaab.hisaab.ledger.dto.request.reminer.SendReminderRequest;
import com.kitaab.hisaab.ledger.dto.response.ErrorMessage;
import com.kitaab.hisaab.ledger.dto.response.SuccessResponse;
import com.kitaab.hisaab.ledger.entity.Reminder;
import com.kitaab.hisaab.ledger.repository.UserRepository;
import com.kitaab.hisaab.ledger.service.ReminderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("api/users/")
public class ReminderController {

    @Autowired
    private ReminderService reminderService;

    @Autowired
    UserRepository userRepository;

    @GetMapping("/sent")
    public Page<Reminder> showSentReminders(@RequestParam(defaultValue = "1", required = false) int pageNo,
                                            @RequestParam(defaultValue = "10",required = false) int pageSize,
                                            @RequestParam(required = true) String userId) {
        Optional.of(userRepository.findById(userId)).get()
                .orElseThrow(() -> new UsernameNotFoundException(String.format("No borrower found for given id %s",userId)));
        return reminderService.showSentReminders(userId,pageNo,pageSize);
    }

    @PostMapping("/sent")
    public Response sendReminder(@RequestBody SendReminderRequest reminderRequest) {
        SuccessResponse successResponse = reminderService.sendReminder(reminderRequest);
        if (successResponse != null)
            return new SuccessResponse(true, "ajkdhas", new Object());
        else
            return new ErrorMessage("true", "ajkdhas");
    }

    @GetMapping("/received")
    public SuccessResponse getReceivedReminder(String id) {
        return new SuccessResponse(true, "ajkdhas", new Object());
    }

    @DeleteMapping("/reminder/reminderId")
    public SuccessResponse readReminder(@RequestParam String reminderId) {
        return new SuccessResponse(true, "ajkdhas", new Object());
    }

    @DeleteMapping("/delete/reminderId")
    public SuccessResponse deleteReminder(@RequestParam String reminderId) {
        Response response = reminderService.deleteReminder(reminderId);
        return new SuccessResponse(true, "ajkdhas", new Object());
    }

}
