/*
 * @Description: 载具报错处理类
 * @Author: Mo Xu
 * @Date: 2021-12-21 20:41:57
 * @LastEditors: Mo Xu
 * @LastEditTime: 2022-01-04 01:28:04
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/handler/VehicleExceptionHandler.java
 */
package com.easytrade.easytradeapi.handler;

import com.easytrade.easytradeapi.constant.consists.Result;
import com.easytrade.easytradeapi.constant.enums.ResultCodeEnum;
import com.easytrade.easytradeapi.constant.exceptions.VehicleException;
import com.easytrade.easytradeapi.utils.ReturnResultUtil;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;


@RestControllerAdvice
public class VehicleExceptionHandler {
    /**
     * @description: 载具异常处理方法
     * @param {VehicleException} e 输入的异常
     * @return {Result} 结果信息
     */
    @ExceptionHandler(VehicleException.class)
    public Result VException(VehicleException e) {
        String message = e.getMessage();
        ResultCodeEnum code = e.getCode();

        return ReturnResultUtil.failure(code, message);
    }

    /**
     * @description: 输入的param不存在时的报错处理
     * @param {MethodArgumentTypeMismatchException} e 传入的报错信息
     * @return {Result} 结果信息
     */    
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Result MissingParamException(MethodArgumentTypeMismatchException e) {
        String message = e.getMessage();

        if (message.contains("VehicleAdTypeEnum")) { // 输入的载具广告类型不在枚举中
            message = "VehicleAdType do not exist";
        } else if (message.contains("ObjectId")) {
            message = "Id do not exist";
        }

        return ReturnResultUtil.failure(ResultCodeEnum.NOT_FOUND, message);
    }
    
    
}
