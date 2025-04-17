package com.bishe.util;

import com.bishe.entity.FinancialHealthDimensionScore;
import com.bishe.entity.FinancialScoreHistory;
import com.bishe.entity.Result;
import com.bishe.mapper.FamilyMapper;
import com.bishe.service.FamilyService;
import com.bishe.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.sql.Date;
import java.util.List;

@Component
public class MyScheduler {

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

    @Scheduled(cron = "0 0 1 1 * ?") // 每月1日凌晨1点执行
    public void generateMonthlyScoreSnapshot() {
        List<Long> familyIds = familyService.getAllFamilyIds(); // 获取所有家庭ID
        for (Long familyId : familyIds) {
            FinancialScoreHistory scoreHistory = new FinancialScoreHistory();
            Result result = familyService.calculateDimensionScores(familyId);
            List<FinancialHealthDimensionScore> list  = (List<FinancialHealthDimensionScore>) result.getContent();
            scoreHistory.setFamilyId(familyId);
            LocalDate firstDayOfLastMonth = LocalDate.now().minusMonths(1).withDayOfMonth(1);
            scoreHistory.setMonth(Date.valueOf(firstDayOfLastMonth));
            scoreHistory.setBalanceScore(list.get(0).getScore());
            scoreHistory.setUnnecessaryExpenseScore(list.get(1).getScore());
            scoreHistory.setBudgetExecutionScore(list.get(2).getScore());
            scoreHistory.setLiabilityScore(list.get(3).getScore());
            familyService.saveFinancialHistoryScore(scoreHistory); // 插入或更新
        }
    }
}