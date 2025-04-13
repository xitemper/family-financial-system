package com.bishe.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FinancialHealthDimensionScore {
    private String dimension;     // 维度名称，如“收支平衡”
    private double score;         // 分数（0-100）
    private String advice;        // 建议或评价
}