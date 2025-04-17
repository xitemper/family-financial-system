package com.bishe.service;

import com.alibaba.dashscope.common.Message;
import com.bishe.entity.FinancialHealthDimensionScore;
import com.bishe.entity.Result;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

public interface ChatService {
    Result processChat(Long userId,String userInput,String sessionId);

    void deleteSession(String sessionId);

    List<Message> getSessionMessage(String sessionId);

    void saveSession(String sessionId, List<Message> messages);

    Result createNewSession(Long userId);

    public void persistSession(Long userId,String sessionId);

    Result getHistorySession(Long userId);

    Result loadSession(String sessionId);

    Result getOverAllAdvice(List<FinancialHealthDimensionScore> scores);

    SseEmitter processChatStream(Long userId, String userInput, String sessionId);

//    public void autoPersistExpiringSessions();
}
