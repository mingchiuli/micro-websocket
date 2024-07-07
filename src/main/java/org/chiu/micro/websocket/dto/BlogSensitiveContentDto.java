package org.chiu.micro.websocket.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BlogSensitiveContentDto {

    private Long id;

    private Long blogId;

    private String sensitiveContentList;

}
