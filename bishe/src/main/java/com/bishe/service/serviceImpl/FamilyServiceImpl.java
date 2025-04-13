package com.bishe.service.serviceImpl;

import com.bishe.controller.FamilyController;
import com.bishe.entity.*;
import com.bishe.mapper.*;
import com.bishe.service.FamilyService;
import com.bishe.service.UserService;
import com.bishe.util.DateUtils;
import com.bishe.util.FamilyCodeGenerator;
import com.bishe.util.PermissionCode;
import com.bishe.vo.FamilyMemberVO;
import com.bishe.vo.UserInfoVO;
import com.bishe.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.validation.constraints.Max;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.*;

@Service
public class FamilyServiceImpl implements FamilyService {

    @Autowired
    private FamilyMapper familyMapper;

    @Autowired
    private FamilyCodeGenerator familyCodeGenerator;
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private TransactionMapper transactionMapper;

    @Autowired
    private NotifyMapper notifyMapper;

    @Override
    @Transactional
    public Result createFamily(Long userId, String familyName) {
        String familyCode = familyCodeGenerator.generateUniqueFamilyCode();
        int result1 = familyMapper.createFamily(userId, familyName, familyCode);
        Family family = familyMapper.getFamilyByCode(familyCode);
        int result2 = familyMapper.insertMemberRecord(family.getFamilyId(), userId, 1);

        //为当前用户添加所有家庭组权限
        List<String> permissionCodeList = permissionMapper.getAllPermissionCode();
        for (String permissionCode : permissionCodeList) {
            permissionMapper.addUserPermisson(family.getFamilyId(), userId, permissionCode);
        }

        if (result1 > 0 && result2 > 0) {
            return Result.succeed("创建成功！", family);
        }
        return Result.fail("创建失败，请稍后重试！");
    }

    @Override
    public Result getFamilyInfoOfUser(Long userId) {
        Long familyId = familyMapper.getFamilyIdByUserId(userId);
        if (familyId != null) {
            Family family = familyMapper.getFamilyInfoById(familyId);
            if (family != null) {
                return Result.succeed("", family);
            } else {
                return Result.fail("当前用户没有家庭组");
            }
        } else {
            return Result.fail("当前用户没有家庭组");
        }
    }

    @Override
    public Result joinFamily(Long userId, String familyCode) {
        Long familyId = familyMapper.getFamilyIdByCode(familyCode);
        User userInfo = userMapper.getUserInfById(userId);
        if (familyId != null) {
            int result = familyMapper.insertFamilyMember(userId, familyId);
            Family family = familyMapper.getFamilyByCode(familyCode);
            if (result > 0 && family != null) {

                //给家庭组其他人加通知
                List<FamilyMember> familyMemberInfo = familyMapper.getFamilyMemberInfo(familyId);
                if (familyMemberInfo != null && !familyMemberInfo.isEmpty()) {
                    for (FamilyMember familyMember : familyMemberInfo) {
                        if (familyMember.getUserId() != userId) {
                            UserNotifications userNotifications = new UserNotifications();
                            userNotifications.setUserId(familyMember.getUserId());
                            userNotifications.setType(2);
                            userNotifications.setMessage("用户：" + userInfo.getUsername() + "加入了您的家庭组!");
                            notifyMapper.addNotify(userNotifications);
                        }
                    }
                }
                return Result.succeed("加入成功！", family);
            } else {
                return Result.fail("加入失败，请稍后重试！");
            }
        } else {
            return Result.fail("家庭组不存在!");
        }

    }

    @Override
    public Result getFamilyMemberInfo(Long familyId) {
        List<FamilyMemberVO> voList = new ArrayList<>();
        List<FamilyMember> mapperList = familyMapper.getFamilyMemberInfo(familyId);
        for (FamilyMember mapper : mapperList) {
            FamilyMemberVO familyMemberVO = new FamilyMemberVO();
            User user = userMapper.getUserInfById(mapper.getUserId());
            familyMemberVO.setUserId(user.getUserId());
            familyMemberVO.setUserName(user.getUsername());
            FamilyRole role = familyMapper.getRoleByRoleId(mapper.getRoleId());
            familyMemberVO.setRole(role.getRoleName());
            voList.add(familyMemberVO);
        }
        return Result.succeed("获取家庭组成员信息成功！", voList);
    }

