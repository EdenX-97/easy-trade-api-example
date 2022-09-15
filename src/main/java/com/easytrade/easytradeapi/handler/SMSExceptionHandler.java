/*
 * @Description: 短信异常处理类
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2022-01-05 20:57:12
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-05 20:57:13
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/handler/SMSExceptionHandler.java
 */
package com.easytrade.easytradeapi.handler;

import com.easytrade.easytradeapi.constant.consists.Result;
import com.easytrade.easytradeapi.constant.enums.ResultCodeEnum;
import com.easytrade.easytradeapi.constant.exceptions.SMSException;
import com.easytrade.easytradeapi.utils.ReturnResultUtil;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class SMSExceptionHandler {
    /**
     * @description: 短信异常处理方法
     * @param {SMSException} e 输入的异常
     * @return {Result} 结果信息
     */
    @ExceptionHandler(SMSException.class)
    public Result SMSExceptionHandler(SMSException e) {
        String message = e.getMessage();
        ResultCodeEnum code = e.getCode();

        return ReturnResultUtil.failure(code, message);
    }
}

