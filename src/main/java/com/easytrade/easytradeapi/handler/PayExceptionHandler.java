/*
 * @Description: 支付异常处理类
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2022-01-18 01:44:56
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-18 01:45:59
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/handler/PayExceptionHandler.java
 */
package com.easytrade.easytradeapi.handler;

import com.easytrade.easytradeapi.constant.consists.Result;
import com.easytrade.easytradeapi.constant.enums.ResultCodeEnum;
import com.easytrade.easytradeapi.constant.exceptions.PayException;
import com.easytrade.easytradeapi.utils.ReturnResultUtil;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class PayExceptionHandler {
    /**
     * @description: 支付异常处理方法
     * @param {PayException} e 输入的异常
     * @return {Result} 结果信息
     */
    @ExceptionHandler(PayException.class)
    public Result PExceptionHandler(PayException e) {
        String message = e.getMessage();
        ResultCodeEnum code = e.getCode();

        return ReturnResultUtil.failure(code, message);
    }
}
