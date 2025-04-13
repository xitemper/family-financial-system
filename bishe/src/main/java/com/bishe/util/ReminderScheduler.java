package com.bishe.util;

import com.bishe.service.FamilyService;
import com.bishe.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ReminderScheduler {

    @Autowired
    private UserService userService;

    @Autowired
    private FamilyService familyService;

    // 每天半夜 12 点执行
//    @Scheduled(cron = "0 0 0 * * ?")
    @Scheduled(cron = "0 * * * * ?") //测试--每一分钟执行一次
    public void runPlanReminder() {
        userService.checkAndRemindPlans();
        System.out.println("还款/储蓄计划 定时任务执行:"+ LocalDateTime.now());
    }

    // 每天半夜 12 点执行
//    @Scheduled(cron = "0 0 0 * * ?")
    @Scheduled(cron = "0 * * * * ?") //测试--每一分钟执行一次
    public void runBudgetReminder() {
        familyService.checkAndRemindBudget();
        System.out.println("家庭组预算超出预警 巡查 定时任务执行:"+ LocalDateTime.now());
    }
}