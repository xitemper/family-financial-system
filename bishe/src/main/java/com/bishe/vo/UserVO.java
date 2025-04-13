package com.bishe.vo;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@ApiModel(value=" UserVo ", description=" 用户VO ")
@NoArgsConstructor
@AllArgsConstructor
public class UserVO {

    @ApiModelProperty(value = "用户唯一ID")
    private long id;

    @ApiModelProperty(value = "用户名")
    private String username;
}
