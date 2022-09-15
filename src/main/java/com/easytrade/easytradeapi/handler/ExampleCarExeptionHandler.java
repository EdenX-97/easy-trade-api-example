/*
 * @Description: 汽车模版异常处理类
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2022-01-03 22:19:49
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-04 01:28:19
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/handler/ExampleCarExeptionHandler.java
 */
package com.easytrade.easytradeapi.handler;

import com.easytrade.easytradeapi.constant.consists.Result;
import com.easytrade.easytradeapi.constant.enums.ResultCodeEnum;
import com.easytrade.easytradeapi.constant.exceptions.ExampleCarException;
import com.easytrade.easytradeapi.utils.ReturnResultUtil;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class ExampleCarExeptionHandler {
    /**
     * @description: 汽车模版异常处理方法
     * @param {ExampleCarException} e 输入的异常
     * @return {Result} 结果信息
     */
    @ExceptionHandler(ExampleCarException.class)
    public Result ECExceptionHandler(ExampleCarException e) {
        String message = e.getMessage();
        ResultCodeEnum code = e.getCode();

        return ReturnResultUtil.failure(code, message);
    }
}

