package com.company.birthday.service.impl;

import com.company.birthday.config.TwilioConfig;
import com.company.birthday.service.MessageComposerService;
import com.company.birthday.service.WhatsAppService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.twilio.type.PhoneNumber;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class WhatsAppServiceImpl implements WhatsAppService {

    private final TwilioConfig twilioConfig;
    private final MessageComposerService messageComposerService;
    private final ObjectMapper objectMapper;

    public WhatsAppServiceImpl(TwilioConfig twilioConfig,
                               MessageComposerService messageComposerService,
                               ObjectMapper objectMapper) {
        this.twilioConfig = twilioConfig;
        this.messageComposerService = messageComposerService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void sendBirthdayMessage(String to, String fullName, String jobTitle, Integer messageId) {
        String filledContent = messageComposerService.composeBirthdayMessage(messageId, fullName, jobTitle)
                .replace("\r\n", "\n")
                .replace("\r", "\n");

        Twilio.init(
                twilioConfig.getAccountSid(),
                twilioConfig.getAuthToken()
        );

        Message.creator(
                new PhoneNumber("whatsapp:" + to),
                new PhoneNumber("whatsapp:" + twilioConfig.getFromNumber()),
                ""
        )
                .setContentSid(twilioConfig.getContentSid())
                .setContentVariables(buildContentVariables(filledContent))
                .create();

        log.info("WHATSAPP DEBUG to={}, from={}, contentSid={}, content={}",
                to,
                twilioConfig.getFromNumber(),
                twilioConfig.getContentSid(),
                filledContent
        );
    }

    private String buildContentVariables(String filledContent) {
        try {
            // Twilio expects a JSON string; newline in value is preserved as \n and rendered as a line break.
            return objectMapper.writeValueAsString(Map.of("1", filledContent));
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Cannot build Twilio content variables", e);
        }
    }
}
