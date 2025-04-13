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
 * @Date: 2025-04-09 15:47:30
 */

@Data
@TableName("family_role")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value=" family_role ", description=" 家庭成员角色表 ")
public class FamilyRole {

  @ApiModelProperty(value = "角色ID（预定义角色：1-创建者，2-管理员，3-成员）")
  private long roleId;
 

  @ApiModelProperty(value = "角色名称（如创建者、财务管理员）")
  private String roleName;
 

  @ApiModelProperty(value = "角色描述")
  private String description;
 

}
