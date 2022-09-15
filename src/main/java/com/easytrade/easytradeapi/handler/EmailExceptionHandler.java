/*
 * @Description: 邮件异常处理类
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2022-01-03 22:18:16
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-04 01:28:15
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/handler/EmailExceptionHandler.java
 */
package com.easytrade.easytradeapi.handler;

import com.easytrade.easytradeapi.constant.consists.Result;
import com.easytrade.easytradeapi.constant.enums.ResultCodeEnum;
import com.easytrade.easytradeapi.constant.exceptions.EmailException;
import com.easytrade.easytradeapi.utils.ReturnResultUtil;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class EmailExceptionHandler {
    /**
     * @description: 邮件异常处理方法
     * @param {EmailException} e 输入的异常
     * @return {Result} 结果信息
     */
    @ExceptionHandler(EmailException.class)
    public Result EExceptionHandler(EmailException e) {
        String message = e.getMessage();
        ResultCodeEnum code = e.getCode();

        return ReturnResultUtil.failure(code, message);
    }
}

