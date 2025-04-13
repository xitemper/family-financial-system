package com.bishe.mapper;

import com.bishe.entity.ChatSessions;
import com.bishe.entity.User;
import com.bishe.entity.VercodeRecord;
import org.apache.ibatis.annotations.*;

import javax.validation.constraints.Size;
import java.util.List;

@Mapper
public interface ChatSessionsMapper {

    @Insert("insert into chat_sessions (session_id, user_id, title, last_active) " +
            "VALUES (#{sessionId},#{userId},#{title},#{lastActive})")
    int insertRecord(ChatSessions chatSession);

    @Select("select * from chat_sessions where user_id = #{userId} order by last_active desc")
    List<ChatSessions> getSessionsByUserId(@Param("userId") Long userId);
}
