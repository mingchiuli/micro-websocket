package org.chiu.micro.websocket.convertor;

import java.util.List;

import org.chiu.micro.websocket.dto.BlogEntityDto;
import org.chiu.micro.websocket.vo.BlogEditVo;

public class BlogEditVoConvertor {

    private BlogEditVoConvertor() {}

    public static BlogEditVo convert(BlogEntityDto blog, Integer version, List<String> sensitiveContentList) {
        return BlogEditVo.builder()
                .userId(blog.getUserId())
                .id(blog.getId())
                .title(blog.getTitle())
                .description(blog.getDescription())
                .content(blog.getContent())
                .link(blog.getLink())
                .version(version)
                .status(blog.getStatus())
                .sensitiveContentList(sensitiveContentList)
                .build();
    }
}
