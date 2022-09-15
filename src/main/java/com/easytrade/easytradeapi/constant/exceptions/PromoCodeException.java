/*
 * @Description: 优惠码异常类
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2022-01-10 17:43:33
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-10 17:43:33
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/exceptions/PromoCodeException.java
 */

package com.easytrade.easytradeapi.constant.exceptions;

import com.easytrade.easytradeapi.constant.enums.ResultCodeEnum;
import lombok.Getter;


@Getter
public class PromoCodeException extends RuntimeException {
    private ResultCodeEnum code;
    private String message;

    public PromoCodeException(ResultCodeEnum code, String message) {
        this.code = code;
        this.message = message;
    }
}
