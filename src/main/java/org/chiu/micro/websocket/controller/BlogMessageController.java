package org.chiu.micro.websocket.controller;

import org.chiu.micro.websocket.req.BlogEditPushActionReq;
import org.chiu.micro.websocket.service.BlogMessageService;
import org.chiu.micro.websocket.utils.SecurityUtils;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @MessageMapping("/edit/ws/push/action")
    @PreAuthorize("hasAuthority('sys:edit:push:action')")
    public void pushAction(@RequestBody @Valid BlogEditPushActionReq req) {
        Long userId = SecurityUtils.getLoginUserId();
        blogMessageService.pushAction(req, userId);
    }
}
