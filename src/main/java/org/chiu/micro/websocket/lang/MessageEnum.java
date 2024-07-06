package org.chiu.micro.websocket.lang;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MessageEnum {

    PUSH_ALL(-1, "推"),

    PULL_ALL(-2, "拉");

    private final Integer code;

    private final String description;
}
