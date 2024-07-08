package org.chiu.micro.websocket.service.impl;

import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import org.chiu.micro.websocket.utils.JsonUtils;
import org.chiu.micro.websocket.dto.BlogEntityDto;
import org.chiu.micro.websocket.dto.BlogSensitiveContentDto;
import org.chiu.micro.websocket.dto.StompMessageDto;
import org.chiu.micro.websocket.rpc.wrapper.BlogHttpServiceWrapper;
import org.chiu.micro.websocket.convertor.BlogEditVoConvertor;
import org.chiu.micro.websocket.convertor.BlogEntityConvertor;
import org.chiu.micro.websocket.key.KeyFactory;
import org.chiu.micro.websocket.req.BlogEditPushActionReq;
import org.chiu.micro.websocket.req.BlogEditPushAllReq;
import org.chiu.micro.websocket.service.BlogMessageService;
import org.chiu.micro.websocket.vo.BlogEditVo;
import org.chiu.micro.websocket.lang.StatusEnum;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.chiu.micro.websocket.lang.Const.*;
import static org.chiu.micro.websocket.lang.MessageActionFieldEnum.*;
import static org.chiu.micro.websocket.lang.MessageEnum.*;

@Service
@RequiredArgsConstructor
public class BlogMessageServiceImpl implements BlogMessageService {

    private final SimpMessagingTemplate simpMessagingTemplate;

    private final StringRedisTemplate redisTemplate;

    private final ResourceLoader resourceLoader;

    private final JsonUtils jsonUtils;

    private final ObjectMapper objectMapper;

    private final BlogHttpServiceWrapper blogHttpServiceWrapper;

    private String pushActionScript;

    private String pushAllScript;

    @PostConstruct
    @SneakyThrows
    private void init() {
        Resource pushActionResource = resourceLoader.getResource(ResourceUtils.CLASSPATH_URL_PREFIX + "script/push-action.lua");
        pushActionScript = pushActionResource.getContentAsString(StandardCharsets.UTF_8);
        Resource pushAllResource = resourceLoader.getResource(ResourceUtils.CLASSPATH_URL_PREFIX + "script/push-all.lua");
        pushAllScript = pushAllResource.getContentAsString(StandardCharsets.UTF_8);
    }

    @Override
    public void pushAction(BlogEditPushActionReq req, Long userId) {
        Long blogId = req.getId();
        String contentChange = req.getContentChange();
        Integer operateTypeCode = req.getOperateTypeCode();
        Integer version = req.getVersion();
        Integer indexStart = req.getIndexStart();
        Integer indexEnd = req.getIndexEnd();
        String field = req.getField();
        Integer paraNo = req.getParaNo();

        String redisKey = KeyFactory.createBlogEditRedisKey(userId, blogId);

        Long execute = redisTemplate.execute(RedisScript.of(pushActionScript, Long.class), Collections.singletonList(redisKey),
                contentChange,
                operateTypeCode.toString(),
                version.toString(),
                Objects.nonNull(indexStart) ? indexStart.toString() : null,
                Objects.nonNull(indexEnd) ? indexEnd.toString() : null,
                Objects.nonNull(field) ? field : null,
                Objects.nonNull(paraNo) ? paraNo.toString() : null);

        if (Long.valueOf(PULL_ALL.getCode()).equals(execute) || Long.valueOf(PUSH_ALL.getCode()).equals(execute)) {
            var dto = StompMessageDto.builder()
                .blogId(blogId)
                .userId(userId)
                .version(version)
                .type(execute.intValue())
                .build();

            simpMessagingTemplate.convertAndSend("/edits/msg", dto);
        }
    }

