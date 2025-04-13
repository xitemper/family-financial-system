package com.bishe.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // 自动生成 getter、setter、toString、equals 和 hashCode
@NoArgsConstructor // 生成无参构造
@AllArgsConstructor // 生成全参构造
@Builder // 提供 Builder 模式
public class CategoryMonthlyDataDTO {

    @ApiModelProperty(value = "账单分类")
    private String category;

    @ApiModelProperty(value = "金额")
    private double amount;
}
