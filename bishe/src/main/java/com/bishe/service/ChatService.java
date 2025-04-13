package com.bishe.service;

import com.alibaba.dashscope.common.Message;
import com.bishe.entity.Result;

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

//    public void autoPersistExpiringSessions();
}
