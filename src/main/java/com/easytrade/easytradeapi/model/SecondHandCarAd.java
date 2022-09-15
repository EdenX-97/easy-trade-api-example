/*
 * @Description: 二手车广告位
 * @Author: Mo Xu
 * @Date: 2021-11-03 02:40:23
 * @LastEditors: Mo Xu
 * @LastEditTime: 2022-01-07 21:24:54
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/model/SecondHandCarAd.java
 */
package com.easytrade.easytradeapi.model;

import java.util.Date;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import com.easytrade.easytradeapi.constant.consists.VehicleAd;
import com.easytrade.easytradeapi.constant.enums.CarPriceLevelEnum;
import com.easytrade.easytradeapi.constant.enums.GearboxTypeEnum;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Document(collection = "secondHandCarAds")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SecondHandCarAd extends VehicleAd {
    // 购买日期
    @NotNull
    private Date purchaseDate;

    // 生产日期
    //@NotNull
    private Date productionDate;

    // 牌照号
    @Pattern(regexp = "^([京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领A-Z]{1}[a-zA-Z](([DF]((?![IO])[a-zA-Z0-9](?![IO]))[0-9]{4})|([0-9]{5}[DF]))|[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领A-Z]{1}[A-Z]{1}[A-Z0-9]{4}[A-Z0-9挂学警港澳]{1})$") // 车牌正则，包含普通车牌和新能源车牌
    private String license;

    // 大架号
    @NotNull
    @Indexed(name = "bigFrame", unique = true)
    @Pattern(regexp = "^[A-HJ-NPR-Z0-9]{8}[X0-9][A-HJ-NPR-Z0-9]{3}[0-9]{5}$") // 大架号正则
    private String bigFrame;

    // 价格区间
    @NotNull
    private CarPriceLevelEnum priceLevel;

    //// 图片地址
    //private String imgURL;

    //// 新旧标志位
    //private String oldNew = "二手车";
}
