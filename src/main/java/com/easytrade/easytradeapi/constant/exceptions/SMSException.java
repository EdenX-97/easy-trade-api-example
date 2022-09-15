/*
 * @Description: 自定义短信服务异常
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2022-01-04 02:46:07
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-04 02:47:41
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/exceptions/SMSException.java
 */
package com.easytrade.easytradeapi.constant.exceptions;

import com.easytrade.easytradeapi.constant.enums.ResultCodeEnum;
import lombok.Getter;


@Getter
public class SMSException extends RuntimeException {
    private ResultCodeEnum code;
    private String message;

    public SMSException(ResultCodeEnum code, String message) {
        this.code = code;
        this.message = message;
    }
}

