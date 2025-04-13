package com.bishe.mapper;

import com.bishe.entity.ChatMessages;
import com.bishe.entity.User;
import com.bishe.entity.VercodeRecord;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ChatMessagesMapper {

    @Insert("insert into chat_messages(session_id,role,content,created_at) " +
            "values (#{sessionId},#{role},#{content},#{createdAt})")
    int insertRecord(ChatMessages chatMessage);

    @Select("select * from chat_messages where session_id = #{sessionId} order by created_at asc")
    List<ChatMessages> getMessagesBySessionId(String sessionId);
}
