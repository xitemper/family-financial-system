package com.bishe.entity;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
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
 * @Date: 2025-02-06 22:18:32
 */

@Data
@TableName("user")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value=" user ", description=" 用户表 ")
@NoArgsConstructor
@AllArgsConstructor
public class User {

  @ApiModelProperty(value = "用户唯一ID")
  private long userId;
 

  @ApiModelProperty(value = "用户手机号（登录账号）")
  private String phone;
 

  @ApiModelProperty(value = "用户密码（BCrypt加密存储）")
  private String password;
 

  @ApiModelProperty(value = "用户名")
  private String username;
 

  @ApiModelProperty(value = "用户邮箱（未来扩展）")
  private String email;
 

  @ApiModelProperty(value = "用户状态（0-禁用，1-正常，2-未激活）")
  private long status;
 

  @ApiModelProperty(value = "最后登录时间")
  private LocalDateTime lastLogin;
 

  @ApiModelProperty(value = "用户注册时间")
  private LocalDateTime createdAt;
 

  @ApiModelProperty(value = "用户信息更新时间")
  private LocalDateTime updatedAt;
 

}
