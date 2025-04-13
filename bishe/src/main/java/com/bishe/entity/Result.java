package com.bishe.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("结果类")
public class Result implements Serializable {

    @ApiModelProperty("状态码")
    private String code;

    @ApiModelProperty("返回描述")
    private String msg;

    @ApiModelProperty("内容")
    private Object content;

    public static Result succeed(String msg){
        Result result = new Result();
        if(StringUtils.isNotBlank(msg)){
            result.msg=msg;
        }
        result.code="200";
        return result;
    }

    public static Result succeed(String msg,Object obj){
        Result result = new Result();
        if(StringUtils.isNotBlank(msg)){
            result.msg=msg;
        }
        if(obj!=null){
            result.content=obj;
        }
        result.code="200";
        return result;
    }

    public static Result succeed(){
        Result result = new Result();
        result.msg = "操作成功";
        result.code="200";
        return result;
    }

    public static Result fail(String msg){
        Result result = new Result();
        if(StringUtils.isNotBlank(msg)){
            result.msg=msg;
        }
        result.code="500";
        return result;
    }

    public static Result fail(String msg,Object obj){
        Result result = new Result();
        if(StringUtils.isNotBlank(msg)){
            result.msg=msg;
        }
        if(obj!=null){
            result.content=obj;
        }
        result.code="500";
        return result;
    }

    public static Result fail(){
        Result result = new Result();
        result.msg = "服务器内部异常";
        result.code="500";
        return result;
    }
}
