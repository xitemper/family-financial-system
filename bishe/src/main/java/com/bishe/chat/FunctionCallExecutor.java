package com.bishe.chat;

import com.bishe.mapper.TransactionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.AbstractMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;
import static java.util.stream.Collectors.toMap;
@Service
public class FunctionCallExecutor {
    
//    @Autowired
//    private TransactionMapper transactionMapper;
//
//    private final Map<String, Function<Map<String, Object>, Object>> functions =
//            Stream.of(
//                    new AbstractMap.SimpleEntry<>(
//                            "get_month_expense",
//                            (Function<Map<String, Object>, Object>) this::executeMonthExpense
//                    ),
//                    new AbstractMap.SimpleEntry<>(
//                            "get_category_expense",
//                            (Function<Map<String, Object>, Object>) this::executeCategoryExpense
//                    )
//            ).collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
//
//    public Object executeFunction(String functionName, Map<String, Object> params) {
//        Function<Map<String, Object>, Object> function = functions.get(functionName);
//        if (function == null) {
//            throw new IllegalArgumentException("未知函数: " + functionName);
//        }
//        return function.apply(params);
//    }
//
//    // 示例函数1：查询本月支出
//    private Double executeMonthExpense(Map<String, Object> params) {
//        int year = (int) params.getOrDefault("year", Year.now().getValue());
//        int month = (int) params.getOrDefault("month", Month.from(LocalDate.now()).getValue());
//        return transactionMapper.getMonthExpense(year, month);
//    }
//
//    // 示例函数2：按分类查询
//    private Double executeCategoryExpense(Map<String, Object> params) {
//        String category = (String) params.get("category");
//        int year = (int) params.getOrDefault("year", Year.now().getValue());
//        return transactionMapper.getCategoryExpense(category, year);
//    }
}