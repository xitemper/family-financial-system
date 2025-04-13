package com.bishe.service.serviceImpl;

import com.bishe.entity.Result;
import com.bishe.entity.UserNotifications;
import com.bishe.mapper.NotifyMapper;
import com.bishe.mapper.UserMapper;
import com.bishe.service.NotifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotifyServiceImpl implements NotifyService {


    @Autowired
    private UserMapper userMapper;

    @Autowired
    private NotifyMapper notifyMapper;

    @Override
    public Result getUserUnReadNotification(Long userId) {
        List<UserNotifications> userNotificationsList = notifyMapper.getUserUnReadNotification(userId);
        return Result.succeed("getUserUnReadNotification方法调用成功!",userNotificationsList);
    }

    @Override
    public Result getUserAllReadedNotification(Long userId) {
        List<UserNotifications> userAllReadedNotification = notifyMapper.getUserAllReadedNotification(userId);
        return Result.succeed("getUserAllReadedNotification方法调用成功",userAllReadedNotification);
    }

    @Override
    public Result readNotification(Long userId, int notificationId) {
        notifyMapper.readNotification(userId,notificationId);
        return Result.succeed("当前提醒消息已修改成已读");
    }
}
