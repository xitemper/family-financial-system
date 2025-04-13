package com.bishe.service;

import com.bishe.entity.Result;

public interface NotifyService {

    Result getUserUnReadNotification(Long userId);

    Result getUserAllReadedNotification(Long userId);

    Result readNotification(Long userId, int notificationId);
}
