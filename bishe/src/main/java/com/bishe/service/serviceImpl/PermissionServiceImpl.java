package com.bishe.service.serviceImpl;

import com.bishe.entity.*;
import com.bishe.mapper.FamilyMapper;
import com.bishe.mapper.PermissionMapper;
import com.bishe.mapper.UserMapper;
import com.bishe.service.PermissionService;
import com.bishe.service.UserService;
import com.bishe.vo.AllPermissionVO;
import com.bishe.vo.UserInfoVO;
import com.bishe.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private FamilyMapper familyMapper;

    @Override
    public Result getUserAllPermission(Long userId) {
        List<AllPermissionVO> permissionVOList=new ArrayList<>();
        List<String> permissionCodeList =  permissionMapper.getAllPermissionCodeOfUser(userId);
        for (String code : permissionCodeList) {
            AllPermissionVO allPermissionVO = new AllPermissionVO();
            allPermissionVO.setCode(code);
            allPermissionVO.setName(permissionMapper.getPermissionNameByCode(code));
            permissionVOList.add(allPermissionVO);
        }
        return Result.succeed("getUserAllPermission方法执行成功",permissionVOList);
    }

    @Override
    public Result getAllPermission() {
        List<AllPermissionVO> permissionVOList = new ArrayList<>();
        List<Permissions> permissionsList = permissionMapper.getAllPermission();
        if(permissionsList!=null&&!permissionsList.isEmpty()){
            for (Permissions permissions : permissionsList) {
                AllPermissionVO allPermissionVO = new AllPermissionVO();
                allPermissionVO.setName(permissions.getName());
                allPermissionVO.setCode(permissions.getCode());
                permissionVOList.add(allPermissionVO);
            }
        }
        return Result.succeed("getAllPermission方法执行成功",permissionVOList);
    }

    @Override
    @Transactional
    public Result updateUserPermissions(Long userId, List<String> selectedPermissions) {
        permissionMapper.deleteUserAllPermisson(userId);
        Long familyId = familyMapper.getFamilyIdByUserId(userId);
        for (String selectedPermission : selectedPermissions) {
            permissionMapper.addUserPermission(familyId,userId,selectedPermission);
        }
        return Result.succeed("修改用户权限成功!");
    }
}
