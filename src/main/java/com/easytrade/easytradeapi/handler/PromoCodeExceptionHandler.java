/*
 * @Description: 优惠码异常处理类
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2022-01-10 17:44:51
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-19 02:49:38
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/handler/PromoCodeExceptionHandler.java
 */
package com.easytrade.easytradeapi.handler;

import com.easytrade.easytradeapi.constant.consists.Result;
import com.easytrade.easytradeapi.constant.enums.ResultCodeEnum;
import com.easytrade.easytradeapi.constant.exceptions.PromoCodeException;
import com.easytrade.easytradeapi.utils.ReturnResultUtil;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class PromoCodeExceptionHandler {
    /**
     * @description: 优惠码异常处理方法
     * @param {PromoCodeException} e 输入的异常
     * @return {Result} 结果信息
     */
    @ExceptionHandler(PromoCodeException.class)
    public Result PCExceptionHandler(PromoCodeException e) {
        String message = e.getMessage();
        ResultCodeEnum code = e.getCode();

        return ReturnResultUtil.failure(code, message);
    }
}
