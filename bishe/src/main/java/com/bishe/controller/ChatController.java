package com.bishe.controller;

import com.bishe.entity.Result;
import com.bishe.service.ChatService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "用户模块")
@RestController
@RequestMapping("/chat")
public class ChatController {
    @Autowired
    private ChatService chatService;

    @ApiOperation("用户提问")
    @PostMapping("/processChat")
    public Result chat(@RequestParam("userId")Long userId,@RequestParam("userInput")String userInput,@RequestParam("sessionId")String sessionId) {
            return  chatService.processChat(userId,userInput,sessionId);
    }

    @ApiOperation("持久化会话记录")
    @PostMapping("/endSession")
    public Result endSession(@RequestParam("userId") Long userId,@RequestParam("sessionId") String sessionId) {
        chatService.persistSession(userId,sessionId);
        return Result.succeed("会话已保存");
    }

    @ApiOperation("用户开启新对话")
    @PostMapping("/newSession")
    public Result createNewSession(@RequestParam("userId") Long userId) {
        System.out.println("进入新对话 -- 接口");
        return chatService.createNewSession(userId);
    }

    @ApiOperation("获取当前用户历史对话")
    @GetMapping("/historySession")
    public Result getHistorySession(@RequestParam("userId") Long userId){
        return chatService.getHistorySession(userId);
    }

    @ApiOperation("根据会话id加载会话内容（历史）")
    @GetMapping("/loadSession")
    public Result loadSession(@RequestParam("sessionId") String sessionId){
        return chatService.loadSession(sessionId);
    }
}
