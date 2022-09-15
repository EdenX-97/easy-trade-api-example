/*
 * @Description: 自定义用户异常类
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2022-01-03 23:12:13
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-03 23:12:13
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/exceptions/UserException.java
 */
package com.easytrade.easytradeapi.constant.exceptions;

import com.easytrade.easytradeapi.constant.enums.ResultCodeEnum;
import lombok.Getter;


@Getter
public class UserException extends RuntimeException {
    private ResultCodeEnum code;
    private String message;

    public UserException(ResultCodeEnum code, String message) {
        this.code = code;
        this.message = message;
    }
}

