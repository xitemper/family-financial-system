package com.bishe.util;

public enum PermissionCode {
    MANAGER_FAMILY_BILL("管理家庭组账单信息"),
    EDIT_FAMILY_BUDGET("家庭预算管理"),
    INVITE_MEMBER("邀请成员加入家庭组"),
    ALERT_FAMILY_PROFILE("修改家庭组信息"),
    ADD_FAMILY_BILL("新增家庭组账单");

    private final String description;

    PermissionCode(String description) {
        this.description = description;
    }

    public String getCode() {
        return this.name(); // 返回常量名，比如 "ADD_FAMILY_BILL"
    }

    public String getDescription() {
        return this.description;
    }

    @Override
    public String toString() {
        return getCode();
    }
}
