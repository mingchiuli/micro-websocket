package org.chiu.micro.websocket.controller;

import org.chiu.micro.websocket.lang.Result;
import org.chiu.micro.websocket.req.BlogEditPushActionReq;
import org.chiu.micro.websocket.req.BlogEditPushAllReq;
import org.chiu.micro.websocket.service.BlogMessageService;
import org.chiu.micro.websocket.vo.BlogEditVo;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequiredArgsConstructor
@Validated
public class BlogMessageController {

    private final BlogMessageService blogMessageService;

    private final ObjectMapper objectMapper;

    @SneakyThrows
    @MessageMapping("/edit/ws/push/action/{userId}")
    public void pushAction(@RequestBody String msg,
                           @DestinationVariable Long userId) {
        BlogEditPushActionReq req = objectMapper.readValue(msg, BlogEditPushActionReq.class);
        blogMessageService.pushAction(req, userId);
    }

    @PostMapping("/edit/push/all/{userId}")
    public Result<Void> pullSaveBlog(@RequestBody @Valid BlogEditPushAllReq blog,
                                     @PathVariable Long userId) {
        return Result.success(() -> blogMessageService.pushAll(blog, userId));
    }

    @GetMapping("/edit/pull/echo/{userId}")
    public Result<BlogEditVo> getEchoDetail(@RequestParam(value = "blogId", required = false) Long id,
                                            @PathVariable Long userId) {
        return Result.success(() -> blogMessageService.findEdit(id, userId));
    }
}
