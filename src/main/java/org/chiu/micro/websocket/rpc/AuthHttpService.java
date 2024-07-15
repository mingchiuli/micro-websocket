package org.chiu.micro.websocket.rpc;

import org.chiu.micro.websocket.lang.Result;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;


public interface AuthHttpService {

    @GetExchange("/auth/{token}")
    Result<Authentication> getAuthentication(@PathVariable String token);

}
