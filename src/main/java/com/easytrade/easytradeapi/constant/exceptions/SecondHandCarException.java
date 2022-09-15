/*
 * @Description: 自定义二手车异常
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2022-01-19 02:48:23
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-19 02:48:23
 * 
 * @FilePath:
 * /EasyBuyCar/src/main/java/com/jiandanmaiche/api/constant/exceptions/SecondHandCarException.java
 */
package com.easytrade.easytradeapi.constant.exceptions;

import com.easytrade.easytradeapi.constant.enums.ResultCodeEnum;
import lombok.Getter;


@Getter
public class SecondHandCarException extends RuntimeException {
    private ResultCodeEnum code;
    private String message;

    public SecondHandCarException(ResultCodeEnum code, String message) {
        this.code = code;
        this.message = message;
    }
}
