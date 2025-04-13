package com.bishe.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@ApiModel(value=" TransactionVO ", description=" 账单VO ")
@NoArgsConstructor
@AllArgsConstructor
public class TransactionVO {

    @ApiModelProperty(value = "用户名称")
    private String username;

    @ApiModelProperty(value = "账单分类")
    private String category;

    @ApiModelProperty(value = "金额")
    private double amount;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "时间")
    private LocalDateTime time;

    @ApiModelProperty(value = "是否家庭组账单")
    private int isFamilyBill;

}

