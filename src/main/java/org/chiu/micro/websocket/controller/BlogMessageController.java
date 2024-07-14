package org.chiu.micro.websocket.controller;

import org.chiu.micro.websocket.req.BlogEditPushActionReq;
import org.chiu.micro.websocket.service.BlogMessageService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
public class BlogMessageController {

    private final BlogMessageService blogMessageService;

    @SneakyThrows
    @MessageMapping("/edit/ws/push/action/{userId}")
    public void pushAction(@RequestBody @Valid BlogEditPushActionReq req,
                           @DestinationVariable Long userId) {
        blogMessageService.pushAction(req, userId);
    }
}
