/*
 * @Description: 二手车异常处理类
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2022-01-19 02:49:19
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-19 02:50:23
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/handler/SecondHandCarExceptionHandler.java
 * /EasyBuyCar/src/main/java/com/jiandanmaiche/api/handler/SecondHandCarExceptionHandler.java
 */
package com.easytrade.easytradeapi.handler;

import com.easytrade.easytradeapi.constant.consists.Result;
import com.easytrade.easytradeapi.constant.enums.ResultCodeEnum;
import com.easytrade.easytradeapi.constant.exceptions.SecondHandCarException;
import com.easytrade.easytradeapi.utils.ReturnResultUtil;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class SecondHandCarExceptionHandler {
    /**
     * @description: 二手车异常处理方法
     * @param {SecondHandCarException} e 输入的异常
     * @return {Result} 结果信息
     */
    @ExceptionHandler(SecondHandCarException.class)
    public Result SHCExceptionHandler(SecondHandCarException e) {
        String message = e.getMessage();
        ResultCodeEnum code = e.getCode();

        return ReturnResultUtil.failure(code, message);
    }
}

