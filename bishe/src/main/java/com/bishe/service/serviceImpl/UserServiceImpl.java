package com.bishe.service.serviceImpl;

import com.bishe.dto.NewPlanDTO;
import com.bishe.entity.*;
import com.bishe.mapper.*;
import com.bishe.service.FamilyService;
import com.bishe.service.UserService;
import com.bishe.vo.UserInfoVO;
import com.bishe.vo.UserVO;
import com.fasterxml.uuid.Generators;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private FamilyMapper familyMapper;

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private TransactionMapper transactionMapper;

    @Autowired
    private NotifyMapper notifyMapper;

    @Override
    public Result userLogin(String username,String password) {
        UserVO userVO = new UserVO();
        User user =  userMapper.getUser(username,password);
        if(user==null){
            return Result.fail("用户名或密码错误");
        }
        userVO.setUsername(user.getUsername());
        userVO.setId(user.getUserId());
        return Result.succeed("登录成功",userVO);
    }

    @Override
    public Result userRegister(String username, String phone, String vercode, String password) {
//        用自增id
//        UUID userId= Generators.randomBasedGenerator().generate();
        boolean isCorrect = verifyCode(phone, vercode);
        if(isCorrect){
            User user = new User();
            user.setUsername(username);
            user.setPhone(phone);
            user.setPassword(password);
            int count =  userMapper.judgePhoneIsUsed(phone);
            if(count>0){
                return Result.fail("当前手机号已被注册");
            }
            int num =  userMapper.register(user);
            if(num>0){
                return Result.succeed("注册成功！");
            }else{
                return Result.fail("注册失败");
            }
        }else{
            return Result.fail("验证码错误或过期");
        }
    }


    @Override
    public Result userGenerateVercode(String phone,int type) {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // 生成100000到999999之间的随机数
        System.out.println("生成的6位验证码是: " + code);
        int result =  userMapper.userGenerateVercode(phone,String.valueOf(code),type);
        if(result>0){
            return Result.succeed("验证码已发送，请查收！");
        }else{
            return Result.fail("验证码生成失败");
        }
    }

    @Override
    public Result getUserName(Integer userId) {
        String username =  userMapper.getUserName(userId);
        return Result.succeed("操作成功",username);
    }

    @Override
    public Result getUserInfo(Long userId) {
        User user = userMapper.getUserInfById(userId);
        if (user == null) {
            return Result.fail("当前用户状态异常");
        }
        UserInfoVO userInfoVO = new UserInfoVO();
        userInfoVO.setId(userId);
        userInfoVO.setUsername(user.getUsername());
        userInfoVO.setPhone(user.getPhone());
        //TODO 从Family_Member表获取当前userId对应的familyId
        //TODO 通过familyId去family表获取family_name
        return Result.succeed("",userInfoVO);
    }

    @Override
    @Transactional
    public Result changeUserName(Long userId, String userName) {
        int num = userMapper.changeUserName(userId,userName);
        if(num>0){
            return Result.succeed("修改成功");
        }else{
            return Result.fail("修改失败，请稍后重试");
        }
    }

    @Override
    @Transactional
    public Result exitFamily(Long userId, Long familyId) {
        Long creatorId =  familyMapper.getCreatorIdByFamilyId(familyId);
        int affectNum = familyMapper.exitFamily(userId,familyId);
        //删除用户在当前家庭组的账单信息
        transactionMapper.deleteUserFamilyBill(userId);
        if(affectNum>0){
            int memberNum =  familyMapper.getFamilyMemberNumByFamilyId(familyId);
            //家庭组没成员后直接删除
            if(memberNum ==0){
                familyMapper.deleteFamilyById(familyId);
                transactionMapper.deleteFamilyAllBill(familyId);
            }
            if(memberNum>0&&creatorId.equals(userId)){
                List<FamilyMember> familyMemberInfo = familyMapper.getFamilyMemberInfo(familyId);
                FamilyMember earliestMember = familyMemberInfo.stream().min(Comparator.comparing(FamilyMember::getJoinedAt)).orElse(null);
                // 如果用户是管理员 更新了用户当前family的管理员为family中最早加入的用户
                int updateAffect1 =  familyMapper.updateFamilyManager(familyId,earliestMember.getUserId());
                earliestMember.setRoleId(1);
                int updateAffect2 = familyMapper.updateFamilyMember(earliestMember);

                //删除当前新变成管理员的用户拥有的的全部权限
                permissionMapper.deleteUserAllPermisson(earliestMember.getUserId());
                //给用户添加 家庭组 全部权限
                List<String> permissionCodeList = permissionMapper.getAllPermissionCode();
                for (String permissionCode : permissionCodeList) {
                    permissionMapper.addUserPermisson(familyId,userId,permissionCode);
                }

                if(updateAffect1>0&&updateAffect2>0){
                    return Result.succeed("退出成功");
                }else{
                    return Result.fail("退出家庭组功能异常--更新家庭组管理员部分，请稍后重试");
                }
            }
            //删除当前用户在原家庭组的所有权限
            permissionMapper.deleteUserAllPermisson(userId);

            //给家庭组其他人发通知
            List<FamilyMember> familyMemberInfo = familyMapper.getFamilyMemberInfo(familyId);
            if(familyMemberInfo!=null&&!familyMemberInfo.isEmpty()){
                User userInfo = userMapper.getUserInfById(userId);
                for (FamilyMember familyMember : familyMemberInfo) {
                    if(familyMember.getUserId()!=userId) {
                        UserNotifications newUserNotifications = new UserNotifications();
                        newUserNotifications.setUserId(familyMember.getUserId());
                        newUserNotifications.setType(2);
                        newUserNotifications.setMessage("用户：" + userInfo.getUsername() + "退出了您的家庭组!");
                        notifyMapper.addNotify(newUserNotifications);
                    }
                }
            }



            return Result.succeed("退出成功");
        }else{
            return Result.fail("退出家庭组功能异常，请稍后重试");
        }
    }

    @Override
    @Transactional
    public Result changeFamily(Long userId, String familyCode) {
        Long oldFamilyId = familyMapper.getFamilyIdByUserId(userId);
        Family oldFamily = familyMapper.getFamilyInfoById(oldFamilyId);
        Long creatorId =  familyMapper.getCreatorIdByFamilyId(oldFamilyId);
        if(familyCode.equals(oldFamily.getFamilyCode())){
            return Result.fail("当前已在该家庭组中！");
        }
        Family family = familyMapper.getFamilyByCode(familyCode);

        if(family!=null){
            familyMapper.exitFamily(userId,oldFamilyId);
            //删除用户在当前家庭组的账单信息
            transactionMapper.deleteUserFamilyBill(userId);
            int oldFamilyMemberNum = familyMapper.getFamilyMemberNumByFamilyId(oldFamilyId);
            //旧家庭组没人的话直接删了
            if(oldFamilyMemberNum==0) {
                familyMapper.deleteFamilyById(oldFamilyId);
                transactionMapper.deleteFamilyAllBill(oldFamilyId);
            }
            if(oldFamilyMemberNum>0&&creatorId.equals(userId)){
                List<FamilyMember> familyMemberInfo = familyMapper.getFamilyMemberInfo(oldFamilyId);
                FamilyMember earliestMember = familyMemberInfo.stream().min(Comparator.comparing(FamilyMember::getJoinedAt)).orElse(null);
                // 如果用户是管理员 更新了用户当前family的管理员为family中最早加入的用户
                int updateAffect1 =  familyMapper.updateFamilyManager(oldFamilyId,earliestMember.getUserId());
                earliestMember.setRoleId(1);
                int updateAffect2 = familyMapper.updateFamilyMember(earliestMember);

                //删除当前新变成管理员的用户拥有的的全部权限
                permissionMapper.deleteUserAllPermisson(earliestMember.getUserId());
                //给用户添加 家庭组 全部权限
                List<String> permissionCodeList = permissionMapper.getAllPermissionCode();
                for (String permissionCode : permissionCodeList) {
                    permissionMapper.addUserPermisson(oldFamilyId,userId,permissionCode);
                }

                if(updateAffect1<=0&&updateAffect2<=0){
                    return Result.fail("退出家庭组功能异常--更新家庭组管理员部分，请稍后重试");
                }
            }
            int affectNum = familyMapper.insertFamilyMember(userId, family.getFamilyId());
            //删除当前用户在原家庭组的所有权限
            permissionMapper.deleteUserAllPermisson(userId);


            //给旧家庭组其他人发通知
            List<FamilyMember> familyMemberInfo = familyMapper.getFamilyMemberInfo(oldFamilyId);
            if(familyMemberInfo!=null&&!familyMemberInfo.isEmpty()){
                User userInfo = userMapper.getUserInfById(userId);
                for (FamilyMember familyMember : familyMemberInfo) {
                    if(familyMember.getUserId()!=userId){
                        UserNotifications newUserNotifications = new UserNotifications();
                        newUserNotifications.setUserId(familyMember.getUserId());
                        newUserNotifications.setType(2);
                        newUserNotifications.setMessage("用户："+userInfo.getUsername()+"退出了您的家庭组!");
                        notifyMapper.addNotify(newUserNotifications);
                    }
                }
            }

            //给新家庭组其他人发通知
            List<FamilyMember> NewFamilyMemberInfo = familyMapper.getFamilyMemberInfo(family.getFamilyId());
            if(NewFamilyMemberInfo!=null&&!NewFamilyMemberInfo.isEmpty()){
                User userInfo = userMapper.getUserInfById(userId);
                for (FamilyMember familyMember : NewFamilyMemberInfo) {
                    if(familyMember.getUserId()!=userId) {
                        UserNotifications newUserNotifications = new UserNotifications();
                        newUserNotifications.setUserId(familyMember.getUserId());
                        newUserNotifications.setType(2);
                        newUserNotifications.setMessage("用户：" + userInfo.getUsername() + "加入了您的家庭组!");
                        notifyMapper.addNotify(newUserNotifications);
                    }
                }
            }

            if(affectNum>0){
                return Result.succeed("更改家庭组成功！",family);
            }else{
                return Result.fail("更改家庭组失败，请稍后重试！");
            }
        }else{
            return Result.fail("家庭组不存在！请检查家庭组编码是否正确！");
        }
    }

    @Override
    public Result updateUserProfile(UserInfoVO userInfoVO) {
        int affectNum = userMapper.updateUserProfile(userInfoVO);
        if(affectNum>0){
            return Result.succeed("用户信息更新成功!");
        }
        return Result.fail("用户信息更新失败！请稍后重试~");
    }

    @Override
    public Result changePassword(Long userId,String curPassword,String newPassword) {
        String userPassword = userMapper.getUserPasswordById(userId);
        if(userPassword.equals(curPassword)){
            int affectNum = userMapper.changePassword(userId,newPassword);
            if(affectNum>0){
                return Result.succeed("用户密码更新成功!");
            }else{
                return Result.fail("用户密码修改失败，请稍后重试！");
            }
        }
        return Result.fail("用户旧密码错误，请重新输入！");
    }

    @Override
    public Result getUserCurRole(Long userId) {
        int curRole = userMapper.getUserCurRole(userId);
        return Result.succeed("获取用户当前权限id成功！",curRole);
    }

    @Override
    public Result addPlan(Long userId, NewPlanDTO planDTO) {
        userMapper.addPlan(userId,planDTO);
        return Result.succeed("addPlan方法执行成功");
    }

    @Override
    public Result getAllPlan(Long userId) {
        List<Plans> plansList = userMapper.getAllPlan(userId);
        return Result.succeed("getAllPlan方法执行成功!",plansList);
    }

    @Override
    @Transactional
    public void checkAndRemindPlans() {
        LocalDate today = LocalDate.now();
        int thresholdDays = 3;

        //先判断 status为1的plan 有哪些 curAmount超过或达到targetAmount 修改status为2 发提醒
        List<Plans> okPlanList = userMapper.getOkPlanList();
        //都要发消息
        for (Plans plans : okPlanList) {
            UserNotifications newUserNotifications = new UserNotifications();
            newUserNotifications.setUserId(plans.getUserId());
            newUserNotifications.setType(plans.getType().equals("income")?4:3);
            newUserNotifications
                    .setMessage("尊敬的用户您好！您设置的"
                            +(plans.getType().equals("income")?"还款":"理财")
                            +"计划:"
                            +plans.getName()
                            +" 已在规定时间内完成！该计划已结束!");
            notifyMapper.addNotify(newUserNotifications);
            userMapper.updateStatusToFinish(plans.getId());
        }


        //再判断 status为1的plan 有哪些 超过规定时间 发提醒
        List<Plans> overtimePlanList = userMapper.getOverTimePlanList();
        //都要发消息
        for (Plans plans : overtimePlanList) {
            UserNotifications newUserNotifications = new UserNotifications();
            newUserNotifications.setUserId(plans.getUserId());
            newUserNotifications.setType(plans.getType().equals("income")?4:3);
            newUserNotifications
                    .setMessage("尊敬的用户您好！您设置的"
                            +(plans.getType().equals("income")?"还款":"理财")
                            +"计划:"
                            +plans.getName()
                            +" 未在设定期限时间内完成，该计划已结束!");
            notifyMapper.addNotify(newUserNotifications);
            userMapper.updateStatusToFinish(plans.getId());
        }

        // 获取需要提醒的计划
        List<Plans> plansList = userMapper.getPlansCloseToTargetDate(today, thresholdDays);
        for (Plans plans : plansList) {
            UserNotifications newUserNotifications = new UserNotifications();
            newUserNotifications.setUserId(plans.getUserId());
            newUserNotifications.setType(plans.getType().equals("income")?4:3);
            newUserNotifications
                    .setMessage("尊敬的用户您好！您设置的"
                            +(plans.getType().equals("income")?"还款":"理财")
                            +"计划:"
                            +plans.getName()
                            +" 在"+thresholdDays+"天后即将截止，请注意!");
            notifyMapper.addNotify(newUserNotifications);
            userMapper.updateStatusToFinish(plans.getId());
        }
    }

    private boolean verifyCode(String phone,String vercode){
        VercodeRecord vercodeRecord = userMapper.verifyCode(phone,vercode);//验证 验证码是否正确
        if(ObjectUtils.isEmpty(vercodeRecord)){
            return false;
        }
        // 将数据库中的 UTC 过期时间转换为本地时区
        LocalDateTime utcExpiredAt = vercodeRecord.getExpiredAt();
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Shanghai"));
        return vercodeRecord!=null &&vercodeRecord.getIsused() == 0
                && utcExpiredAt.isAfter(now);
    }
}
