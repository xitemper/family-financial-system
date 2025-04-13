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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @Description 
 * @Author lzh
 * @Date: 2025-03-09 23:38:08
 */

@Data
@TableName("family")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value=" family ", description=" 家庭组表 ")
public class Family {

  @ApiModelProperty(value = "家庭组唯一ID")
  private long familyId;
 

  @ApiModelProperty(value = "家庭组编号")
  private String familyCode;
 

  @ApiModelProperty(value = "家庭组名称")
  private String familyName;
 

  @ApiModelProperty(value = "创建者ID（外键，关联user表）")
  private long creatorId;
 

  @ApiModelProperty(value = "当前预算")
  private double currentBudget;
 

  @ApiModelProperty(value = "家庭组创建时间")
  private java.sql.Date createdAt;
 

  @ApiModelProperty(value = "家庭组信息更新时间")
  private java.sql.Date updatedAt;
 

}
