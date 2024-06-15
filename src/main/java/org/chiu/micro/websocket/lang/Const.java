package org.chiu.micro.websocket.lang;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author mingchiuli
 * @create 2021-12-14 11:58 AM
 */
@Getter
@AllArgsConstructor
public enum Const {

    A_WEEK("604899"),

    PARAGRAPH_PREFIX("para::"),

    PARAGRAPH_SPLITTER("\n\n"),

    TEMP_EDIT_BLOG("temp_edit_blog:");

    private final String info;

}

