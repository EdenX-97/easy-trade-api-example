/*
 * @Description: 聊天异常处理类
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2022-01-11 20:20:18
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-11 20:44:25
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/handler/ChatExceptionHandler.java
 */
package com.easytrade.easytradeapi.handler;

import com.easytrade.easytradeapi.constant.consists.Result;
import com.easytrade.easytradeapi.constant.enums.ResultCodeEnum;
import com.easytrade.easytradeapi.constant.exceptions.ChatException;
import com.easytrade.easytradeapi.utils.ReturnResultUtil;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class ChatExceptionHandler {
    @ExceptionHandler(ChatException.class)
    public Result CExceptionHandler(ChatException e) {
        String message = e.getMessage();
        ResultCodeEnum code = e.getCode();

        return ReturnResultUtil.failure(code, message);
    }
}

