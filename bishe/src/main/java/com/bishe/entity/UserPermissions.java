package com.bishe.entity;
import lombok.Data;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import io.swagger.annotations.ApiModel;
import java.util.Date;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @Description 
 * @Author lzh
 * @Date: 2025-04-10 19:47:57
 */

@Data
@TableName("user_permissions")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value=" user_permissions ", description=" 家庭组成员权限关联表 ")
public class UserPermissions {

  @ApiModelProperty(value = "主键ID")
  private long id;
 

  @ApiModelProperty(value = "家庭组ID")
  private long familyId;
 

  @ApiModelProperty(value = "用户ID")
  private long userId;
 

  @ApiModelProperty(value = "权限标识 code")
  private String permissionCode;
 

}
