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
 * @Date: 2025-04-12 16:45:02
 */

@Data
@TableName("family_budget")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value=" family_budget ", description=" 家庭预算表 ")
public class FamilyBudget {

  @ApiModelProperty(value = "主键id")
  private long id;
 

  @ApiModelProperty(value = "家庭组id")
  private long familyId;
 

  @ApiModelProperty(value = "年")
  private long year;
 

  @ApiModelProperty(value = "月")
  private long month;
 

  @ApiModelProperty(value = "预算总额")
  private double budgetAmount;
 

  @ApiModelProperty(value = "当支出超出预算时是否已提醒用户,0-未提醒，1-已提醒")
  private long isNotified;
 

}
