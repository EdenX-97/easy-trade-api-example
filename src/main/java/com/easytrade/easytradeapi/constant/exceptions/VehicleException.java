/*
 * @Description: 自定义载具异常
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2022-01-03 23:52:23
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-03 23:52:23
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/exceptions/VehicleException.java
 */
package com.easytrade.easytradeapi.constant.exceptions;

import com.easytrade.easytradeapi.constant.enums.ResultCodeEnum;
import lombok.Getter;


@Getter
public class VehicleException extends RuntimeException {
    private ResultCodeEnum code;
    private String message;

    public VehicleException(ResultCodeEnum code, String message) {
        this.code = code;
        this.message = message;
    }
}

