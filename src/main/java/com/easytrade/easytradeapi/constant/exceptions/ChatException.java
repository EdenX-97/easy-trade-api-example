/*
 * @Description: 自定义聊天异常
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2022-01-11 20:19:31
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-11 20:19:31
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/exceptions/ChatException.java
 */
package com.easytrade.easytradeapi.constant.exceptions;

import com.easytrade.easytradeapi.constant.enums.ResultCodeEnum;
import lombok.Getter;


@Getter
public class ChatException extends RuntimeException {
    private ResultCodeEnum code;
    private String message;

    public ChatException(ResultCodeEnum code, String message) {
        this.code = code;
        this.message = message;
    }
}

