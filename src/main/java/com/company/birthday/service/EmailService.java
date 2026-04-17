package com.company.birthday.service;

public interface EmailService {
    void sendBirthdayMessage(String to, String fullName, String jobTitle, Integer messageId);
}

