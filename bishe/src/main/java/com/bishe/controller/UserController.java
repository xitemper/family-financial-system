package com.bishe.controller;

import com.bishe.dto.NewPlanDTO;
import com.bishe.entity.Result;
import com.bishe.service.UserService;
import com.bishe.vo.UserInfoVO;
import com.bishe.vo.UserVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "用户模块")
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @ApiOperation("用户登录")
    @GetMapping("/login")
    public Result userLogin(@RequestParam("username") String username,@RequestParam("password") String password){
        Result result = userService.userLogin(username,password);
        return result;
    }

    @GetMapping("/getUserName")
    public Result getUserName(@RequestParam Integer userId){
        return userService.getUserName(userId);
    }

    @ApiOperation("用户注册")
    @PostMapping("/register")
    public Result userRegister(@RequestParam("username") String username
            ,@RequestParam("phone") String phone
            ,@RequestParam("vercode") String vercode
            ,@RequestParam("password") String password
            ){
        Result result = userService.userRegister(username,phone,vercode,password);
        return result;
    }

    @ApiOperation("用户注册")
    @PostMapping("/generateVercode")
    public Result userGenerateVercode(
            @RequestParam("phone") String phone,
            @RequestParam("type") int type
            ){
        Result result = userService.userGenerateVercode(phone,type);
        return result;
    }

    @ApiOperation("获取当前用户信息")
    @GetMapping("/userInfo")
    public Result getUserInfo(@RequestParam("userId")Long userId){
        return userService.getUserInfo(userId);
    }

    @ApiOperation("更改用户的用户名")
    @PostMapping("/changeUserName")
    public Result changeUserName(@RequestParam("userId")Long userId,@RequestParam("userName")String userName){
        return userService.changeUserName(userId,userName);
    }

    @ApiOperation("退出家庭组")
    @PostMapping("exitFamily")
    public Result exitFamily(@RequestParam("userId")Long userId,@RequestParam("familyId")Long familyId){
        return userService.exitFamily(userId,familyId);
    }

    @ApiOperation("更换家庭组")
    @PostMapping("changeFamily")
    public Result changeFamily(@RequestParam("userId")Long userId,@RequestParam("familyCode")String familyCode){
        return userService.changeFamily(userId,familyCode);
    }

    @ApiOperation("更新用户信息")
    @PostMapping("updateUserProfile")
    public Result updateUserProfile(@RequestBody UserInfoVO userInfoVO){
        return userService.updateUserProfile(userInfoVO);
    }

    @ApiOperation("修改用户密码")
    @PostMapping("changePassword")
    public Result changePassword(@RequestParam("userId") Long userId,@RequestParam("curPassword")String curPassword,@RequestParam("newPassword")String newPassword){
        return userService.changePassword(userId,curPassword,newPassword);
    }

    @ApiOperation("获取当前用户信息")
    @GetMapping("/getUserCurRole")
    public Result getUserCurRole(@RequestParam("userId")Long userId){
        return userService.getUserCurRole(userId);
    }

    //TODO 修改用户手机号

    @ApiOperation("新增计划")
    @PostMapping("/addPlan")
    public Result addPlan(@RequestParam("userId")Long userId, @RequestBody NewPlanDTO planDTO){
        return userService.addPlan(userId,planDTO);
    }

    @ApiOperation("获取用户的全部计划")
    @GetMapping("/getAllPlan")
    public Result getAllPlan(@RequestParam("userId")Long userId){
        return userService.getAllPlan(userId);
    }


}
