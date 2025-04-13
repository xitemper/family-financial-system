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
 * @Date: 2025-03-24 15:56:31
 */

@Data
@TableName("chat_sessions")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value=" chat_sessions ", description=" AI对话总表 ")
public class ChatSessions {

  @ApiModelProperty(value = "会话ID（UUID格式）")
  private String sessionId;
 

  @ApiModelProperty(value = "关联的用户ID")
  private long userId;
 

  @ApiModelProperty(value = "对话标题")
  private String title;
 

  @ApiModelProperty(value = "最后活跃时间")
  private LocalDateTime lastActive;
 

}
