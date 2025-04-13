package com.bishe.service.serviceImpl;

import com.bishe.dto.CategoryMonthlyDataDTO;
import com.bishe.dto.NewTransactionDTO;
import com.bishe.dto.TransactionSummary;
import com.bishe.entity.Family;
import com.bishe.entity.Result;
import com.bishe.entity.Transaction;
import com.bishe.entity.User;
import com.bishe.mapper.FamilyMapper;
import com.bishe.mapper.TransactionMapper;
import com.bishe.mapper.UserMapper;
import com.bishe.service.TransactionService;
import com.bishe.vo.TransactionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionMapper transacitionMapper;

    @Autowired
    private FamilyMapper familyMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public Result getIncomeRecord(Long userId,String startTime,String endTime) {
        List<Transaction> incomeRecordList = new ArrayList<>();
        if(startTime!=null&&!startTime.equals("")&&endTime!=null&&!endTime.equals("")){
            LocalDateTime startDate = LocalDateTime.parse(startTime, DateTimeFormatter.ISO_DATE_TIME);
            LocalDateTime endDate = LocalDateTime.parse(endTime, DateTimeFormatter.ISO_DATE_TIME);
            incomeRecordList = transacitionMapper.getTransactionsByTypeAndDate(userId,"income",startDate,endDate);
        }else{
            // 如果时间为空，默认查询当前月的开头和结尾
            LocalDateTime now = LocalDateTime.now();
            // 设置为当前月的第一天
            LocalDateTime startDate = now.withDayOfMonth(1).toLocalDate().atStartOfDay();
            // 设置为当前月的最后一天
            LocalDateTime endDate = now.withDayOfMonth(now.toLocalDate().lengthOfMonth()).toLocalDate().atTime(23, 59, 59, 999999);
            incomeRecordList = transacitionMapper.getTransactionsByTypeAndDate(userId, "income", startDate, endDate);
        }
        List<TransactionVO> incomeRecordVOList = new ArrayList<>();
        if(incomeRecordList!=null&&!incomeRecordList.isEmpty()){
            for (Transaction transaction : incomeRecordList) {
                TransactionVO transactionVO = new TransactionVO();
                User user = userMapper.getUserInfById(transaction.getUserId());
                transactionVO.setUsername(user.getUsername());
                transactionVO.setCategory(transaction.getCategory());
                transactionVO.setAmount(transaction.getAmount());
                transactionVO.setDescription(transaction.getDescription());
                transactionVO.setTime(transaction.getTransactionDate());
                transactionVO.setIsFamilyBill(transaction.getIsFamilyBill());
                incomeRecordVOList.add(transactionVO);
            }
        }
        return Result.succeed("getIncomeRecord方法读取数据成功",incomeRecordVOList);
    }

    @Override
    public Result getExpenseRecord(Long userId,String startTime,String endTime) {
        List<Transaction> expenseRecordList=new ArrayList<>();
        if(startTime!=null&&!startTime.equals("")&&endTime!=null&&!endTime.equals("")){
            LocalDateTime startDate = LocalDateTime.parse(startTime, DateTimeFormatter.ISO_DATE_TIME);
            LocalDateTime endDate = LocalDateTime.parse(endTime, DateTimeFormatter.ISO_DATE_TIME);
            expenseRecordList = transacitionMapper.getTransactionsByTypeAndDate(userId,"expense",startDate,endDate);
        }else{
            // 如果时间为空，默认查询当前月的开头和结尾
            LocalDateTime now = LocalDateTime.now();
            // 设置为当前月的第一天
            LocalDateTime startDate = now.withDayOfMonth(1).toLocalDate().atStartOfDay();
            // 设置为当前月的最后一天
            LocalDateTime endDate = now.withDayOfMonth(now.toLocalDate().lengthOfMonth()).toLocalDate().atTime(23, 59, 59, 999999);

            expenseRecordList = transacitionMapper.getTransactionsByTypeAndDate(userId, "expense", startDate, endDate);
        }
        List<TransactionVO> expenseRecordVOList = new ArrayList<>();
        if(expenseRecordList!=null&&!expenseRecordList.isEmpty()){
            for (Transaction transaction : expenseRecordList) {
                TransactionVO transactionVO = new TransactionVO();
                User user = userMapper.getUserInfById(transaction.getUserId());
                transactionVO.setUsername(user.getUsername());
                transactionVO.setCategory(transaction.getCategory());
                transactionVO.setAmount(transaction.getAmount());
                transactionVO.setDescription(transaction.getDescription());
                transactionVO.setTime(transaction.getTransactionDate());
                transactionVO.setIsFamilyBill(transaction.getIsFamilyBill());
                expenseRecordVOList.add(transactionVO);
            }
        }
        return Result.succeed("getExpenseRecord方法读取数据成功",expenseRecordVOList);
    }

    @Override
    public Result addTransaction(Long userId, NewTransactionDTO newTransactionDTO) {
        Transaction transaction = new Transaction();
        transaction.setUserId(userId);
        Long familyIdByUserId = familyMapper.getFamilyIdByUserId(userId);
        Family family = familyMapper.getFamilyInfoById(familyIdByUserId);
        transaction.setFamilyId(family.getFamilyId());
        transaction.setFamilyName(family.getFamilyName());
        transaction.setType(newTransactionDTO.getType());
        transaction.setCategory(newTransactionDTO.getCategory());
        transaction.setAmount(newTransactionDTO.getAmount());
        transaction.setDescription(newTransactionDTO.getDescription());
        transaction.setTransactionDate(newTransactionDTO.getTransactionDate());
        transaction.setIsFamilyBill(newTransactionDTO.getIsFamilyBill());
        int affectNum = transacitionMapper.addTransaction(userId,transaction);

        if(newTransactionDTO.getPlanId()!=null){
            transacitionMapper.addPlanTransaction(transaction.getTransactionId(),newTransactionDTO.getPlanId());
            //更新对应计划的当前金额
            transacitionMapper.updatePlanCurrentAmount(newTransactionDTO.getPlanId(),transaction.getAmount());
        }
        if(affectNum>0){
            return Result.succeed("添加账单成功");
        }else{
            return Result.fail("添加账单失败！");
        }
    }

    @Override
    public Result getTotalIncome(Long userId, String startTime, String endTime) {
        Double  result = new Double(0);
        if(startTime!=null&&!startTime.equals("")&&endTime!=null&&!endTime.equals("")){
            LocalDateTime startDate = LocalDateTime.parse(startTime, DateTimeFormatter.ISO_DATE_TIME);
            LocalDateTime endDate = LocalDateTime.parse(endTime, DateTimeFormatter.ISO_DATE_TIME);
            result = transacitionMapper.getTotalIncome(userId,"income",startDate,endDate);
        }else{
            // 如果时间为空，默认查询当前月的开头和结尾
            LocalDateTime now = LocalDateTime.now();
            // 设置为当前月的第一天
            LocalDateTime startDate = now.withDayOfMonth(1).toLocalDate().atStartOfDay();
            // 设置为当前月的最后一天
            LocalDateTime endDate = now.withDayOfMonth(now.toLocalDate().lengthOfMonth()).toLocalDate().atTime(23, 59, 59, 999999);
            result = transacitionMapper.getTotalIncome(userId, "income", startDate, endDate);
        }
        double res = result != null ? result : 0.0;
        return Result.succeed("totalIncome查询成功",res);
    }

    @Override
    public Result getTotalExpense(Long userId, String startTime, String endTime) {
        Double  result = new Double(0);
        if(startTime!=null&&!startTime.equals("")&&endTime!=null&&!endTime.equals("")){
            LocalDateTime startDate = LocalDateTime.parse(startTime, DateTimeFormatter.ISO_DATE_TIME);
            LocalDateTime endDate = LocalDateTime.parse(endTime, DateTimeFormatter.ISO_DATE_TIME);
            result = transacitionMapper.getTotalIncome(userId,"expense",startDate,endDate);
        }else{
            // 如果时间为空，默认查询当前月的开头和结尾
            LocalDateTime now = LocalDateTime.now();
            // 设置为当前月的第一天
            LocalDateTime startDate = now.withDayOfMonth(1).toLocalDate().atStartOfDay();
            // 设置为当前月的最后一天
            LocalDateTime endDate = now.withDayOfMonth(now.toLocalDate().lengthOfMonth()).toLocalDate().atTime(23, 59, 59, 999999);
            result = transacitionMapper.getTotalIncome(userId, "expense", startDate, endDate);
        }
        double res = result != null ? result : 0.0;
        return Result.succeed("totalIncome查询成功",res);
    }

    @Override
    public List<TransactionSummary> getTransactionSummaryByMonth(Long userId,String type ,int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        return transacitionMapper.findTransactionSummaryByMonth(userId, type,startDate.atStartOfDay(), endDate.atStartOfDay());
    }

    @Override
    public List<CategoryMonthlyDataDTO> getMonthlyCategoryProportion(Long userId, String type, int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        return transacitionMapper.getMonthlyCategoryProportion(userId, type,startDate.atStartOfDay(), endDate.atStartOfDay());
    }

    @Override
    public Result getFamilyIncomeRecord(Long familyId, String startTime, String endTime) {
        List<Transaction> incomeRecordList = new ArrayList<>();
        if(startTime!=null&&!startTime.equals("")&&endTime!=null&&!endTime.equals("")){
            LocalDateTime startDate = LocalDateTime.parse(startTime, DateTimeFormatter.ISO_DATE_TIME);
            LocalDateTime endDate = LocalDateTime.parse(endTime, DateTimeFormatter.ISO_DATE_TIME);
            incomeRecordList = transacitionMapper.getFamilyTransactionsByTypeAndDate(familyId,"income",startDate,endDate);
        }else{
            // 如果时间为空，默认查询当前月的开头和结尾
            LocalDateTime now = LocalDateTime.now();
            // 设置为当前月的第一天
            LocalDateTime startDate = now.withDayOfMonth(1).toLocalDate().atStartOfDay();
            // 设置为当前月的最后一天
            LocalDateTime endDate = now.withDayOfMonth(now.toLocalDate().lengthOfMonth()).toLocalDate().atTime(23, 59, 59, 999999);
            incomeRecordList = transacitionMapper.getFamilyTransactionsByTypeAndDate(familyId, "income", startDate, endDate);
        }
        List<TransactionVO> incomeRecordVOList = new ArrayList<>();
        if(incomeRecordList!=null&&!incomeRecordList.isEmpty()){
            for (Transaction transaction : incomeRecordList) {
                TransactionVO transactionVO = new TransactionVO();
                User user = userMapper.getUserInfById(transaction.getUserId());
                transactionVO.setUsername(user.getUsername());
                transactionVO.setCategory(transaction.getCategory());
                transactionVO.setAmount(transaction.getAmount());
                transactionVO.setDescription(transaction.getDescription());
                transactionVO.setTime(transaction.getTransactionDate());
                transactionVO.setIsFamilyBill(transaction.getIsFamilyBill());
                incomeRecordVOList.add(transactionVO);
            }
        }
        return Result.succeed("getIncomeRecord方法读取数据成功",incomeRecordVOList);
    }

    @Override
    public Result getFamilyExpenseRecord(Long familyId, String startTime, String endTime) {
        List<Transaction> expenseRecordList=new ArrayList<>();
        if(startTime!=null&&!startTime.equals("")&&endTime!=null&&!endTime.equals("")){
            LocalDateTime startDate = LocalDateTime.parse(startTime, DateTimeFormatter.ISO_DATE_TIME);
            LocalDateTime endDate = LocalDateTime.parse(endTime, DateTimeFormatter.ISO_DATE_TIME);
            expenseRecordList = transacitionMapper.getFamilyTransactionsByTypeAndDate(familyId,"expense",startDate,endDate);
        }else{
            // 如果时间为空，默认查询当前月的开头和结尾
            LocalDateTime now = LocalDateTime.now();
            // 设置为当前月的第一天
            LocalDateTime startDate = now.withDayOfMonth(1).toLocalDate().atStartOfDay();
            // 设置为当前月的最后一天
            LocalDateTime endDate = now.withDayOfMonth(now.toLocalDate().lengthOfMonth()).toLocalDate().atTime(23, 59, 59, 999999);

            expenseRecordList = transacitionMapper.getFamilyTransactionsByTypeAndDate(familyId, "expense", startDate, endDate);
        }
        List<TransactionVO> expenseRecordVOList = new ArrayList<>();
        if(expenseRecordList!=null&&!expenseRecordList.isEmpty()){
            for (Transaction transaction : expenseRecordList) {
                TransactionVO transactionVO = new TransactionVO();
                User user = userMapper.getUserInfById(transaction.getUserId());
                transactionVO.setUsername(user.getUsername());
                transactionVO.setCategory(transaction.getCategory());
                transactionVO.setAmount(transaction.getAmount());
                transactionVO.setDescription(transaction.getDescription());
                transactionVO.setTime(transaction.getTransactionDate());
                transactionVO.setIsFamilyBill(transaction.getIsFamilyBill());
                expenseRecordVOList.add(transactionVO);
            }
        }
        return Result.succeed("getExpenseRecord方法读取数据成功",expenseRecordVOList);
    }

}