    @Override
    public Result removePeople(Long userId) {
        Long oldFamilyId = familyMapper.getFamilyIdByUserId(userId);

        permissionMapper.deleteUserAllPermisson(userId);
        int affectNum = familyMapper.removePeople(userId);
        //删除用户在当前家庭组的账单信息
        transactionMapper.deleteUserFamilyBill(userId);

        //给用户发通知
        UserNotifications userNotifications = new UserNotifications();
        userNotifications.setUserId(userId);
        userNotifications.setMessage("很抱歉，您已被移出原家庭组!");
        userNotifications.setType(2);
        notifyMapper.addNotify(userNotifications);

        //给家庭组其他人发通知
        List<FamilyMember> familyMemberInfo = familyMapper.getFamilyMemberInfo(oldFamilyId);
        User userInfo = userMapper.getUserInfById(userId);
        if (familyMemberInfo != null && !familyMemberInfo.isEmpty()) {
            for (FamilyMember familyMember : familyMemberInfo) {
                if (familyMember.getUserId() != userId) {
                    UserNotifications newUserNotifications = new UserNotifications();
                    newUserNotifications.setUserId(familyMember.getUserId());
                    newUserNotifications.setType(2);
                    newUserNotifications.setMessage("用户：" + userInfo.getUsername() + "退出了您的家庭组!");
                    notifyMapper.addNotify(newUserNotifications);
                }
            }
        }
        if (affectNum > 0) {
            return Result.succeed("移除用户成功！");
        }
        return Result.fail("移除用户失败！请稍后重试");
    }

    @Override
    public Result updateProfile(Long familyId, String familyName, String familyCode) {
        familyMapper.updateProfile(familyId, familyName, familyCode);
        return Result.succeed("修改家庭组信息成功!");
    }

    @Override
    public Result getTotalBudget(Long familyId, int year, int month) {
        Double result = familyMapper.getTotalBudget(familyId, year, month);
        return Result.succeed("getTotalBudget执行成功!", result);
    }

    @Override
    public Result addBudget(Long familyId, int year, int month, double budget) {
        familyMapper.addBudget(familyId, year, month, budget);
        return Result.succeed("addBudget执行成功!");
    }

    @Override
    public Result updateBudget(Long familyId, int year, int month, double budget) {
        familyMapper.updateBudget(familyId, year, month, budget);
        return Result.succeed("updateBudget执行成功!");
    }

    @Override
    public void checkAndRemindBudget() {
        int currentMonth = DateUtils.getCurrentMonth();
        int currentYear = DateUtils.getCurrentYear();
        LocalDate firstDay = LocalDate.of(currentYear, currentMonth, 1);
        LocalDate firstDayNextMonth = firstDay.plusMonths(1);
        // 转换为 LocalDateTime（设置为当天0点）
        LocalDateTime startDateTime = firstDay.atStartOfDay();
        LocalDateTime endDateTime = firstDayNextMonth.atStartOfDay();
        List<FamilyBudget> budgetList = familyMapper.getAllNotNotifyFamilyId(currentYear, currentMonth);
        for (FamilyBudget familyBudget : budgetList) {
            double totalExpense = familyMapper.getCurrentMonthFamilyTotalExpense(familyBudget.getFamilyId(), startDateTime, endDateTime);
            if (totalExpense > familyBudget.getBudgetAmount()) {
                List<FamilyMember> familyMemberList = familyMapper.getFamilyMemberInfo(familyBudget.getFamilyId());
                for (FamilyMember familyMember : familyMemberList) {
                    //发送给所有具有该 权限的人 通知
                    if (permissionMapper.checkUserHasPermission(familyMember.getUserId(), PermissionCode.EDIT_FAMILY_BUDGET.getCode()) > 0) {
                        UserNotifications newUserNotifications = new UserNotifications();
                        newUserNotifications.setUserId(familyMember.getUserId());
                        newUserNotifications.setType(5);
                        newUserNotifications.setMessage("您家庭组本月支出已经超出所设置的本月预算，请注意！");
                        notifyMapper.addNotify(newUserNotifications);
                    }
                }
                familyMapper.updateBudgetIsNotified(familyBudget.getId());
            }
        }


    }

