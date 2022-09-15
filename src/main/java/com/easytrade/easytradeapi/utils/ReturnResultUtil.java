/*
 * @Description: 返回结果工具类
 * @Author: Mo Xu
 * @Date: 2021-11-03 16:28:51
 * @LastEditors: Mo Xu
 * @LastEditTime: 2021-12-18 00:36:39
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/utils/ReturnResultUtil.java
 */
package com.easytrade.easytradeapi.utils;

import com.easytrade.easytradeapi.constant.consists.Result;
import com.easytrade.easytradeapi.constant.enums.ResultCodeEnum;
import org.springframework.stereotype.Component;

@Component
public class ReturnResultUtil {
    public static Result success() {
        return new Result().setResult(ResultCodeEnum.SUCCESS);
    }

    public static Result success(Object data) {
        return new Result().setResult(ResultCodeEnum.SUCCESS, data);
    }

    // Only failure need to use enum result code like 401, 404
    public static Result failure(ResultCodeEnum resultCode) {
        return new Result().setResult(resultCode);
    }

    public static Result failure(ResultCodeEnum resultCode, Object data) {
        return new Result().setResult(resultCode, data);
    }
}
