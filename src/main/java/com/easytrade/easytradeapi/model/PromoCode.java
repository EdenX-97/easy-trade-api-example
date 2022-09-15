/*
 * @Description: 优惠码类
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2022-01-09 21:53:34
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-10 19:23:12
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/model/PromoCode.java
 */
package com.easytrade.easytradeapi.model;

import java.util.Date;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;
import lombok.Data;
import lombok.ToString;


@Document(collection = "promoCodes")
@Data
@ToString
public class PromoCode {
    // 优惠码代码，六位大写字母+数字组成
    @MongoId
    private String code;

    // 添加的免费标准广告次数
    private long freeAdNums;

    // 广告的创建时间
    private Date createDate;

    // 广告的过期时间
    private Date expireDate;

    // 被使用的次数，默认为0次
    private long usedTimes = 0;

    // 最大使用次数，如果为-1即无限
    private long maxUseTimes;

    // 适用于的用户类型
    private String suitableUserStatus;
}
