package org.chiu.micro.websocket.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StompMessageDto {

    private Integer version;

    private Long userId;

    private Long blogId;

    private Integer type;
}
