package com.bishe.entity;
import lombok.Data;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import io.swagger.annotations.ApiModel;

import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @Description 
 * @Author lzh
 * @Date: 2025-03-30 14:25:13
 */

@Data
@TableName("transaction")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value=" transaction ", description=" 收支记录表 ")
public class Transaction {

  @ApiModelProperty(value = "收支记录ID")
  private long transactionId;
 

  @ApiModelProperty(value = "用户ID（外键，关联user表）")
  private long userId;
 

  @ApiModelProperty(value = "家庭组ID（外键，关联family表）")
  private long familyId;
 

  @ApiModelProperty(value = "家庭组名称")
  private String familyName;
 

  @ApiModelProperty(value = "类型（income/expense）")
  private String type;
 

  @ApiModelProperty(value = "分类（如餐饮、交通、工资）")
  private String category;
 

  @ApiModelProperty(value = "金额")
  private double amount;
 

  @ApiModelProperty(value = "备注")
  private String description;
 

  @ApiModelProperty(value = "交易时间")
  private LocalDateTime transactionDate;

  @ApiModelProperty(value = "是否家庭组账单")
  private int isFamilyBill;
 

  @ApiModelProperty(value = "是否为系统自动生成（如账单解析）")
  private long isAutoGenerated;
 

  @ApiModelProperty(value = "上传文件ID（外键，关联uploaded_file表）")
  private long fileId;

  @Override
  public String toString() {
    return String.format(
            "【%s】金额: %.2f | 分类: %s | 备注: %s | 时间: %s | 家庭组: %s ",
            type.toUpperCase(),
            amount,
            category,
            description != null ? description : "无",
            transactionDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
            familyName != null ? familyName : "无",
            isFamilyBill == 1 ? "家庭账单" : "个人账单"
    );
  }

}
