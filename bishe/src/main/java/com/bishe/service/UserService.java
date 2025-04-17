package com.bishe.service;

import com.bishe.dto.NewPlanDTO;
import com.bishe.entity.Result;
import com.bishe.vo.UserInfoVO;
import com.bishe.vo.UserVO;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface UserService {
    Result userLogin(String username,String password);

    Result userRegister(String username, String phone,  String vercode,String password);

    Result userGenerateVercode(String phone,int type);

    Result getUserName(Integer userId);

    Result getUserInfo(Long userId);

    Result changeUserName(Long userId, String userName);

    Result exitFamily(Long userId, Long familyId);

    Result changeFamily(Long userId, String familyCode);

    Result updateUserProfile(UserInfoVO userInfoVO);

    Result changePassword(Long userId,String curPassword,String newPassword);

    Result getUserCurRole(Long userId);

    Result addPlan(Long userId, NewPlanDTO planDTO);

    Result getAllPlan(Long userId);

    void checkAndRemindPlans();

    Result getPlanProgress(Long planId);
}