    @Override
    public void pushAll(BlogEditPushAllReq blog, Long userId) {
        Long id = blog.getId();
        String redisKey = Objects.isNull(id) ?
                TEMP_EDIT_BLOG.getInfo() + userId :
                TEMP_EDIT_BLOG.getInfo() + userId + ":" + id;

        String content = blog.getContent();

        String[] splits = content.split(PARAGRAPH_SPLITTER.getInfo());
        List<String> paragraphList = new ArrayList<>(splits.length + 2);
        Collections.addAll(paragraphList, splits);
        if (content.endsWith(PARAGRAPH_SPLITTER.getInfo())) {
            paragraphList.add("");
        }
        String paragraphListString = jsonUtils.writeValueAsString(paragraphList);

        redisTemplate.execute(RedisScript.of(pushAllScript),
                Collections.singletonList(redisKey),
                paragraphListString, ID.getMsg(), USER_ID.getMsg(), TITLE.getMsg(), DESCRIPTION.getMsg(),
                STATUS.getMsg(), LINK.getMsg(), VERSION.getMsg(), SENSITIVE_CONTENT_LIST.getMsg(),
                Objects.isNull(blog.getId()) ? "" : blog.getId().toString(), userId.toString(), blog.getTitle(),
                blog.getDescription(), blog.getStatus().toString(), blog.getLink(), blog.getVersion().toString(), jsonUtils.writeValueAsString(blog.getSensitiveContentList()),
                A_WEEK.getInfo());
    }

    @Override
    @SneakyThrows
    public BlogEditVo findEdit(Long id, Long userId) {

        String redisKey = KeyFactory.createBlogEditRedisKey(userId, id);
        Map<String, String> entries = redisTemplate.<String, String>opsForHash()
                .entries(redisKey);

        BlogEntityDto blog;
        List<String> sensitiveContentList;
        int version = -1;
        String paragraphListString = null;
        if (!entries.isEmpty()) {
            blog = BlogEntityConvertor.convert(entries);
            sensitiveContentList = objectMapper.readValue(entries.get(SENSITIVE_CONTENT_LIST.getMsg()), new TypeReference<List<String>>() {});
            version = Integer.parseInt(entries.get(VERSION.getMsg()));

            entries.remove(SENSITIVE_CONTENT_LIST.getMsg());
            entries.remove(ID.getMsg());
            entries.remove(USER_ID.getMsg());
            entries.remove(DESCRIPTION.getMsg());
            entries.remove(TITLE.getMsg());
            entries.remove(STATUS.getMsg());
            entries.remove(LINK.getMsg());
            entries.remove(VERSION.getMsg());

            StringBuilder content = new StringBuilder();

            for (int i = 1; i <= entries.size(); i++) {
                String idx = PARAGRAPH_PREFIX.getInfo() + i;
                content.append(entries.get(idx));
                if (i != entries.size()) {
                    content.append(PARAGRAPH_SPLITTER.getInfo());
                }
                if (i == entries.size() && !StringUtils.hasLength(entries.get(idx))) {
                    content.append(PARAGRAPH_SPLITTER.getInfo());
                }
            }
            blog.setContent(content.toString());
        } else if (Objects.isNull(id)) {
            // 新文章
            blog = BlogEntityDto.builder()
                    .status(StatusEnum.NORMAL.getCode())
                    .userId(userId)
                    .content("")
                    .description("")
                    .link("")
                    .title("")
                    .build();
            paragraphListString = "[]";
            sensitiveContentList = new ArrayList<>();
        } else {
            blog = blogHttpServiceWrapper.findByIdAndUserId(id, userId);
            List<String> paragraphList = List.of(blog.getContent().split(PARAGRAPH_SPLITTER.getInfo()));
            paragraphListString = jsonUtils.writeValueAsString(paragraphList);
            BlogSensitiveContentDto sensitiveContentDto = blogHttpServiceWrapper.findSensitiveByBlogId(id);
            String contentList = sensitiveContentDto.getSensitiveContentList();
            if (StringUtils.hasLength(contentList)) {
                sensitiveContentList = Arrays.stream(contentList.split(",")).toList();
            } else {
                sensitiveContentList = new ArrayList<>();
            }
        }

        if (StringUtils.hasLength(paragraphListString)) {
            redisTemplate.execute(RedisScript.of(pushAllScript),
                    Collections.singletonList(redisKey),
                    paragraphListString, ID.getMsg(), USER_ID.getMsg(), TITLE.getMsg(), DESCRIPTION.getMsg(),
                            STATUS.getMsg(), LINK.getMsg(), VERSION.getMsg(),
                    Objects.isNull(blog.getId()) ? "" : blog.getId().toString(), userId.toString(), blog.getTitle(),
                            blog.getDescription(), blog.getStatus().toString(), blog.getLink(), Integer.toString(version),
                    A_WEEK.getInfo());
        }

        return BlogEditVoConvertor.convert(blog, version, sensitiveContentList);
    }

}
