/*
 * @Description: 用户异常处理类
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2022-01-03 23:13:18
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-03 23:13:18
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/handler/UserExceptionHandler.java
 */
package com.easytrade.easytradeapi.handler;

import com.easytrade.easytradeapi.constant.consists.Result;
import com.easytrade.easytradeapi.constant.enums.ResultCodeEnum;
import com.easytrade.easytradeapi.constant.exceptions.UserException;
import com.easytrade.easytradeapi.utils.ReturnResultUtil;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class UserExceptionHandler {
    /**
     * @description: 用户异常处理方法
     * @param {UserException} e 输入的异常
     * @return {Result} 结果信息
     */
    @ExceptionHandler(UserException.class)
    public Result UExceptionHandler(UserException e) {
        String message = e.getMessage();
        ResultCodeEnum code = e.getCode();

        return ReturnResultUtil.failure(code, message);
    }
}

