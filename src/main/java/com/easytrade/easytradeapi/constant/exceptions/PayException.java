/*
 * @Description: 自定义支付异常
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2022-01-09 21:28:17
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-09 21:28:17
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/exceptions/PayException.java
 */

package com.easytrade.easytradeapi.constant.exceptions;

import com.easytrade.easytradeapi.constant.enums.ResultCodeEnum;
import lombok.Getter;


@Getter
public class PayException extends RuntimeException {
    private ResultCodeEnum code;
    private String message;

    public PayException(ResultCodeEnum code, String message) {
        this.code = code;
        this.message = message;
    }
}
