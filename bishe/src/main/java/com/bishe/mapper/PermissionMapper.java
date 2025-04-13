package com.bishe.mapper;

import com.bishe.entity.Permissions;
import com.bishe.entity.User;
import com.bishe.entity.VercodeRecord;
import com.bishe.vo.AllPermissionVO;
import com.bishe.vo.UserInfoVO;
import org.apache.ibatis.annotations.*;
import org.springframework.security.core.parameters.P;

import java.util.List;

@Mapper
public interface PermissionMapper {

    @Select("select code from permissions")
    List<String> getAllPermissionCode();

    @Insert("insert into user_permissions (family_id, user_id, permission_code) values (#{familyId},#{userId},#{permissionCode})")
    void addUserPermisson(@Param("familyId") long familyId,@Param("userId") Long userId,@Param("permissionCode") String permissionCode);

    @Delete("delete from user_permissions where user_id = #{userId}")
    void deleteUserAllPermisson(@Param("userId")Long userId);

    @Select("select permission_code from user_permissions where user_id = #{userId}")
    List<String> getAllPermissionCodeOfUser(@Param("userId")Long userId);

    @Select("select name from permissions where code = #{code}")
    String getPermissionNameByCode(@Param("code") String code);

    @Select("select * from permissions")
    List<Permissions> getAllPermission();

    @Insert("insert into user_permissions (family_id, user_id, permission_code) values (#{familyId},#{userId},#{selectedPermission})")
    void addUserPermission(@Param("familyId") Long familyId,@Param("userId") Long userId,@Param("selectedPermission") String selectedPermission);

    @Select("select count(*) from user_permissions where user_id = #{userId} and permission_code = #{code}")
    int checkUserHasPermission(@Param("userId") long userId,@Param("code") String code);
}