    @Override
    public Result getCurMonthTotalExpense(Long familyId) {
        int currentMonth = DateUtils.getCurrentMonth();
        int currentYear = DateUtils.getCurrentYear();
        LocalDate firstDay = LocalDate.of(currentYear, currentMonth, 1);
        LocalDate firstDayNextMonth = firstDay.plusMonths(1);
        // 转换为 LocalDateTime（设置为当天0点）
        LocalDateTime startDateTime = firstDay.atStartOfDay();
        LocalDateTime endDateTime = firstDayNextMonth.atStartOfDay();

        double totalExpense = familyMapper.getCurrentMonthFamilyTotalExpense(familyId, startDateTime, endDateTime);

        return Result.succeed("getCurMonthTotalExpense方法执行成功", totalExpense);
    }

    @Override
    public Result calculateDimensionScores(Long familyId) {
        List<FinancialHealthDimensionScore> result = new ArrayList<>();

        int currentMonth = DateUtils.getCurrentMonth();
        int currentYear = DateUtils.getCurrentYear();
        LocalDate firstDay = LocalDate.of(currentYear, currentMonth, 1);
        LocalDate firstDayNextMonth = firstDay.plusMonths(1);
        // 转换为 LocalDateTime（设置为当天0点）
        LocalDateTime startDateTime = firstDay.atStartOfDay();
        LocalDateTime endDateTime = firstDayNextMonth.atStartOfDay();


        Map<String, String> adviceMap = new HashMap<>();
        result.add(new FinancialHealthDimensionScore("收支平衡", calcBalanceScore(familyId, startDateTime, endDateTime, adviceMap), adviceMap.get("收支平衡")));
        result.add(new FinancialHealthDimensionScore("非必要支出占比", calcUnnecessaryExpenseScore(familyId, startDateTime, endDateTime, adviceMap), adviceMap.get("非必要支出占比")));
        result.add(new FinancialHealthDimensionScore("预算执行情况", calcBudgetExecutionScore(familyId, startDateTime, endDateTime, adviceMap), adviceMap.get("预算执行情况")));
//        result.add(new FinancialHealthDimensionScore("储蓄占比", calcSavingsScore(familyId, adviceMap), adviceMap.get("储蓄占比")));
        result.add(new FinancialHealthDimensionScore("负债情况", calcLiabilitiesScore(familyId, startDateTime, endDateTime, adviceMap), adviceMap.get("负债情况")));

        return Result.succeed("calculateDimensionScores执行成功!", result);
    }

