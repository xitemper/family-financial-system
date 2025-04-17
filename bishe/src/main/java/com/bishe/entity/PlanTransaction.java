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
 * @Date: 2025-04-14 18:22:01
 */

@Data
@TableName("plan_transaction")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value=" plan_transaction ", description=" 储蓄/理财计划 与 账单 关联表 ")
public class PlanTransaction {

  @ApiModelProperty(value = "收支记录ID")
  private long transactionId;
 

  @ApiModelProperty(value = "账单id")
  private long planId;
 

}
