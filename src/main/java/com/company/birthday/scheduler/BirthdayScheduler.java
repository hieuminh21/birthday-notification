package com.company.birthday.scheduler;

import com.company.birthday.service.BirthdayService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class BirthdayScheduler {

    private final BirthdayService birthdayService;
    private final Integer messageTemplateId;

    public BirthdayScheduler(BirthdayService birthdayService,
                             @Value("${birthday.scheduler.message-template-id:1}") Integer messageTemplateId) {
        this.birthdayService = birthdayService;
        this.messageTemplateId = messageTemplateId;
    }

    @Scheduled(cron = "0 40 19 * * ?", zone = "Asia/Ho_Chi_Minh")
    public void sendBirthdayMessageDaily() {
        birthdayService.handleTodayBirthday(messageTemplateId);
    }
}
