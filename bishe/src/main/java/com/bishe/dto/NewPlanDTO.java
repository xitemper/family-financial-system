package com.bishe.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data // 自动生成 getter、setter、toString、equals 和 hashCode
@NoArgsConstructor // 生成无参构造
@AllArgsConstructor // 生成全参构造
@Builder // 提供 Builder 模式
public class NewPlanDTO {

    @ApiModelProperty(value = "计划名称")
    private String name;

    @ApiModelProperty(value = "计划类型")
    private String type;

    @ApiModelProperty(value = "目标金额")
    private double targetAmount;

    @ApiModelProperty(value = "当前金额")
    private String currentAmount;

    @ApiModelProperty(value = "目标截至时间")
    private LocalDateTime targetDate;



}
