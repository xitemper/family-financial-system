package com.bishe.service;

import com.bishe.entity.Result;
import com.bishe.vo.UserInfoVO;

import java.util.List;

public interface PermissionService {

    Result getUserAllPermission(Long userId);

    Result getAllPermission();

    Result updateUserPermissions(Long userId, List<String> selectedPermissions);
}
