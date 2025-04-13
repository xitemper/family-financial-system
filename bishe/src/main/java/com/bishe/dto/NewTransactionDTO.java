package com.bishe.dto;

import com.sun.org.apache.xpath.internal.operations.Bool;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data // 自动生成 getter、setter、toString、equals 和 hashCode
@NoArgsConstructor // 生成无参构造
@AllArgsConstructor // 生成全参构造
@Builder // 提供 Builder 模式
public class NewTransactionDTO {

    @ApiModelProperty(value = "账单类型")
    private String type;

    @ApiModelProperty(value = "账单分类")
    private String category;

    @ApiModelProperty(value = "金额")
    private double amount;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "用户注册时间")
    private LocalDateTime transactionDate;

    @ApiModelProperty(value = "是否家庭组账单")
    private int isFamilyBill;

    @ApiModelProperty("关联的计划id，为空则忽略")
    private Long planId;
}
