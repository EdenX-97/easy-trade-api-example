/*
 * @Description: 地区信息的类
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2022-01-02 20:43:50
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-11 22:22:38
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/model/Area.java
 */
package com.easytrade.easytradeapi.model;

import javax.validation.constraints.NotNull;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;
import lombok.Data;


@Document(collection = "areas")
@Data
public class Area {
    // 地区的代码作为id
    @MongoId
    private long code;

    // 省
    @NotNull
    private String province;

    // 市
    @NotNull
    private String city;

    // 县
    @NotNull
    private String county;

    // 镇
    @NotNull
    private String town;
}
