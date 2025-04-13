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
 * @Date: 2025-04-09 15:36:33
 */

@Data
@TableName("family_member")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value=" family_member ", description=" 家庭成员表 ")
public class FamilyMember {

  @ApiModelProperty(value = "家庭组ID（外键，关联family表）")
  private long familyId;
 

  @ApiModelProperty(value = "用户ID（外键，关联user表）")
  private long userId;
 

  @ApiModelProperty(value = "角色ID（外键，关联family_role表）")
  private long roleId;
 

  @ApiModelProperty(value = "加入家庭组时间")
  private LocalDateTime joinedAt;
 

}
