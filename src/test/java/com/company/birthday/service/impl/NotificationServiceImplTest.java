package com.company.birthday.service.impl;

import com.company.birthday.entity.Channel;
import com.company.birthday.entity.Employee;
import com.company.birthday.entity.SendStatus;
import com.company.birthday.service.BirthdayLogService;
import com.company.birthday.service.EmailService;
import com.company.birthday.service.MessageComposerService;
import com.company.birthday.service.WhatsAppService;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

class NotificationServiceImplTest {

    private Employee createEmployee() {
        Employee employee = new Employee();
        employee.setPhoneNumber("+84901122334");
        employee.setEmail("john@example.com");
        employee.setFullName("John");
        employee.setJobTitle("Dev");
        return employee;
    }

    @Test
    void sendsBothChannelsWhenEnabledAndDestinationExists() {
        Employee employee = createEmployee();
        WhatsAppService whatsAppService = mock(WhatsAppService.class);
        EmailService emailService = mock(EmailService.class);
        BirthdayLogService birthdayLogService = mock(BirthdayLogService.class);
        MessageComposerService messageComposerService = mock(MessageComposerService.class);
        NotificationServiceImpl notificationService = new NotificationServiceImpl(
                whatsAppService,
                emailService,
                birthdayLogService,
                messageComposerService,
                true,
                true
        );
        when(messageComposerService.composeBirthdayMessage(1, "John", "Dev")).thenReturn("Happy birthday, John");

        notificationService.sendBirthdayMessage(employee, 1);

        verify(whatsAppService).sendBirthdayMessage("+84901122334", "John", "Dev", 1);
        verify(emailService).sendBirthdayMessage("john@example.com", "John", "Dev", 1);
        verify(birthdayLogService).saveLog(eq(employee), eq(Channel.WHATSAPP), eq(SendStatus.SUCCESS), any(), eq("Happy birthday, John"), eq(null));
        verify(birthdayLogService).saveLog(eq(employee), eq(Channel.EMAIL), eq(SendStatus.SUCCESS), any(), eq("Happy birthday, John"), eq(null));
    }

    @Test
    void skipsDisabledOrMissingDestinationChannels() {
        Employee employee = new Employee();
        employee.setPhoneNumber("");
        employee.setEmail("");
        employee.setFullName("John");
        employee.setJobTitle("Dev");
        WhatsAppService whatsAppService = mock(WhatsAppService.class);
        EmailService emailService = mock(EmailService.class);
        BirthdayLogService birthdayLogService = mock(BirthdayLogService.class);
        MessageComposerService messageComposerService = mock(MessageComposerService.class);
        NotificationServiceImpl notificationService = new NotificationServiceImpl(
                whatsAppService,
                emailService,
                birthdayLogService,
                messageComposerService,
                false,
                true
        );

        notificationService.sendBirthdayMessage(employee, 1);

        verify(whatsAppService, never()).sendBirthdayMessage("", "John", "Dev", 1);
        verify(emailService, never()).sendBirthdayMessage("", "John", "Dev", 1);
        verify(birthdayLogService, never()).saveLog(any(), any(), any(), any(), any(), any());
    }

    @Test
    void continuesSendingOtherChannelsWhenOneChannelFails() {
        Employee employee = createEmployee();
        WhatsAppService whatsAppService = mock(WhatsAppService.class);
        EmailService emailService = mock(EmailService.class);
        BirthdayLogService birthdayLogService = mock(BirthdayLogService.class);
        MessageComposerService messageComposerService = mock(MessageComposerService.class);
        NotificationServiceImpl notificationService = new NotificationServiceImpl(
                whatsAppService,
                emailService,
                birthdayLogService,
                messageComposerService,
                true,
                true
        );
        when(messageComposerService.composeBirthdayMessage(1, "John", "Dev")).thenReturn("Happy birthday, John");

        doThrow(new RuntimeException("twilio down"))
                .when(whatsAppService)
                .sendBirthdayMessage("+84901122334", "John", "Dev", 1);

        notificationService.sendBirthdayMessage(employee, 1);

        verify(emailService).sendBirthdayMessage("john@example.com", "John", "Dev", 1);
        verify(birthdayLogService).saveLog(eq(employee), eq(Channel.WHATSAPP), eq(SendStatus.FAILED), any(), eq("Happy birthday, John"), eq("twilio down"));
        verify(birthdayLogService).saveLog(eq(employee), eq(Channel.EMAIL), eq(SendStatus.SUCCESS), any(), eq("Happy birthday, John"), eq(null));
    }
}

