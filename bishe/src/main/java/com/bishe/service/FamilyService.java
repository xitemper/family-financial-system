package com.bishe.service;

import com.bishe.entity.Result;

import java.time.YearMonth;

public interface FamilyService {

    Result createFamily(Long userId, String familyName);

    Result getFamilyInfoOfUser(Long userId);

    Result joinFamily(Long userId, String familyCode);

    Result getFamilyMemberInfo(Long familyId);

    Result removePeople(Long userId);

    Result updateProfile(Long familyId, String familyName, String familyCode);

    Result getTotalBudget(Long familyId, int year, int month);

    Result addBudget(Long familyId, int year, int month,double budget);

    Result updateBudget(Long familyId, int year, int month, double budget);

    void checkAndRemindBudget();

    Result getCurMonthTotalExpense(Long familyId);

    Result calculateDimensionScores(Long familyId);
}