    //第一维度 计算收支平衡
    private double calcBalanceScore(Long familyId, LocalDateTime startDateTime, LocalDateTime endDateTime, Map<String, String> adviceMap) {
        //获取 本月总收入
        BigDecimal income = transactionMapper.getFamilyMonthlyTotalAmountByType(familyId, "income", startDateTime, endDateTime);
        //获取 本月总支出
        BigDecimal expense = transactionMapper.getFamilyMonthlyTotalAmountByType(familyId, "expense", startDateTime, endDateTime);
        if (expense == null || expense.compareTo(BigDecimal.ZERO) == 0) {
            adviceMap.put("收支平衡", "本月暂未有支出。请继续保持，合理消费！");
            return 100;
        }
        if (income == null || income.compareTo(BigDecimal.ZERO) == 0) {
            adviceMap.put("收支平衡", "本月暂未有收入。建议拓展收入来源，合理进行储蓄或投资!");
            return 0;
        }

        double ratio = expense.divide(income, 2, RoundingMode.HALF_UP).doubleValue();
        double score = 100 - Math.min(ratio * 100, 100);

        if (score >= 90) {
            adviceMap.put("收支平衡", "收支状况非常健康，收入远高于支出，建议合理规划剩余资金进行储蓄或投资。");
        } else if (score >= 70) {
            adviceMap.put("收支平衡", "本月收入略高于支出，整体收支良好，建议继续维持良好的消费习惯。");
        } else if (score >= 40) {
            adviceMap.put("收支平衡", "支出接近或超过收入，建议分析支出结构，控制不必要支出。");
        } else {
            adviceMap.put("收支平衡", "本月支出远高于收入，建议及时调整消费行为，避免产生债务风险。");
        }

        return score;
    }

    //第二维度：计算非必要支出的得分
    private double calcUnnecessaryExpenseScore(Long familyId, LocalDateTime startDateTime, LocalDateTime endDateTime, Map<String, String> adviceMap) {
        //获取 本月 总支出
        BigDecimal totalExpense = transactionMapper.getFamilyMonthlyTotalAmountByType(familyId, "expense", startDateTime, endDateTime);
        //获取 非必要 类型的支出
        BigDecimal unnecessary = transactionMapper.sumUnnecessaryExpense(familyId, startDateTime, endDateTime); // 假设某类 category 属于非必要开销

        if (totalExpense == null || totalExpense.compareTo(BigDecimal.ZERO) == 0) {
            adviceMap.put("非必要支出占比", "本月暂未有支出。继续保持，合理消费！");
            return 100;
        }
        if (unnecessary == null || unnecessary.compareTo(BigDecimal.ZERO) == 0) {
            adviceMap.put("非必要支出占比", "本月暂未有非必要支出。继续保持，合理消费！");
            return 100;
        }
            //计算 支出 ÷ 收入 的比例，保留两位小数，四舍五入。
        double ratio = unnecessary.divide(totalExpense, 2, RoundingMode.HALF_UP).doubleValue();
        double score = 100 - Math.min(ratio * 100, 100);

        if (score >= 90) {
            adviceMap.put("非必要支出占比", "非必要支出占比极低，消费十分理性，建议继续保持精打细算的消费习惯。");
        } else if (score >= 70) {
            adviceMap.put("非必要支出占比", "非必要支出控制得较好，可适当犒劳自己，但仍需保持节制。");
        } else if (score >= 40) {
            adviceMap.put("非必要支出占比", "非必要支出占比较高，建议重新评估购物决策，避免冲动消费。");
        } else {
            adviceMap.put("非必要支出占比", "本月大部分支出为非必要消费，建议严格控制娱乐、餐饮等可选开支，避免财务压力。");
        }

        return score;
    }

