package com.bishe.service;

import com.bishe.dto.CategoryMonthlyDataDTO;
import com.bishe.dto.NewTransactionDTO;
import com.bishe.dto.TransactionSummary;
import com.bishe.entity.Result;
import com.bishe.vo.CategoryRankVO;
import com.bishe.vo.TransactionVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    Result updateTransaction(TransactionVO transactionVO);

    Result deleteTransaction(Long transactionId);

    Result uploadAndParse(MultipartFile file, Long userId) throws IOException;

    List<TransactionSummary> getFamilyTransactionSummaryByMonth(Long familyId, String type, int year, int month);

    List<CategoryMonthlyDataDTO> getFamilyMonthlyCategoryProportion(Long familyId, String type, int year, int month);

    CategoryRankVO getMonthlyCategoryRank(Long userId, String type, int year, int month);

    CategoryRankVO getFamilyMonthlyCategoryRank(Long familyId, String type, int year, int month);

    Result familyUploadAndParse(MultipartFile file,Long userId, Long familyId) throws IOException;
}
