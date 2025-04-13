package com.bishe.controller;

import com.bishe.entity.Result;
import com.bishe.service.FamilyService;
import com.bishe.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;

@Api(tags = "用户模块")
@RestController
@RequestMapping("/family")
public class FamilyController {

    @Autowired
    private FamilyService familyService;



    @ApiOperation("创建新家庭组")
    @PostMapping("/createFamily")
    public Result createFamily(@RequestParam("userId") Long userId,@RequestParam("familyName") String familyName){
        Result result = familyService.createFamily(userId,familyName);
        return result;
    }
    @ApiOperation("通过userId获取家庭组信息")
    @GetMapping("/getFamilyInfoOfUser")
    public Result getFamilyInfoOfUser(@RequestParam("userId") Long userId){
        Result result = familyService.getFamilyInfoOfUser(userId);
        return result;
    }

    @ApiOperation("加入家庭组")
    @PostMapping("/joinFamily")
    public Result joinFamily(@RequestParam("userId") Long userId,@RequestParam("familyCode") String familyCode){
        Result result = familyService.joinFamily(userId,familyCode);
        return result;
    }

    @ApiOperation("获取家庭组成员信息")
    @GetMapping("/getFamilyMemberInfo")
    public Result getFamilyMemberInfo(@RequestParam("familyId") Long familyId){
        Result result = familyService.getFamilyMemberInfo(familyId);
        return result;
    }

    @ApiOperation("移除成员")
    @PostMapping("/removePeople")
    public Result removePeople(@RequestParam("userId") Long userId){
        Result result = familyService.removePeople(userId);
        return result;
    }

    @ApiOperation("修改家庭组信息")
    @PostMapping("/updateProfile")
    public Result updateProfile(@RequestParam("familyId") Long familyId,@RequestParam("familyName") String familyName,@RequestParam("familyCode") String familyCode){
        Result result = familyService.updateProfile(familyId,familyName,familyCode);
        return result;
    }

    @ApiOperation("获取家庭组本月预算")
    @GetMapping("/getTotalBudget")
    public Result getTotalBudget(@RequestParam("familyId") Long familyId,@RequestParam("year") int year,@RequestParam("month") int month){
        return familyService.getTotalBudget(familyId,year,month);
    }

    @ApiOperation("获取家庭组本月预算")
    @PostMapping("/addBudget")
    public Result addBudget(@RequestParam("familyId") Long familyId,@RequestParam("year") int year,@RequestParam("month") int month,@RequestParam("budget") double budget){
        return familyService.addBudget(familyId,year,month,budget);
    }

    @ApiOperation("获取家庭组本月预算")
    @PostMapping("/updateBudget")
    public Result updateBudget(@RequestParam("familyId") Long familyId,@RequestParam("year") int year,@RequestParam("month") int month,@RequestParam("budget") double budget){
        return familyService.updateBudget(familyId,year,month,budget);
    }

    @ApiOperation("获取家庭组当月总支出")
    @GetMapping("/getCurMonthTotalExpense")
    public Result getCurMonthTotalExpense(@RequestParam("familyId") Long familyId){
        return familyService.getCurMonthTotalExpense(familyId);
    }

    @ApiOperation("获取家庭组本月财务健康状况评分")
    @GetMapping("/financialScore")
    public Result getScores(@RequestParam("familyId") Long familyId) {
        return familyService.calculateDimensionScores(familyId);
    }

}
