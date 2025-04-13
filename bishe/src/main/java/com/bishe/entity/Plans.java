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
 * @Date: 2025-04-12 13:32:12
 */

@Data
@TableName("plans")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value=" plans ", description=" null ")
public class Plans {

  @ApiModelProperty(value = "主键id")
  private long id;
 

  @ApiModelProperty(value = "用户id")
  private long userId;
 

  @ApiModelProperty(value = "计划名称")
  private String name;
 

  @ApiModelProperty(value = "计划类型")
  private String type;
 

  @ApiModelProperty(value = "目标金额")
  private double targetAmount;
 

  @ApiModelProperty(value = "当前金额")
  private double currentAmount;
 

  @ApiModelProperty(value = "目标截至日期")
  private java.sql.Date targetDate;
 

  @ApiModelProperty(value = "1-进行中;2-已完成")
  private long status;
 

}
