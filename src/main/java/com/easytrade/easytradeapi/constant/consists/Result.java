/*
 * @Description: 返回结果Result的类
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2021-11-03 04:11:40
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-14 17:21:17
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/constant/consists/Result.java
 */
package com.easytrade.easytradeapi.constant.consists;

import java.io.Serializable;
import com.easytrade.easytradeapi.constant.enums.ResultCodeEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;


@Getter
public class Result<T> implements Serializable {
    // 序列化的版本UID
    private static final long serialVersionUID = 6308315887056661996L;
    // 结果代码，具体参照enums/ResultCodeEnum下的枚举类
    private Integer code;
    // 返回结果中的附带信息，此信息在枚举类中设置
    private String message;
    // 返回结果中的附带数据
    private T data;

    /**
     * @description: 返回只有结果代码的结果
     * @param {ResultCodeEnum} resultCode 结果代码，参考ResultCodeEnum枚举类
     * @return {Result} 返回结果对象，包含结果代码、结果信息
     */
    public Result setResult(ResultCodeEnum resultCode) {
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
        return this;
    }

    /**
     * @description: 返回带有结果代码和数据的结果
     * @param {ResultCodeEnum} resultCode 结果代码，参考ResultCodeEnum枚举类
     * @param {T} data 结果数据，任意类型的数据
     * @return {Result} 返回结果对象，包含结果代码、结果信息、结果数据
     */
    public Result setResult(ResultCodeEnum resultCode, T data) {
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
        this.data = data;
        return this;
    }

    /**
     * @description: 返回Result的String类型信息
     * @param {*}
     * @return {String} String类型的信息
     */
    public String toString() {
        return new ObjectMapper().valueToTree(this).toString();
    }
}
