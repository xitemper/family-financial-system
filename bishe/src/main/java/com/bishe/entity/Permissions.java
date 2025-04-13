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
 * @Date: 2025-04-10 19:47:51
 */

@Data
@TableName("permissions")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value=" permissions ", description=" 系统功能权限表 ")
public class Permissions {

  @ApiModelProperty(value = "权限主键ID")
  private long id;
 

  @ApiModelProperty(value = "权限标识，如 VIEW_OTHERS_RECORDS")
  private String code;
 

  @ApiModelProperty(value = "权限名称，用于展示")
  private String name;
 

  @ApiModelProperty(value = "权限说明")
  private String description;
 

}
