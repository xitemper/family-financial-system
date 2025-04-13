package com.bishe.mapper;

import com.bishe.entity.Family;
import com.bishe.entity.FamilyMember;
import com.bishe.entity.FamilyRole;
import com.bishe.entity.UserNotifications;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface NotifyMapper {

    @Select("select * from user_notifications where user_id=#{userId} and is_read = 0")
    List<UserNotifications> getUserUnReadNotification(@Param("userId") Long userId);

    @Select("select * from user_notifications where user_id=#{userId} and is_read = 1")
    List<UserNotifications> getUserAllReadedNotification(@Param("userId")Long userId);

    @Insert("insert into user_notifications (user_id,type,message) VALUES (#{userNotifications.userId},#{userNotifications.type},#{userNotifications.message})")
    void addNotify(@Param("userNotifications") UserNotifications userNotifications);

    @Update("update user_notifications set is_read = 1 where user_id = #{userId} and id = #{notificationId}")
    void readNotification(@Param("userId") Long userId,@Param("notificationId") int notificationId);
}
