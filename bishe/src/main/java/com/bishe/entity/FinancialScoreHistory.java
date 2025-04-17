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
 * @Date: 2025-04-14 15:58:47
 */

@Data
@TableName("financial_score_history")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value=" financial_score_history ", description=" 家庭财务健康评分月度汇总表 ")
public class FinancialScoreHistory {

  @ApiModelProperty(value = "主键ID")
  private long id;
 

  @ApiModelProperty(value = "家庭组ID")
  private long familyId;
 

  @ApiModelProperty(value = "评分月份，建议固定为当月第一天")
  private java.sql.Date month;
 

  @ApiModelProperty(value = "收支平衡评分")
  private double balanceScore;
 

  @ApiModelProperty(value = "非必要支出占比评分")
  private double unnecessaryExpenseScore;
 

  @ApiModelProperty(value = "预算执行情况评分")
  private double budgetExecutionScore;
 

  @ApiModelProperty(value = "负债情况评分")
  private double liabilityScore;
 

}
