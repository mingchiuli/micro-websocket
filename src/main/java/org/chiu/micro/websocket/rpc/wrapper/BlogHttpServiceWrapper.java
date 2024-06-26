package org.chiu.micro.websocket.rpc.wrapper;

import org.chiu.micro.websocket.dto.BlogEntityDto;
import org.chiu.micro.websocket.exception.MissException;
import org.chiu.micro.websocket.lang.Result;
import org.chiu.micro.websocket.rpc.BlogHttpService;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BlogHttpServiceWrapper {

    private final BlogHttpService blogHttpService;

    public BlogEntityDto findByIdAndUserId(Long id, Long userId) {
        Result<BlogEntityDto> result = blogHttpService.findByIdAndUserId(id, userId);
        if (result.getCode() != 200) {
            throw new MissException(result.getMsg());
        }
        return result.getData();
    }


}
