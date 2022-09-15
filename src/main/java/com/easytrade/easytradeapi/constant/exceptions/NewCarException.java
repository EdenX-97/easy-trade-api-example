/*
 * @Description: 自定义新车异常类
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2022-01-03 22:28:22
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-03 22:29:32
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/exceptions/NewCarException.java
 */
package com.easytrade.easytradeapi.constant.exceptions;

import com.easytrade.easytradeapi.constant.enums.ResultCodeEnum;
import lombok.Getter;


@Getter
public class NewCarException extends RuntimeException {
    private ResultCodeEnum code;
    private String message;

    public NewCarException(ResultCodeEnum code, String message) {
        this.code = code;
        this.message = message;
    }
}

