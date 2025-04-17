package com.bishe.service.serviceImpl;

import com.bishe.chat.IntentClient;
import com.bishe.dto.CategoryMonthlyDataDTO;
import com.bishe.dto.NewTransactionDTO;
import com.bishe.dto.TransactionSummary;
import com.bishe.entity.*;
import com.bishe.mapper.FamilyMapper;
import com.bishe.mapper.TransactionMapper;
import com.bishe.mapper.UserMapper;
import com.bishe.service.TransactionService;
import com.bishe.vo.CategoryRankVO;
import com.bishe.vo.TransactionVO;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionMapper transacitionMapper;

    @Autowired
    private FamilyMapper familyMapper;

    @Autowired
    private UserMapper userMapper;

    private IntentClient intentClient = new IntentClient();

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
                transactionVO.setTransactionId(transaction.getTransactionId());
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
                transactionVO.setTransactionId(transaction.getTransactionId());
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
            Plans plan = transacitionMapper.getPlanByPlanId(newTransactionDTO.getPlanId());
            transacitionMapper.addPlanTransaction(transaction.getTransactionId(),newTransactionDTO.getPlanId());
            //更新对应计划的当前金额
            transacitionMapper.updatePlanCurrentAmount(newTransactionDTO.getPlanId(),plan.getCurrentAmount()+transaction.getAmount());
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
                transactionVO.setTransactionId(transaction.getTransactionId());
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
                transactionVO.setTransactionId(transaction.getTransactionId());
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
    public Result updateTransaction(TransactionVO transactionVO) {
        //修改 用户计划 相关计划数据
        PlanTransaction planTransaction =  transacitionMapper.getPlanTransactionByTransactionId(transactionVO.getTransactionId());
        if(planTransaction!=null){
            Transaction oldTransaction =transacitionMapper.getTransactionById(transactionVO.getTransactionId());
            Plans plan = transacitionMapper.getPlanByPlanId(planTransaction.getPlanId());

            plan.setCurrentAmount(plan.getCurrentAmount()-oldTransaction.getAmount()+transactionVO.getAmount());
            transacitionMapper.updatePlanCurrentAmount(plan.getId(),plan.getCurrentAmount());
        }
       //再更新账单信息
        transacitionMapper.updateTransaction(transactionVO);
        return Result.succeed("updateTransaction执行成功");
    }

    @Override
    public Result deleteTransaction(Long transactionId) {
        //修改 用户计划 相关计划数据
        PlanTransaction planTransaction =  transacitionMapper.getPlanTransactionByTransactionId(transactionId);
        if(planTransaction!=null){
            Transaction oldTransaction =transacitionMapper.getTransactionById(transactionId);
            Plans plan = transacitionMapper.getPlanByPlanId(planTransaction.getPlanId());
            plan.setCurrentAmount(plan.getCurrentAmount()-oldTransaction.getAmount());
            transacitionMapper.updatePlanCurrentAmount(plan.getId(),plan.getCurrentAmount());
        }
        transacitionMapper.deleteTransaction(transactionId);
        return Result.succeed("deleteTransaction执行成功1");
    }

    @Override
    public Result uploadAndParse(MultipartFile file, Long userId) throws IOException {
        System.out.println("--------------用户上传账单解析功能(CSV)-------");

        List<String> incomeCategories = Arrays.asList("工资", "奖金", "投资收益", "红包", "租金", "分红", "其他", "收款");
        List<String> expenseCategories = Arrays.asList("餐饮", "交通", "购物", "日用", "蔬菜", "水果", "零食", "运动", "娱乐", "通讯",
                "房租", "烟酒", "医疗", "学习", "礼品", "维修", "快递", "还款", "游戏", "其他");

        BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
        String line;
        int lineNum = 0;

        Map<String, Integer> headerIndexMap = new HashMap<>();

        while ((line = reader.readLine()) != null) {
            String[] columns = line.split(",");

            if (lineNum == 16) {
                // 第17行是表头
                for (int i = 0; i < columns.length; i++) {
                    headerIndexMap.put(columns[i].trim(), i);
                }
            }

            if (lineNum >= 17) {
                // 处理数据行
                String product = columns[headerIndexMap.get("商品")].trim();
                String typeStr = columns[headerIndexMap.get("收/支")].trim();
                String amountStr = columns[headerIndexMap.get("金额(元)")].trim();
                String categoryStr = columns[headerIndexMap.get("交易类型")].trim();

                if (amountStr.isEmpty()) {continue;}
                // 清理掉货币符号、逗号等非数字字符，只保留数字和小数点
                amountStr = amountStr.replaceAll("[^\\d.]", "");
                Transaction transaction = new Transaction();
                transaction.setUserId(userId);
                transaction.setTransactionDate(LocalDateTime.now());
                transaction.setAmount(Double.parseDouble(amountStr));
                transaction.setDescription(product);
                transaction.setIsFamilyBill(0);
                transaction.setIsAutoGenerated(1);
                transaction.setFileId(0L);

                Long familyId = familyMapper.getFamilyIdByUserId(userId);
                transaction.setFamilyId(familyId == null ? 0L : familyId);
                transaction.setFamilyName(familyId == null ? "无" : familyMapper.getFamilyInfoById(familyId).getFamilyName());

                if (typeStr.equals("收入")) {
                    transaction.setType("income");
                    String category = intentClient.classify(categoryStr, incomeCategories.toArray(new String[0]));
                    transaction.setCategory(category);
                } else {
                    transaction.setType("expense");
                    String category = intentClient.classify(categoryStr, expenseCategories.toArray(new String[0]));
                    transaction.setCategory(category);
                }

                transacitionMapper.addTransaction(userId, transaction);
            }

            lineNum++;
        }

        reader.close();
        return Result.succeed("CSV解析成功");
    }

    @Override
    public Result familyUploadAndParse(MultipartFile file, Long userId, Long familyId) throws IOException {
        System.out.println("--------------家庭组上传账单解析功能(CSV)-------");

        List<String> incomeCategories = Arrays.asList("工资", "奖金", "投资收益", "红包", "租金", "分红", "其他", "收款");
        List<String> expenseCategories = Arrays.asList("餐饮", "交通", "购物", "日用", "蔬菜", "水果", "零食", "运动", "娱乐", "通讯",
                "房租", "烟酒", "医疗", "学习", "礼品", "维修", "快递", "还款", "游戏", "其他");

        BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
        String line;
        int lineNum = 0;

        Map<String, Integer> headerIndexMap = new HashMap<>();

        while ((line = reader.readLine()) != null) {
            String[] columns = line.split(",");

            if (lineNum == 16) {
                // 第17行是表头
                for (int i = 0; i < columns.length; i++) {
                    headerIndexMap.put(columns[i].trim(), i);
                }
            }

            if (lineNum >= 17) {
                // 处理数据行
                String product = columns[headerIndexMap.get("商品")].trim();
                String typeStr = columns[headerIndexMap.get("收/支")].trim();
                String amountStr = columns[headerIndexMap.get("金额(元)")].trim();
                String categoryStr = columns[headerIndexMap.get("交易类型")].trim();

                if (amountStr.isEmpty()) {continue;}
                // 清理掉货币符号、逗号等非数字字符，只保留数字和小数点
                amountStr = amountStr.replaceAll("[^\\d.]", "");
                Transaction transaction = new Transaction();
                transaction.setUserId(userId);
                transaction.setTransactionDate(LocalDateTime.now());
                transaction.setAmount(Double.parseDouble(amountStr));
                transaction.setDescription(product);
                transaction.setIsFamilyBill(1);
                transaction.setIsAutoGenerated(1);
                transaction.setFileId(0L);

                transaction.setFamilyId(familyId);
                transaction.setFamilyName(familyId == null ? "无" : familyMapper.getFamilyInfoById(familyId).getFamilyName());

                if (typeStr.equals("收入")) {
                    transaction.setType("income");
                    String category = intentClient.classify(categoryStr, incomeCategories.toArray(new String[0]));
                    transaction.setCategory(category);
                } else {
                    transaction.setType("expense");
                    String category = intentClient.classify(categoryStr, expenseCategories.toArray(new String[0]));
                    transaction.setCategory(category);
                }
                transacitionMapper.addTransaction(userId, transaction);
            }
            lineNum++;
        }

        reader.close();
        return Result.succeed("CSV解析成功");
    }

    @Override
    public List<TransactionSummary> getFamilyTransactionSummaryByMonth(Long familyId, String type, int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        return transacitionMapper.findFamilyTransactionSummaryByMonth(familyId, type,startDate.atStartOfDay(), endDate.atStartOfDay());
    }

    @Override
    public List<CategoryMonthlyDataDTO> getFamilyMonthlyCategoryProportion(Long familyId, String type, int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        return transacitionMapper.getFamilyMonthlyCategoryProportion(familyId, type,startDate.atStartOfDay(), endDate.atStartOfDay());

    }

    @Override
    public CategoryRankVO getMonthlyCategoryRank(Long userId, String type, int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        List<CategoryMonthlyDataDTO> list = transacitionMapper.getMonthlyCategoryProportion(userId, type, startDate.atStartOfDay(), endDate.atStartOfDay());

        List<String> categoryName = list.stream().map(CategoryMonthlyDataDTO::getCategory).collect(Collectors.toList());
        List<Double> amounts = list.stream().map(CategoryMonthlyDataDTO::getAmount).collect(Collectors.toList());

        CategoryRankVO categoryRankVO = new CategoryRankVO();
        categoryRankVO.setCategoryName(categoryName);
        categoryRankVO.setCategoryData(amounts);
        return categoryRankVO;


    }

    @Override
    public CategoryRankVO getFamilyMonthlyCategoryRank(Long familyId, String type, int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        List<CategoryMonthlyDataDTO> list = transacitionMapper.getFamilyMonthlyCategoryProportion(familyId, type, startDate.atStartOfDay(), endDate.atStartOfDay());

        List<String> categoryName = list.stream().map(CategoryMonthlyDataDTO::getCategory).collect(Collectors.toList());
        List<Double> amounts = list.stream().map(CategoryMonthlyDataDTO::getAmount).collect(Collectors.toList());

        CategoryRankVO categoryRankVO = new CategoryRankVO();
        categoryRankVO.setCategoryName(categoryName);
        categoryRankVO.setCategoryData(amounts);
        return categoryRankVO;


    }

    //传入一行参数，和想读取的列号
    private String getCellString(Row row, Integer colIdx) {
        //没有表头列，返回空字符串
        if (colIdx == null) {return "";}
        Cell cell = row.getCell(colIdx);
        //没有内容，返回空字符串
        if (cell == null) {return "";}
        //强转成字符串
        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue().trim();
    }
}