    //    第三维度：预算执行情况
    private double calcBudgetExecutionScore(Long familyId, LocalDateTime startDateTime, LocalDateTime endDateTime, Map<String, String> adviceMap) {
        //本月家庭组预算
        BigDecimal budget = BigDecimal.valueOf(familyMapper.getTotalBudget(familyId, startDateTime.getMonthValue(), startDateTime.getYear()));
        //本月总支出
        BigDecimal expense = BigDecimal.valueOf(familyMapper.getCurrentMonthFamilyTotalExpense(familyId, startDateTime, endDateTime));

        if(expense==null|| budget.compareTo(BigDecimal.ZERO) == 0){
            adviceMap.put("预算执行情况", "本月暂未有支出。建议若未设置本月家庭组预算的话尽快设置！");
            return 100;
        }

        if (budget == null || budget.compareTo(BigDecimal.ZERO) == 0) {
            adviceMap.put("预算执行情况", "本月已有支出，但暂未设置本月家庭组预算。建议设置本月家庭组预算！");
            return 0;
        }
        // 支出 / 预算
        double ratio = expense.divide(budget, 2, RoundingMode.HALF_UP).doubleValue();
        // 超出预算的 每 1% 扣 0.4分
        double score = Math.max(ratio > 1 ? 60 - (ratio - 1) * 40 : 100, 0);

        if (score >= 90) {
            adviceMap.put("预算执行情况", "预算执行非常好，支出远低于预算，建议考虑将剩余预算投入储蓄或计划性消费。");
        } else if (score >= 70) {
            adviceMap.put("预算执行情况", "支出基本控制在预算范围内，预算使用合理，建议继续保持。");
        } else if (score >= 40) {
            adviceMap.put("预算执行情况", "支出已接近或略超预算，建议回顾本月各类支出项，适当调整预算或减少开销。");
        } else {
            adviceMap.put("预算执行情况", "预算严重超支，请及时调整消费行为，并优化下月预算计划。");
        }

        return Math.max(score, 0);
    }
//      第四维度： 计算 家庭组 储蓄能力 --从储蓄计划表获取数据（未新建）
//    private double calcSavingsScore(Long familyId, Map<String, String> adviceMap) {
//        //查询 所有 未完成 的 储蓄计划 -- 防止 已完成的计划影响 当前 的 储蓄能力反映
//        List<Plans> savings = familyMapper.getAllActivePlans(familyId, "income");
//
//        if (savings.isEmpty()) {
//            adviceMap.put("储蓄占比", "当前未设置家庭组储蓄计划，建议增加储蓄计划。");
//            return 0;
//        }
//        //获取 所有储蓄计划 当前金额 占 计划目标金额 比重
//        double totalTarget = savings.stream().mapToDouble(p -> p.getTargetAmount().doubleValue()).sum();
//        double currentTotal = savings.stream().mapToDouble(p -> p.getCurrentAmount().doubleValue()).sum();
//
//        double ratio = currentTotal / totalTarget;
//        double score = Math.min(ratio * 100, 100);
//
//        if (score < 60) {
//            adviceMap.put("储蓄占比", "储蓄进度偏低，建议增加储蓄计划或提高定期存款比例。");
//        }else{
//            adviceMap.put("储蓄占比", "当前储蓄进度良好，请继续保持！");
//        }
//
//        return score;
//    }

    //第五维度 ： 计算 负债情况
    private double calcLiabilitiesScore(Long familyId, LocalDateTime startDateTime, LocalDateTime endDateTime, Map<String, String> adviceMap) {
        //本月家庭组预算
        BigDecimal totalDebt = transactionMapper.getMonthlyLiabilities(familyId, startDateTime, endDateTime);
        //本月总收入
        BigDecimal income = BigDecimal.valueOf(familyMapper.getCurrentMonthFamilyTotalIncome(familyId, startDateTime, endDateTime));

        if(totalDebt==null){
            adviceMap.put("负债情况", "本月暂未有负债情况，请继续保持！。");
            return 100;
        }
        if (income == null || income.compareTo(BigDecimal.ZERO) == 0) {
            adviceMap.put("负债情况", "本月存在负债情况，但暂未有收入。建议增加收入来源。");
            return 0;
        }
//        ratio = totalDebt / income
        double ratio = totalDebt.divide(income, 2, RoundingMode.HALF_UP).doubleValue();
        double score = 100 - Math.min(ratio * 120, 100);

        if (score >= 90) {
            adviceMap.put("负债情况", "家庭当前负债情况非常健康，建议继续保持良好的财务习惯。");
        } else if (score >= 70) {
            adviceMap.put("负债情况", "负债占比尚可，建议控制非必要借贷支出。");
        } else if (score >= 40) {
            adviceMap.put("负债情况", "负债压力较大，建议减少信用卡透支或贷款，逐步降低负债。");
        } else {
            adviceMap.put("负债情况", "负债占收入比例过高，财务风险较大，建议制定还款计划，谨慎控制支出。");
        }

        return Math.max(score, 0);
    }
}
