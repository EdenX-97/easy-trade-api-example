/*
 * @Description: 自定义JWT异常
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2021-12-16 21:08:41
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-03 20:14:15
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/exceptions/JWTException.java
 */
package com.easytrade.easytradeapi.constant.exceptions;

import com.easytrade.easytradeapi.constant.enums.ResultCodeEnum;
import lombok.Getter;


@Getter
public class JWTException extends RuntimeException {
    private ResultCodeEnum code;
    private String message;

    public JWTException(ResultCodeEnum code, String message) {
        this.code = code;
        this.message = message;
    }
}

