/*
 * @Description: 用户登录记录类
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2022-01-14 17:21:05
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-15 20:38:47
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/constant/consists/LoginRecord.java
 */
package com.easytrade.easytradeapi.constant.consists;

import java.util.Date;
import org.springframework.util.ClassUtils;
import lombok.Data;
import lombok.ToString;



@Data
@ToString
public class LoginRecord {
    // 用户登录的ip地址
    private String loginIp;

    // 用户登录的时间
    private Date loginDate;
}

