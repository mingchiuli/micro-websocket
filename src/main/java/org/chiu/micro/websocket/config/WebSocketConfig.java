package org.chiu.micro.websocket.config;

import lombok.RequiredArgsConstructor;

import org.chiu.micro.websocket.config.interceptor.CSRFChannelInterceptor;
import org.chiu.micro.websocket.config.interceptor.MessageInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.concurrent.SimpleAsyncTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;


/**
 * @author mingchiuli
 * @create 2021-12-21 11:11 AM
 */
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final CSRFChannelInterceptor csrfChannelInterceptor;

    private final MessageInterceptor messageInterceptor;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/edit/ws")
                .setAllowedOriginPatterns("https://chiu.wiki");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        SimpleAsyncTaskScheduler taskScheduler = new SimpleAsyncTaskScheduler();
        taskScheduler.setVirtualThreads(true);
        //客户端向服务器发消息的前缀
        //客户端订阅消息的前缀
        registry.setApplicationDestinationPrefixes("/app")
                .enableSimpleBroker( "/edits")
                .setTaskScheduler(taskScheduler)
                .setHeartbeatValue(new long[] {2000, 2000});
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(csrfChannelInterceptor, messageInterceptor);
    }
}
