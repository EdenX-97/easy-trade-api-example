/*
 * @Description: 自定义JWT异常处理
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2021-12-16 21:16:27
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-04 01:56:50
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/handler/JWTExceptionHandler.java
 */
package com.easytrade.easytradeapi.handler;

import com.easytrade.easytradeapi.constant.consists.Result;
import com.easytrade.easytradeapi.constant.enums.ResultCodeEnum;
import com.easytrade.easytradeapi.constant.exceptions.JWTException;
import com.easytrade.easytradeapi.utils.ReturnResultUtil;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class JWTExceptionHandler {
    /**
     * @description: 包括Token为空以及Token过期的异常
     * @param {JWTException} e 传入的报错信息
     * @return {Result} 结果信息
     */    
    @ExceptionHandler(JWTException.class)
    public Result JWTException(JWTException e) {
        String message = e.getMessage();
        ResultCodeEnum code = e.getCode();

        return ReturnResultUtil.failure(code, message);
    }
}
