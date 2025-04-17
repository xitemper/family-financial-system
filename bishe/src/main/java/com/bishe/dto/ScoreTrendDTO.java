package com.bishe.dto;

import com.bishe.entity.FinancialScoreHistory;
import lombok.Data;

@Data
public class ScoreTrendDTO {
    private String monthStr;
    private Double balanceScore;
    private Double unnecessaryExpenseScore;
    private Double budgetExecutionScore;
    private Double liabilityScore;

    public ScoreTrendDTO(){

    }

    // 构造器
    public ScoreTrendDTO(String monthStr, FinancialScoreHistory s) {
        this.monthStr = monthStr;
        this.balanceScore = s.getBalanceScore();
        this.unnecessaryExpenseScore = s.getUnnecessaryExpenseScore();
        this.budgetExecutionScore = s.getBudgetExecutionScore();
        this.liabilityScore = s.getLiabilityScore();
    }

    // getters/setters
}
