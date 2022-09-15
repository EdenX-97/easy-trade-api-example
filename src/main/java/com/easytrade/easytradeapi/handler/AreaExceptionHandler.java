/*
 * @Description: 地区异常处理类
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2022-01-03 20:44:09
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-04 01:26:59
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/handler/AreaExceptionHandler.java
 */
package com.easytrade.easytradeapi.handler;

import com.easytrade.easytradeapi.constant.consists.Result;
import com.easytrade.easytradeapi.constant.enums.ResultCodeEnum;
import com.easytrade.easytradeapi.constant.exceptions.AreaException;
import com.easytrade.easytradeapi.utils.ReturnResultUtil;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class AreaExceptionHandler {
    /**
     * @description: 地区异常处理方法
     * @param {AreaException} e 输入的异常
     * @return {Result} 结果信息
     */    
    @ExceptionHandler(AreaException.class)
    public Result AExceptionHandler(AreaException e) {
        String message = e.getMessage();
        ResultCodeEnum code = e.getCode();

        return ReturnResultUtil.failure(code, message);
    }
}

