/*
 * @Description: 新车异常处理类
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2022-01-03 22:30:33
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-04 01:27:45
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/handler/NewCarExceptionHandler.java
 */
package com.easytrade.easytradeapi.handler;

import com.easytrade.easytradeapi.constant.consists.Result;
import com.easytrade.easytradeapi.constant.enums.ResultCodeEnum;
import com.easytrade.easytradeapi.constant.exceptions.NewCarException;
import com.easytrade.easytradeapi.utils.ReturnResultUtil;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class NewCarExceptionHandler {
    /**
     * @description: 新车异常处理方法
     * @param {NewCarException} e 输入的异常
     * @return {Result} 结果信息
     */
    @ExceptionHandler(NewCarException.class)
    public Result NCExceptionHandler(NewCarException e) {
        String message = e.getMessage();
        ResultCodeEnum code = e.getCode();

        return ReturnResultUtil.failure(code, message);
    }
}

