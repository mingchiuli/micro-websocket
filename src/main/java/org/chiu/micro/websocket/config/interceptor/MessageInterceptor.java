package org.chiu.micro.websocket.config.interceptor;


import lombok.RequiredArgsConstructor;

import java.util.Optional;

import org.chiu.micro.websocket.rpc.wrapper.AuthHttpServiceWrapper;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * @author mingchiuli
 * @create 2022-06-17 9:46 PM
 */
@Component
@RequiredArgsConstructor
public class MessageInterceptor implements ChannelInterceptor {

    private final AuthHttpServiceWrapper authHttpServiceWrapper;

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
    
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = Optional.ofNullable(accessor.getFirstNativeHeader("Authorization")).orElse("");
            Authentication authentication = authHttpServiceWrapper.getAuthentication(token);
            accessor.setUser(authentication);
        }
        return message;
    }

}
