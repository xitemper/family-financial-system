package com.bishe.service;

import com.bishe.dto.CategoryMonthlyDataDTO;
import com.bishe.dto.NewTransactionDTO;
import com.bishe.dto.TransactionSummary;
import com.bishe.entity.Result;

import java.util.List;

public interface TransactionService {


    Result getIncomeRecord(Long userId,String startTime,String endTime);

    Result getExpenseRecord(Long userId,String startTime,String endTime);

    Result addTransaction(Long userId, NewTransactionDTO newTransactionDTO);

    Result getTotalIncome(Long userId, String startTime, String endTime);

    Result getTotalExpense(Long userId, String startTime, String endTime);

    List<TransactionSummary> getTransactionSummaryByMonth(Long userId,String type, int year, int month);

    List<CategoryMonthlyDataDTO> getMonthlyCategoryProportion(Long userId, String type, int year, int month);

    Result getFamilyIncomeRecord(Long familyId, String startTime, String endTime);

    Result getFamilyExpenseRecord(Long familyId, String startTime, String endTime);
}
