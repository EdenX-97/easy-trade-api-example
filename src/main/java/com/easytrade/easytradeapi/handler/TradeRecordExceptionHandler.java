/**
 * @author: Hongzhang Liu
 * @description 用于处理交易记录异常的类
 * @date 8/4/2022 1:20 pm
 */
package com.easytrade.easytradeapi.handler;

import com.easytrade.easytradeapi.constant.consists.Result;
import com.easytrade.easytradeapi.constant.enums.ResultCodeEnum;
import com.easytrade.easytradeapi.constant.exceptions.AreaException;
import com.easytrade.easytradeapi.utils.ReturnResultUtil;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class TradeRecordExceptionHandler {
    /**
     * @description: 交易记录异常处理
     * @param {AreaException} e 输入的异常
     * @return {Result} 结果信息
     */
    @ExceptionHandler(AreaException.class)
    public Result AExceptionHandler(AreaException e) {
        String message = e.getMessage();
        ResultCodeEnum code = e.getCode();

        return ReturnResultUtil.failure(code, message);
    }
}
