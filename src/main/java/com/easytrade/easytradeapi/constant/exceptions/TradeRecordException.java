/**
 * @author: Hongzhang Liu
 * @description 自定义交易记录异常
 * @date 8/4/2022 1:21 pm
 */
package com.easytrade.easytradeapi.constant.exceptions;

import com.easytrade.easytradeapi.constant.enums.ResultCodeEnum;
import lombok.Getter;


@Getter
public class TradeRecordException extends RuntimeException {
    private ResultCodeEnum code;
    private String message;

    public TradeRecordException(ResultCodeEnum code, String message) {
        this.code = code;
        this.message = message;
    }
}
