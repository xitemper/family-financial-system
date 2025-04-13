package com.bishe.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@ApiModel(value=" AllPermissionVO ", description=" 账单VO ")
@NoArgsConstructor
@AllArgsConstructor
public class AllPermissionVO {
    @ApiModelProperty(value = "权限标识，如 VEW_OTHERS_RECORDS")
    private String code;

    @ApiModelProperty(value = "权限名称，用于展示")
    private String name;
}
