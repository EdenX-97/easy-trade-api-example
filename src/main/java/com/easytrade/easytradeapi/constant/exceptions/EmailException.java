/*
 * @Description: 自定义邮件异常
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2022-01-03 20:53:01
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-03 22:19:16
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/exceptions/EmailException.java
 */

package com.easytrade.easytradeapi.constant.exceptions;

import com.easytrade.easytradeapi.constant.enums.ResultCodeEnum;
import lombok.Getter;


@Getter
public class EmailException extends RuntimeException {
    private ResultCodeEnum code;
    private String message;

    public EmailException(ResultCodeEnum code, String message) {
        this.code = code;
        this.message = message;
    }
}
