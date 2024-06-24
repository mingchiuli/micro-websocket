package org.chiu.micro.websocket.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class MessageTemplateConfig {

    private final SimpMessagingTemplate simpMessagingTemplate;

    @PostConstruct
    private void init() {
        simpMessagingTemplate.setMessageConverter(new MappingJackson2MessageConverter());
    }
}
