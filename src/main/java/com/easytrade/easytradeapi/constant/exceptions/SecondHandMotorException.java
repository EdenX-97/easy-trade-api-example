/*
 * @Description: 自定义汽车模版异常
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2022-01-03 22:13:28
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-03 22:20:40
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/exceptions/ExampleCarException.java
 */
package com.easytrade.easytradeapi.constant.exceptions;

import com.easytrade.easytradeapi.constant.enums.ResultCodeEnum;
import lombok.Getter;


@Getter
public class SecondHandMotorException extends RuntimeException {
    private ResultCodeEnum code;
    private String message;

    public SecondHandMotorException(ResultCodeEnum code, String message) {
        this.code = code;
        this.message = message;
    }
}

