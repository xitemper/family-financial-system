package com.bishe.entity;
import lombok.Data;
import io.swagger.annotations.ApiModelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import io.swagger.annotations.ApiModel;

import java.time.LocalDateTime;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @Description 
 * @Author lzh
 * @Date: 2025-03-23 14:48:21
 */

@Data
@TableName("chat_messages")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value=" chat_messages ", description=" AI对话内容表 ")
public class ChatMessages {

  @ApiModelProperty(value = "null")
  private long messageId;
 

  @ApiModelProperty(value = "关联的会话ID")
  private String sessionId;
 

  @ApiModelProperty(value = "消息角色")
  private String role;
 

  @ApiModelProperty(value = "消息内容")
  private String content;
 

  @ApiModelProperty(value = "消息时间")
  private LocalDateTime createdAt;
 

}
