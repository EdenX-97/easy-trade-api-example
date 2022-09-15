/*
 * @Description: 用户状态的枚举类
 * @Author: Mo Xu
 * @Date: 2021-11-12 20:08:10
 * @LastEditors: Mo Xu
 * @LastEditTime: 2022-01-03 20:51:54
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/constant/enums/UserStatusEnum.java
 */
package com.easytrade.easytradeapi.constant.enums;

import lombok.Getter;


@Getter
public enum UserStatusEnum {
    DISABLE("ROLE_DISABLE"), // 未激活用户
    VERIFIED("ROLE_VERIFIED"), // 手机验证用户
    USER("ROLE_USER"), // 实名认证后的普通用户
    DEALER("ROLE_DEALER"), // 车商公司认证后的车商用户
    BLOCKED("ROLE_BLOCKED"), // 禁用账号
    ADMIN("ROLE_ADMIN"); // 管理员

    private String status;

    UserStatusEnum(String status) {
        this.status = status;
    }
}
