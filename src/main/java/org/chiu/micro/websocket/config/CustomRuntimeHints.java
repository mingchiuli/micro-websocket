package org.chiu.micro.websocket.config;


import org.chiu.micro.websocket.dto.StompMessageDto;
import org.springframework.aot.hint.*;

org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;


public class CustomRuntimeHints implements RuntimeHintsRegistrar {

    @Override// Register method for reflection
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        // Register resources
        hints.serialization().registerType(StompMessageDto.class);
        hints.serialization().registerType(PreAuthenticatedAuthenticationToken.class);

        hints.resources().registerPattern("script/push-action.lua");
    }
}
