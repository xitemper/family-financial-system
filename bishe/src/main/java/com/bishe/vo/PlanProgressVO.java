package com.bishe.vo;

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
public class PlanProgressVO {

    @ApiModelProperty(value = "计划名称")
    private double targetAmount;

    @ApiModelProperty(value = "计划类型")
    private double currentAmount;

    @ApiModelProperty(value = "进度百分比")
    private int progress;



}
