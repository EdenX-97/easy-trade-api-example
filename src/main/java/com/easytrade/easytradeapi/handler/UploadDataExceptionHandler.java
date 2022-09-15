/*
 * @Description: 汽车样本报错处理类
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2021-12-26 02:24:16
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-04 01:57:26
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/handler/UploadDataExceptionHandler.java
 * /EasyBuyCar/src/main/java/com/jiandanmaiche/api/handler/ExampleCarExceptionHandler.java
 */
package com.easytrade.easytradeapi.handler;

import java.io.IOException;
import com.easytrade.easytradeapi.constant.consists.Result;
import com.easytrade.easytradeapi.constant.enums.ResultCodeEnum;
import com.easytrade.easytradeapi.utils.ReturnResultUtil;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class UploadDataExceptionHandler {
    /**
     * @description: 上传excel数据的文件相关IO报错
     * @param {IOException} e 传入的报错信息
     * @return {Result} 结果信息
     */
    @ExceptionHandler(IOException.class)
    public Result UploadExampleCarIOException(IOException e) {
        String message = e.getMessage();

        message = "Upload data failed: " + message;

        return ReturnResultUtil.failure(ResultCodeEnum.FAILED, message);
    }
}

