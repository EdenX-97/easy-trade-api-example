/*
 * @Description: 全局异常处理类
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2021-11-03 02:08:30
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-12 22:36:01
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/handler/GlobalExceptionHandler.java
 */
package com.easytrade.easytradeapi.handler;

import javax.validation.ConstraintViolationException;
import com.easytrade.easytradeapi.constant.consists.Result;
import com.easytrade.easytradeapi.constant.enums.ResultCodeEnum;
import com.easytrade.easytradeapi.utils.ReturnResultUtil;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * @description: 处理MissingServletRequestParameterException，该报错通常出现在前端传入参数有误，单个输入参数为null时会进入此异常处理
     * @param {MissingServletRequestParameterException} e 传入的报错信息
     * @return {Result} 结果信息
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Result NMissingParamException(MissingServletRequestParameterException e) {
        String message = e.getMessage();
        if (message.contains("not present")) { // 单个参数输入为null
            message = "Input cannot be null";
        }

        return ReturnResultUtil.failure(ResultCodeEnum.INVALID_PARAM, message);
    }

    /**
     * @description: 处理ConstraintViolationException，该报错出现在多个输入参数时，@Notnull注释的参数为null，或@NotBlank注释的参数为blank
     * @param {ConstraintViolationException} e 传入的报错信息
     * @return {Result} 结果信息
     */    
    @ExceptionHandler(ConstraintViolationException.class)
    public Result CVException(ConstraintViolationException e) {
        String message = e.getMessage();
        if (message.contains("blank")) { // 单个参数输入为null
            message = "Input cannot be blank";
        } else if (message.contains("null")) {
            message = "Input cannot be null";
        }

        return ReturnResultUtil.failure(ResultCodeEnum.INVALID_PARAM, message);
    }

    /**
     * @description: 此报错通常出现在，对传入的数据自动映射为类时，传入数据为null的情况
     * @param {HttpMessageNotReadableException} e 报错的类
     * @return {Result} 结果信息，包含报错信息
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result HMNotReadableException(HttpMessageNotReadableException e) {
        String message = e.getMessage();
        if (message.contains("register")) { 
            // 单个参数输入为null
            message = "Register cannot input null";
        }

        return ReturnResultUtil.failure(ResultCodeEnum.INVALID_PARAM, message);
    }

    /**
     * @description: 处理MethodArgumentNotValidException，出现于输入的参数内带有null但不允许为null的场景
     * @param {MethodArgumentNotValidException} e 传入的报错信息
     * @return {Result} 结果信息
     */    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result ValidatedException(MethodArgumentNotValidException e) {
        String message = e.getMessage();

        // 当用户输入的注册信息内包含不正确的信息
        if (message.contains("register")) {
            // 包含null信息
            if (message.contains("null")) {
                message = "Register information contains null";
            } else if (message.contains("Pattern")) {
                message = "Register information contains wrong format";
            }
        } else if (message.contains("bigFrame")) {
            // 大架号格式不正确
            message = "Input big frame number format wrong";
        } else if (message.contains("license")) {
            // 车牌号格式不正确
            message = "Input license number format wrong";
        }

        return ReturnResultUtil.failure(ResultCodeEnum.INVALID_PARAM, message);
    }

    /**
     * @description: 处理DuplicateKeyException，出现于数据库中已经存在了唯一的信息，但是想要再次添加的场景
     * @param {DuplicateKeyException} e 传入的报错信息
     * @return {Result} 结果信息
     */    
    @ExceptionHandler(DuplicateKeyException.class)
    public Result DKException(DuplicateKeyException e) {
        String message = e.getMessage();

        if (message.contains("email")) {
            // 当用户输入的email信息在数据库中存在，即该邮箱已被注册时报错
            message = "Register email exist";
        } else if (message.contains("phone")) {
            // 输入的手机号已存在
            message = "Register phone exist";
        } else if (message.contains("model")) {
            // 导入example car数据时model已存在
            message = "Input model exist";
        } else if (message.contains("bigFrame")) {
            // 大架号已存在
            message = "Input big frame exist";
        } else if (message.contains("license")) {
            // 车牌号已存在
            message = "Input license exist";
        }

        return ReturnResultUtil.failure(ResultCodeEnum.FAILED, message);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public Result MRHException(MissingRequestHeaderException e) {
        String message = e.getMessage();

        if (message.contains("Authorization")) {
            message = "No authorization";
        }

        return ReturnResultUtil.failure(ResultCodeEnum.NOT_AUTHORIZED, message);
    }
    
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result HRMSException(HttpRequestMethodNotSupportedException e) {
        String message = e.getMessage();

        if (message.contains("Request")) {
            message = "Request method incorrect";
        }

        return ReturnResultUtil.failure(ResultCodeEnum.FAILED, message);
    }
}
