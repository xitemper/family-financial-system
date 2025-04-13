package com.bishe.controller;

import com.bishe.dto.CategoryMonthlyDataDTO;
import com.bishe.dto.NewTransactionDTO;
import com.bishe.dto.TransactionSummary;
import com.bishe.entity.Result;
import com.bishe.service.TransactionService;
import com.bishe.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Api(tags = "账单模块")
@RestController
@RequestMapping("/transaction")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @ApiOperation("获取收入记录")
    @GetMapping("/getIncomeRecord")
    public Result getIncomeRecord(@RequestParam("userId") Long userId,@RequestParam("startTime")String startTime,@RequestParam("endTime")String endTime){
        Result result = transactionService.getIncomeRecord(userId,startTime,endTime);
        return result;
    }

    @ApiOperation("获取支出记录")
    @GetMapping("/getExpenseRecord")
    public Result getExpenseRecord(@RequestParam("userId") Long userId,@RequestParam("startTime")String startTime,@RequestParam("endTime")String endTime){
        Result result = transactionService.getExpenseRecord(userId,startTime,endTime);
        return result;
    }

    @ApiOperation("获取家庭组收入记录")
    @GetMapping("/getFamilyIncomeRecord")
    public Result getFamilyIncomeRecord(@RequestParam("familyId") Long familyId,@RequestParam("startTime")String startTime,@RequestParam("endTime")String endTime){
        Result result = transactionService.getFamilyIncomeRecord(familyId,startTime,endTime);
        return result;
    }

    @ApiOperation("获取家庭组支出记录")
    @GetMapping("/getFamilyExpenseRecord")
    public Result getFamilyExpenseRecord(@RequestParam("familyId") Long familyId,@RequestParam("startTime")String startTime,@RequestParam("endTime")String endTime){
        Result result = transactionService.getFamilyExpenseRecord(familyId,startTime,endTime);
        return result;
    }

    @ApiOperation("新增账单信息")
    @PostMapping("/addTransaction")
    public Result addTransaction(@RequestParam("userId") Long userId, @RequestBody NewTransactionDTO newTransactionDTO){
        Result result = transactionService.addTransaction(userId,newTransactionDTO);
        return result;
    }

    @ApiOperation("获取收入总金额")
    @GetMapping("/getTotalIncome")
    public Result getTotalIncome(@RequestParam("userId") Long userId,@RequestParam("startTime")String startTime,@RequestParam("endTime")String endTime){
        Result result = transactionService.getTotalIncome(userId,startTime,endTime);
        return result;
    }

    @ApiOperation("获取收入总金额")
    @GetMapping("/getTotalExpense")
    public Result getTotalExpense(@RequestParam("userId") Long userId,@RequestParam("startTime")String startTime,@RequestParam("endTime")String endTime){
        Result result = transactionService.getTotalExpense(userId,startTime,endTime);
        return result;
    }

    @ApiOperation("根据年月获取该月每天的账单总额")
    @GetMapping("/monthlyIncome")
    public Result getMonthlyIncomeTransactions(
            @RequestParam Long userId,
            @RequestParam int year,
            @RequestParam int month
    ) {
        // 获取当月天数
        int daysInMonth = LocalDate.of(year, month, 1).lengthOfMonth();

        // 初始化固定长度的数组
        double[] amountArray = new double[daysInMonth]; // 默认全是 0
        List<String> labels = new ArrayList<>();

        // 生成日期 labels（01-31）
        for (int i = 1; i <= daysInMonth; i++) {
            labels.add(String.format("%02d", i)); // "01", "02", ..., "31"
        }

        // 查询数据库，按天 GROUP BY
        List<TransactionSummary> summaries = transactionService.getTransactionSummaryByMonth(userId,"income", year, month);

        // 填充数据到数组
        for (TransactionSummary summary : summaries) {
            int dayIndex = summary.getTransactionDate().getDayOfMonth() - 1;
            amountArray[dayIndex] = summary.getTotalAmount();
        }

        // 转换数组为 List<Double>
        List<Double> series = Arrays.stream(amountArray).boxed().collect(Collectors.toList());

        // 返回数据
        Map<String, Object> response = new HashMap<>();
        response.put("labels", labels);
        response.put("series", Collections.singletonList(series));

        return Result.succeed("获取当前月份数据成功",response);
    }

    @ApiOperation("根据年月获取该月每天的账单总额")
    @GetMapping("/monthlyExpense")
    public Result getMonthlyExpenseTransactions(
            @RequestParam Long userId,
            @RequestParam int year,
            @RequestParam int month
    ) {
        // 获取当月天数
        int daysInMonth = LocalDate.of(year, month, 1).lengthOfMonth();

        // 初始化固定长度的数组
        double[] amountArray = new double[daysInMonth]; // 默认全是 0
        List<String> labels = new ArrayList<>();

        // 生成日期 labels（01-31）
        for (int i = 1; i <= daysInMonth; i++) {
            labels.add(String.format("%02d", i)); // "01", "02", ..., "31"
        }

        // 查询数据库，按天 GROUP BY
        List<TransactionSummary> summaries = transactionService.getTransactionSummaryByMonth(userId,"expense", year, month);

        // 填充数据到数组
        for (TransactionSummary summary : summaries) {
            int dayIndex = summary.getTransactionDate().getDayOfMonth() - 1;
            amountArray[dayIndex] = summary.getTotalAmount();
        }

        // 转换数组为 List<Double>
        List<Double> series = Arrays.stream(amountArray).boxed().collect(Collectors.toList());

        // 返回数据
        Map<String, Object> response = new HashMap<>();
        response.put("labels", labels);
        response.put("series", Collections.singletonList(series));

        return Result.succeed("获取当前月份数据成功",response);
    }

    @ApiOperation("获取当月收支最大值")
    @GetMapping("/getMonthlyMaxValue")
    public Result getMaxValue(
            @RequestParam Long userId,
            @RequestParam String type,
            @RequestParam int year,
            @RequestParam int month
    ){
        List<TransactionSummary> summaries = transactionService.getTransactionSummaryByMonth(userId,type, year, month);
        double max = 0;
        for (TransactionSummary summary : summaries) {
            if(summary.getTotalAmount()>max){
                max = summary.getTotalAmount();
            }
        }
        if(max==0){
            max = 120;

        }
        return Result.succeed("获取当月收入或支出最大值成功",max);
    }


    @ApiOperation("根据年月获取该月每天的账单总额")
    @GetMapping("/getMonthlyCategoryProportion")
    public Result getMonthlyCategoryProportion(
            @RequestParam Long userId,
            @RequestParam String type,
            @RequestParam int year,
            @RequestParam int month
    ) {
        // 获取当月天数
        int daysInMonth = LocalDate.of(year, month, 1).lengthOfMonth();

        List<String> labels = new ArrayList<>();

        // 查询数据库，按天 GROUP BY
        List<CategoryMonthlyDataDTO> categoryList = transactionService.getMonthlyCategoryProportion(userId,type, year, month);

        System.out.println("categoryList: " + categoryList);

        // 计算金额总和
        double totalAmount = categoryList.stream()
                .mapToDouble(CategoryMonthlyDataDTO::getAmount)
                .sum();

        // 计算占比并转换为整数
        List<Integer> percentageList = categoryList.stream()
                .map(dto -> (int) Math.round((dto.getAmount() / totalAmount) * 100))
                .collect(Collectors.toList());

        // 处理四舍五入导致的误差
        percentageList = adjustPercentage(percentageList, 100);

        // 返回数据
        Map<String, Object> response = new HashMap<>();
        response.put("labels", percentageList.stream()
                .map(p -> p + "%")
                .collect(Collectors.toList()));
        response.put("series", Collections.singletonList(percentageList));
        response.put("categories",categoryList.stream().map(CategoryMonthlyDataDTO::getCategory).collect(Collectors.toList()));

        return Result.succeed("获取当前月份数据成功",response);
    }

    // 调整四舍五入误差，使总和恰好等于 targetSum
    private static List<Integer> adjustPercentage(List<Integer> percentages, int targetSum) {

        if (percentages == null || percentages.isEmpty()) {
            // 当没有数据时，直接返回空列表，或者根据业务需求返回默认值
            return new ArrayList<>();
        }

        int currentSum = percentages.stream().mapToInt(Integer::intValue).sum();
        int diff = targetSum - currentSum; // 计算误差

        // 如果有误差，调整最大值或最小值
        if (diff != 0) {
            for (int i = 0; i < Math.abs(diff); i++) {
                int index = (diff > 0) ? findMaxIndex(percentages) : findMinIndex(percentages);
                percentages.set(index, percentages.get(index) + (diff > 0 ? 1 : -1));
            }
        }
        return percentages;
    }

    // 找到最大值索引（误差正时调整最大值）
    private static int findMaxIndex(List<Integer> list) {
        return list.indexOf(Collections.max(list));
    }

    // 找到最小值索引（误差负时调整最小值）
    private static int findMinIndex(List<Integer> list) {
        return list.indexOf(Collections.min(list));
    }

}
