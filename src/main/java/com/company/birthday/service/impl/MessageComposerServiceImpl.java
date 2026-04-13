package com.company.birthday.service.impl;

import com.company.birthday.entity.MessageTemplate;
import com.company.birthday.repository.MessageTemplateRepository;
import com.company.birthday.service.MessageComposerService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MessageComposerServiceImpl implements MessageComposerService {

    private final MessageTemplateRepository messageTemplateRepository;

    public MessageComposerServiceImpl(MessageTemplateRepository messageTemplateRepository) {
        this.messageTemplateRepository = messageTemplateRepository;
    }

    @Override
    public String composeBirthdayMessage(Integer messageId, String fullName, String jobTitle) {
        MessageTemplate messageTemplate = messageTemplateRepository.findById(messageId)
                .orElseThrow(() -> new EntityNotFoundException("Message template not found with id: " + messageId));

        String safeFullName = fullName == null ? "" : fullName.trim();
        String safeJobTitle = jobTitle == null ? "" : jobTitle.trim();

        return messageTemplate.getContent()
                .replace("{fullName}", safeFullName)
                .replace("{jobTitle}", safeJobTitle)
                .replace("\r\n", "\n")
                .replace("\r", "\n");
    }
}

