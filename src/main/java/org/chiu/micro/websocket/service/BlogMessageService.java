package org.chiu.micro.websocket.service;

import org.chiu.micro.websocket.req.BlogEditPushActionReq;
import org.chiu.micro.websocket.req.BlogEditPushAllReq;
import org.chiu.micro.websocket.vo.BlogEditVo;

public interface BlogMessageService {

    void pushAction(BlogEditPushActionReq req, Long userId);

    void pushAll(BlogEditPushAllReq blog, Long userId);

    BlogEditVo findEdit(Long id, Long userId);
}
