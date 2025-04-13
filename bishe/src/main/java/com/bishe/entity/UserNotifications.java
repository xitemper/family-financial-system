package com.bishe.entity;
import lombok.*;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.experimental.Accessors;
import io.swagger.annotations.ApiModel;
import java.util.Date;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @Description 
 * @Author lzh
 * @Date: 2025-04-10 22:38:33
 */

@Data
@TableName("user_notifications")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value=" user_notifications ", description=" 用户消息提醒表 ")
public class UserNotifications {

  @ApiModelProperty(value = "提醒表主键id")
  private long id;
 

  @ApiModelProperty(value = "用户id")
  private long userId;

  @ApiModelProperty(value = "提醒类型： 1-通用信息;2-家庭组信息;3-还款计划提醒;4-理财计划提醒;5-预算提醒")
  private long type;

  @ApiModelProperty(value = "提醒消息内容")
  private String message;
 

  @ApiModelProperty(value = "是否被用户查看")
  private long isRead;
 

  @ApiModelProperty(value = "创建时间")
  private LocalDateTime createAt;
 

}
