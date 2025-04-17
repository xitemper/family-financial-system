package com.bishe.mapper;

import com.bishe.entity.*;
import org.apache.ibatis.annotations.*;
import org.springframework.security.core.parameters.P;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Mapper
public interface FamilyMapper {

    @Select("select count(*) from family where family_code = #{code}")
    int judgeExistedByCode(@Param("code") String code);

    @Insert("insert into family (family_code, family_name, creator_id) " +
            "values (#{familyCode},#{familyName},#{userId})")
    int createFamily(@Param("userId") Long userId,@Param("familyName") String familyName,@Param("familyCode") String familyCode);

    @Select("select * from family where family_code = #{familyCode}")
    Family getFamilyByCode(@Param("familyCode") String familyCode);

//  在成员创建家庭组或者加入家庭组时添加member表对应信息
    @Insert("insert into family_member (family_id, user_id, role_id) values(#{familyId},#{userId},#{roleId})")
    int insertMemberRecord(@Param("familyId")long familyId,@Param("userId") Long userId,@Param("roleId") int roleId);

    @Select("select family_id from family_member where user_id = #{userId}")
    Long getFamilyIdByUserId(@Param("userId")Long userId);

    @Select("select * from family where family_id = #{familyId}")
    Family getFamilyInfoById(@Param("familyId")Long familyId);

    @Select("select family_id from family where family_code = #{familyCode}")
    Long getFamilyIdByCode(@Param("familyCode")String familyCode);

    @Insert("insert into family_member (family_id, user_id, role_id) VALUES (#{familyId},#{userId},2)")
    int insertFamilyMember(@Param("userId")Long userId,@Param("familyId") Long familyId);

    @Delete("delete from family_member where user_id=#{userId} and family_id = #{familyId}")
    int exitFamily(@Param("userId")Long userId,@Param("familyId") Long familyId);

    @Select("select count(*) from family_member where family_id = #{familyId}")
    int getFamilyMemberNumByFamilyId(@Param("familyId")Long familyId);

    @Delete("delete from family where family_id = #{familyId}")
    int deleteFamilyById(@Param("familyId")Long familyId);

    @Select("select * from family_member where family_id =#{familyId}")
    List<FamilyMember> getFamilyMemberInfo(@Param("familyId") Long familyId);

    @Select("select * from family_role where role_id = #{roleId}")
    FamilyRole getRoleByRoleId(@Param("roleId") long roleId);

    @Select("select creator_id from family where family_id = #{familyId}")
    Long getCreatorIdByFamilyId(@Param("familyId") Long familyId);

    @Update("update family set creator_id =#{userId} where family_id = #{familyId}")
    int updateFamilyManager(@Param("familyId") Long familyId,@Param("userId") Long userId);

    //根据用户id 去 更新其他几个值
    @Update("update family_member set family_id = #{member.familyId},role_id = #{member.roleId},joined_at=#{member.joinedAt}")
    int updateFamilyMember(@Param("member") FamilyMember member);

    @Delete("delete from family_member where user_id=#{userId}")
    int removePeople(@Param("userId")Long userId);

    @Update("update family set family_name = #{familyName},family_code = #{familyCode} where family_id = #{familyId}")
    void updateProfile(@Param("familyId") Long familyId,@Param("familyName") String familyName,@Param("familyCode") String familyCode);

    @Select("select budget_amount from family_budget where family_id = #{familyId} and year = #{year} and month=#{month}")
    Double getTotalBudget(@Param("familyId")Long familyId,@Param("year") int year,@Param("month") int month);

    @Insert("insert into family_budget (family_id, year, month, budget_amount) " +
            "values (#{familyId},#{year},#{month},#{budget})")
    void addBudget(@Param("familyId")Long familyId,@Param("year") int year,@Param("month") int month,@Param("budget")double budget);

    @Update("update family_budget set budget_amount = #{budget},isNotified = 0 where family_id = #{familyId} and year = #{year} and month = #{month}")
    void updateBudget(@Param("familyId")Long familyId,@Param("year") int year,@Param("month") int month,@Param("budget")double budget);

    @Select("select * from family_budget where year = #{currentYear} and month = #{currentMonth} and isNotified = 0")
    List<FamilyBudget> getAllNotNotifyFamilyId(@Param("currentYear") int currentYear,@Param("currentMonth") int currentMonth);

//    @Select("select sum(amount) from transaction where family_id = #{familyId} and type = 'expense' group by family_id")
//    double getCurrentMonthFamilyTotalExpense(@Param("familyId") long familyId);

    @Select("SELECT IFNULL(SUM(amount), 0) " +
            "FROM transaction " +
            "WHERE family_id = #{familyId} and (isFamilyBill = 1 or isFamilyBill = 2) and type = 'expense' " +
            "AND transaction_date >= #{startDate} " +
            "AND transaction_date < #{endDate}")
    double getCurrentMonthFamilyTotalExpense(@Param("familyId") Long familyId,
                                             @Param("startDate") LocalDateTime startDate,
                                             @Param("endDate") LocalDateTime endDate);

    @Update("update family_budget set isNotified = 1 where id = #{id}")
    void updateBudgetIsNotified(@Param("id") long id);

    @Select("SELECT IFNULL(SUM(amount), 0) " +
            "FROM transaction " +
            "WHERE family_id = #{familyId} and (isFamilyBill = 1 or isFamilyBill = 2) and type = 'income' " +
            "AND transaction_date >= #{startDate} " +
            "AND transaction_date < #{endDate}")
    double getCurrentMonthFamilyTotalIncome(@Param("familyId") Long familyId,
                                             @Param("startDate") LocalDateTime startDate,
                                             @Param("endDate") LocalDateTime endDate);

    @Select("select family_id from family")
    List<Long> getAllFamilyIds();

    @Insert("insert into financial_score_history (family_id, month, balance_score, unnecessary_expense_score, budget_execution_score, liability_score) " +
            "VALUES (#{scoreHistory.familyId},#{scoreHistory.month},#{scoreHistory.balanceScore},#{scoreHistory.unnecessaryExpenseScore},#{scoreHistory.budgetExecutionScore},#{scoreHistory.liabilityScore})")
    void saveFinancialHistoryScore(@Param("scoreHistory") FinancialScoreHistory scoreHistory);

    @Select("select * from financial_score_history where family_id = #{familyId} and month between #{startDate} and #{endDate} order by month asc")
    List<FinancialScoreHistory> getFinancialScoreHistorys(@Param("familyId")Long familyId,@Param("startDate")Date startDate,@Param("endDate")Date endDate);


//    @Select("select * from family_plans where family_id = #{familyId} and  type=#{type} ")
//    List<Plans> getAllActivePlans(@Param("familyId") Long familyId,@Param("type") String type);
}
