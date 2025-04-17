package com.bishe.mapper;

import com.bishe.dto.CategoryMonthlyDataDTO;
import com.bishe.dto.TransactionSummary;
import com.bishe.entity.*;
import com.bishe.vo.TransactionVO;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface TransactionMapper {

    @Select("select * from transaction where user_id = #{userId} and type = #{type} ")
    List<Transaction> getTransactionsByType(@Param("userId") Long userId,@Param("type") String type);

    @Insert("insert into transaction (user_id, family_id, family_name, type, category, amount, description,transaction_date,isFamilyBill) " +
            "values (#{userId},#{transaction.familyId},#{transaction.familyName}" +
            ",#{transaction.type},#{transaction.category},#{transaction.amount},#{transaction.description},#{transaction.transactionDate},#{transaction.isFamilyBill})")
    @Options(useGeneratedKeys = true, keyProperty = "transaction.transactionId", keyColumn = "transaction_id")
    int addTransaction(@Param("userId") Long userId,@Param("transaction") Transaction transaction);

    @Select("select * from transaction where user_id = #{userId} and type = #{type} and (isFamilyBill = 0 or isFamilyBill = 2)  and transaction_date between #{startDate} and #{endDate}")
    List<Transaction> getTransactionsByTypeAndDate(@Param("userId") Long userId,@Param("type") String type,@Param("startDate") LocalDateTime startDate,@Param("endDate") LocalDateTime endDate);

    @Select("select sum(amount) from transaction where user_id = #{userId} and (isFamilyBill = 0 or isFamilyBill = 2) and type = #{type} and transaction_date between #{startDate} and #{endDate} group by type")
    Double  getTotalIncome(@Param("userId") Long userId,@Param("type") String type,@Param("startDate") LocalDateTime startDate,@Param("endDate") LocalDateTime endDate);

    @Select("SELECT DATE(transaction_date) AS transaction_date, SUM(amount) AS total_amount " +
            "FROM transaction " +
            "WHERE user_id = #{userId} AND type = #{type} and (isFamilyBill = 0 or isFamilyBill = 2)   AND transaction_date BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY DATE(transaction_date)")
    List<TransactionSummary> findTransactionSummaryByMonth(@Param("userId") Long userId,
                                                           @Param("type") String type,
                                                           @Param("startDate") LocalDateTime startDate,
                                                           @Param("endDate") LocalDateTime endDate);


    @Delete("delete from transaction where user_id = #{userId} and (isFamilyBill = 1 or isFamilyBill = 2)")
    void deleteUserFamilyBill(@Param("userId") Long userId);

    @Delete("delete from transaction where family_id =#{familyId} and (isFamilyBill = 1 or isFamilyBill = 2)")
    void deleteFamilyAllBill(@Param("familyId")Long familyId);

    @Select("select * from transaction where family_id = #{familyId} and type = #{type}  and transaction_date between #{startDate} and #{endDate} and (isFamilyBill = 1 or isFamilyBill = 2)")
    List<Transaction> getFamilyTransactionsByTypeAndDate(@Param("familyId") Long familyId
            ,@Param("type") String type,@Param("startDate")  LocalDateTime startDate
            ,@Param("endDate")  LocalDateTime endDate);

    @Insert("insert into plan_transaction (transaction_id, plan_id) values (#{transactionId},#{planId})")
    void addPlanTransaction(@Param("transactionId") long transactionId,@Param("planId") Long planId);

    @Update("update plans set current_amount = #{amount} where id = #{planId}")
    void updatePlanCurrentAmount(@Param("planId")Long planId,@Param("amount") double amount);

    @Update("update plans set status = 2 where id = #{planId}")
    void updatePlanStatusToFinish(@Param("planId")Long planId);

    @Select("select sum(amount) from transaction where family_id = #{familyId} and type = #{type} and (isFamilyBill = 1 or isFamilyBill = 2) and transaction_date between #{startDate} and #{endDate}")
    BigDecimal getFamilyMonthlyTotalAmountByType(@Param("familyId")Long familyId,@Param("type")  String type,@Param("startDate")  LocalDateTime startDate
            ,@Param("endDate")  LocalDateTime endDate);

    @Select("select sum(amount) from transaction where family_id = #{familyId} " +
            "and type = 'expense' and category in ('购物','零食','运动','娱乐','烟酒','礼品','维修','快递','游戏') " +
            "and (isFamilyBill = 1 or isFamilyBill = 2)  and transaction_date between #{startDate} and #{endDate}")
    BigDecimal sumUnnecessaryExpense(@Param("familyId")Long familyId,@Param("startDate")  LocalDateTime startDate
            ,@Param("endDate")  LocalDateTime endDate);

    @Select("select sum(amount) from transaction where family_id = #{familyId} " +
            "and type = 'expense' and category in ('还款') " +
            "and (isFamilyBill = 1 or isFamilyBill = 2)  and transaction_date between #{startDate} and #{endDate}")
    BigDecimal getMonthlyLiabilities(@Param("familyId")Long familyId,@Param("startDate")  LocalDateTime startDate
            ,@Param("endDate")  LocalDateTime endDate);

    @Update("update transaction set category = #{transactionVO.category}" +
            ",amount = #{transactionVO.amount}" +
            ",description = #{transactionVO.description}" +
            ",transaction_date = #{transactionVO.time} where transaction_id = #{transactionVO.transactionId}")
    void updateTransaction(@Param("transactionVO") TransactionVO transactionVO);

    @Delete("delete from transaction where transaction_id = #{transactionId}")
    void deleteTransaction(@Param("transactionId") Long transactionId);

    @Select("select * from plan_transaction where transaction_id = #{transactionId}")
    PlanTransaction getPlanTransactionByTransactionId(@Param("transactionId") Long transactionId);

    @Select("select * from transaction where transaction_id = #{transactionId}")
    Transaction getTransactionById(@Param("transactionId")Long transactionId);

    @Select("select * from plans where id = #{planId}")
    Plans getPlanByPlanId(@Param("planId") long planId);

    @Select("SELECT DATE(transaction_date) AS transaction_date, SUM(amount) AS total_amount " +
            "FROM transaction " +
            "WHERE family_id = #{familyId} AND type = #{type} AND (isFamilyBill = 1 or isFamilyBill =2)  AND (transaction_date BETWEEN #{startDate} AND #{endDate}) " +
            "GROUP BY DATE(transaction_date)")
    List<TransactionSummary> findFamilyTransactionSummaryByMonth(@Param("familyId") Long familyId,
                                                           @Param("type") String type,
                                                           @Param("startDate") LocalDateTime startDate,
                                                           @Param("endDate") LocalDateTime endDate);


    @Select("SELECT category, SUM(amount) AS amount " +
            "FROM transaction " +
            "WHERE user_id = #{userId} AND type = #{type} and (isFamilyBill = 0 or isFamilyBill = 2)   AND  transaction_date BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY category order by amount asc")
    List<CategoryMonthlyDataDTO> getMonthlyCategoryProportion(@Param("userId") Long userId,
                                                              @Param("type") String type,
                                                              @Param("startDate") LocalDateTime startDate,
                                                              @Param("endDate") LocalDateTime endDate);


    @Select("SELECT category, SUM(amount) AS amount " +
            "FROM transaction " +
            "WHERE family_id = #{familyId} AND type = #{type} AND (isFamilyBill= 1 or isFamilyBill = 2)  AND (transaction_date BETWEEN #{startDate} AND #{endDate}) " +
            "GROUP BY category order by amount asc")
    List<CategoryMonthlyDataDTO> getFamilyMonthlyCategoryProportion(@Param("familyId") Long familyId,
                                                              @Param("type") String type,
                                                              @Param("startDate") LocalDateTime startDate,
                                                              @Param("endDate") LocalDateTime endDate);



//    @Update("update plans set current_amount = #{plan.currentAmount} where id= #{plan.id}")
//    void updatePlanCurrentAmount(@Param("plan") Plans plan);
}
