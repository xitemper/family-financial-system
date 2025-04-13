package com.bishe.entity;
import lombok.Data;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import io.swagger.annotations.ApiModel;

import java.time.LocalDateTime;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @Description 
 * @Author lzh
 * @Date: 2025-03-01 16:57:23
 */

@Data
@TableName("vercode_record")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value=" vercode_record ", description=" 用户验证码 ")
public class VercodeRecord {

  @ApiModelProperty(value = "验证码id")
  private long id;

  @ApiModelProperty(value = "手机号")
  private String phone;

  @ApiModelProperty(value = "验证码内容")
  private String content;
 

  @ApiModelProperty(value = "验证码类型：1、登录，2、注册")
  private int type;
 

  @ApiModelProperty(value = "是否被使用")
  private long isused;
 

  @ApiModelProperty(value = "创建时间")
  private LocalDateTime createdAt;
 

  @ApiModelProperty(value = "null")
  private LocalDateTime expiredAt;
 

}
