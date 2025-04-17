package com.bishe.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@ApiModel(value=" CategoryRankVO ", description=" 分类数据排行VO ")
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRankVO {

    @ApiModelProperty(value="账单id")
    private List<String> categoryName;

    @ApiModelProperty(value = "用户名称")
    private List<Double> categoryData;


}

