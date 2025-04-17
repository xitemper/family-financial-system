package com.bishe.mapper;

import com.bishe.dto.NewPlanDTO;
import com.bishe.entity.Plans;
import com.bishe.entity.User;
import com.bishe.entity.VercodeRecord;
import com.bishe.vo.UserInfoVO;
import com.bishe.vo.UserVO;
import jdk.nashorn.internal.objects.annotations.Getter;
import org.apache.ibatis.annotations.*;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface UserMapper {
    @Insert("insert into vercode_record (phone,content,type) values (#{phone},#{code},#{type})")
    int userGenerateVercode(@Param("phone") String phone,@Param("code") String code,@Param("type") int type);

    @Select("select * from vercode_record where phone=#{phone} and content = #{vercode}")
    VercodeRecord verifyCode(@Param("phone")String phone,@Param("vercode") String vercode);

    @Select("select count(*) from user where phone = #{phone}")
    int judgePhoneIsUsed(@Param("phone") String phone);

    @Insert("insert into user (phone, password, username) values (#{phone},#{password},#{username})")
    int register(User user);

    @Select("select * from user where username=#{username} and password = #{password}")
    User getUser(@Param("username")String username,@Param("password") String password);

    @Select("select username from user where user_id = #{userId}")
    String getUserName(@Param("userId") Integer userId);

    @Select("select * from user where user_id = #{userId}")
    User getUserInfById(@Param("userId") Long userId);

    @Update("update user set username = #{userName} where user_id = #{userId}")
    int changeUserName(@Param("userId")Long userId,@Param("userName") String userName);

    @Update("update user set username = #{userInfoVO.username} where user_id = #{userInfoVO.id}")
    int updateUserProfile(@Param("userInfoVO") UserInfoVO userInfoVO);

    @Update("update user set password = #{newPassword} where user_id = #{userId}")
    int changePassword(@Param("userId") Long userId,@Param("newPassword") String newPassword);

    @Select("select password from user where user_id = #{userId}")
    String getUserPasswordById(@Param("userId")Long userId);

    @Select("select role_id from family_member where user_id = #{userId}")
    int getUserCurRole(@Param("userId") Long userId);

    @Insert("insert into plans (user_id, name, type, target_amount, current_amount, target_date)" +
            " values (#{userId},#{planDTO.name},#{planDTO.type},#{planDTO.targetAmount},#{planDTO.currentAmount},#{planDTO.targetDate}) ")
    void addPlan(@Param("userId")Long userId,@Param("planDTO") NewPlanDTO planDTO);

    @Select("select * from plans where user_id = #{userId} and status = 1")
    List<Plans> getAllPlan(@Param("userId")Long userId);

    @Select("SELECT * FROM plans WHERE status = 1 AND target_date = DATE_ADD(CURDATE(), INTERVAL #{threshold} DAY)")
    List<Plans> getPlansCloseToTargetDate(@Param("today") LocalDate today,
                                         @Param("threshold") int threshold);

//    @Update("UPDATE plans SET status = 2 WHERE status = 1 AND (current_amount >= target_amount OR target_date < CURDATE())")
//    void updateOkPlans();

    @Update("update plans set status = 2 where id = #{planId}")
    void updateStatusToFinish(@Param("planId")Long planId);

    @Select("select * from plans where status = 1 AND (current_amount >= target_amount)")
    List<Plans> getOkPlanList();

    @Select("select * from plans where status = 1 AND (target_date < CURDATE())")
    List<Plans> getOverTimePlanList();

    @Select("select * from plans where id = #{planId}")
    Plans getPlanById(@Param("planId") Long planId);

//    @Select("select * from user where username =#{username} and password =#{password}")
//    User getUser(String username, String password);




}
