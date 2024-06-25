package org.chiu.micro.websocket.config;

import lombok.SneakyThrows;
import org.springframework.aot.hint.*;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.springframework.util.ReflectionUtils.*;

@SuppressWarnings("all")
public class CustomRuntimeHints implements RuntimeHintsRegistrar {
    @SneakyThrows
    @Override// Register method for reflection
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        // Register resources
        hints.resources().registerPattern("script/push-action.lua");
        hints.resources().registerPattern("script/push-all.lua");
    }
}
