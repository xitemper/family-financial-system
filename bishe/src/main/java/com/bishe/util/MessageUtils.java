package com.bishe.util;

import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import org.hibernate.result.Output;
import org.springframework.stereotype.Component;

@Component
public class MessageUtils {
    // 转换阿里云API响应到Message
    public static Message convertResponse(GenerationResult result) {
        return Message.builder()
                .role(Role.ASSISTANT.getValue())
                .content(result.getOutput().getChoices().get(0).getMessage().getContent())
                .build();
    }

    // 转换String响应到Message
    public static Message convertString(String result) {
        return Message.builder()
                .role(Role.ASSISTANT.getValue())
                .content(result)
                .build();
    }

    public static Message createMessage(Role role, String content) {
        return Message.builder().role(role.getValue()).content(content).build();
    }
}