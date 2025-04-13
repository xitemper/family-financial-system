package com.bishe.controller;

import com.bishe.entity.Result;
import com.bishe.service.NotifyService;
import com.bishe.service.UserService;
import com.bishe.vo.UserInfoVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "用户模块")
@RestController
@RequestMapping("/notify")
public class NotifyController {

    @Autowired
    private NotifyService notifyService;

    @ApiOperation("获取当前用户未读的提醒消息")
    @GetMapping("/getUserUnReadNotification")
    public Result getUserUnReadNotification(@RequestParam("userId")Long userId){
        Result result = notifyService.getUserUnReadNotification(userId);
        return result;
    }

    @ApiOperation("获取当前用户的全部提醒消息")
    @GetMapping("/getUserAllReadedNotification")
    public Result getUserAllReadedNotification(@RequestParam("userId")Long userId){
        Result result = notifyService.getUserAllReadedNotification(userId);
        return result;
    }

    @ApiOperation("用户已读当前消息")
    @PostMapping("readNotification")
    public Result readNotification(@RequestParam("userId")Long userId,@RequestParam("notificationId")int notificationId){
        return notifyService.readNotification(userId,notificationId);
    }


}
