/*
 * @Description: 自定义地区异常
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2022-01-03 20:13:32
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-03 20:17:55
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/exceptions/AreaException.java
 */
package com.easytrade.easytradeapi.constant.exceptions;

import com.easytrade.easytradeapi.constant.enums.ResultCodeEnum;
import lombok.Getter;


@Getter
public class ReportRecordException extends RuntimeException {
    private ResultCodeEnum code;
    private String message;

    public ReportRecordException(ResultCodeEnum code, String message) {
        this.code = code;
        this.message = message;
    }
}

