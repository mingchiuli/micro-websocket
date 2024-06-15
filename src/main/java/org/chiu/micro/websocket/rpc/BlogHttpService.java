package org.chiu.micro.websocket.rpc;

import org.chiu.micro.websocket.dto.BlogEntityDto;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

public interface BlogHttpService {

    @GetExchange("/blog/{blogId}/{userId}")
    BlogEntityDto findByIdAndUserId(@PathVariable(value = "blogId") Long blogId,
                                    @PathVariable(value = "userId") Long userId);
}
