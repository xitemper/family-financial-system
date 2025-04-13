package com.bishe.util;

import com.bishe.mapper.FamilyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component // 添加注解，让 Spring 管理该类
public class FamilyCodeGenerator {
    private static final String PREFIX = "FAM-";
    private static final int CODE_LENGTH = 8;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final Random RANDOM = new Random();

    private final FamilyMapper familyMapper;

    @Autowired // 通过构造函数注入
    public FamilyCodeGenerator(FamilyMapper familyMapper) {
        this.familyMapper = familyMapper;
    }

    public String generateUniqueFamilyCode() {
        String code;
        do {
            code = generateRandomCode();
        } while (isCodeExists(code));
        return code;
    }

    private String generateRandomCode() {
        // 原有逻辑保持不变
        StringBuilder sb = new StringBuilder(PREFIX);
        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = RANDOM.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }
        return sb.toString();
    }

    private boolean isCodeExists(String code) {
        int num = familyMapper.judgeExistedByCode(code);
        return num > 0; // 修正逻辑：如果存在返回 true，否则返回 false
    }
}