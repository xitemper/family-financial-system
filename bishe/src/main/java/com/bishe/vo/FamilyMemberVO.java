package com.bishe.vo;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @Description 
 * @Author lzh
 * @Date: 2025-04-09 15:36:33
 */

@Data
@ApiModel(value=" FamilyMemberVO ", description=" FamilyMemberVO ")
@NoArgsConstructor
@AllArgsConstructor
public class FamilyMemberVO {

  @ApiModelProperty(value = "用户id")
  private Long userId;

  @ApiModelProperty(value = "用户名称")
  private String userName;

  @ApiModelProperty(value = "角色ID（外键，关联family_role表）")
  private String role;

}
