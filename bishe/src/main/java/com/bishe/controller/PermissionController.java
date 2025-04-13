package com.bishe.controller;

import com.bishe.entity.Result;
import com.bishe.service.PermissionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "权限管理模块")
@RestController
@RequestMapping("/permission")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @ApiOperation("获取当前用户全部权限")
    @GetMapping("/getUserAllPermission")
    public Result getUserAllPermission(@RequestParam("userId") Long userId){
        return permissionService.getUserAllPermission(userId);
    }

    @ApiOperation("获取权限表全部权限")
    @GetMapping("/getAllPermission")
    public Result getAllPermission(){
        return permissionService.getAllPermission();
    }
    @ApiOperation("修改用户权限")
    @PostMapping("/updateUserPermissions")
    public Result updateUserPermissions(@RequestParam("userId")Long userId,@RequestBody List<String>selectedPermissions){
        return permissionService.updateUserPermissions(userId,selectedPermissions);
    }


}
