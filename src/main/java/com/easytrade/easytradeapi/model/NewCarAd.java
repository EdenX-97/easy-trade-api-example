/*
 * @Description: 新车广告位
 * @Author: Mo Xu
 * @Date: 2021-11-14 02:53:04
 * @LastEditors: Mo Xu
 * @LastEditTime: 2022-01-02 02:30:39
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/model/NewCarAd.java
 */
package com.easytrade.easytradeapi.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.easytrade.easytradeapi.constant.consists.VehicleAd;
import com.easytrade.easytradeapi.constant.enums.CarPriceLevelEnum;
import com.easytrade.easytradeapi.constant.enums.GearboxTypeEnum;
import com.easytrade.easytradeapi.constant.enums.MotorPriceLevelEnum;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Document(collection = "newCarAds")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class NewCarAd extends VehicleAd {
    // 车型名称
    @NotNull
    private String model;

    // 变速箱模式 (手动/自动)
    @NotNull
    private GearboxTypeEnum gearbox;

    // 价格区间
    @NotNull
    private CarPriceLevelEnum priceLevel;

    // 大架号
    @NotNull
    @Indexed(name = "bigFrame", unique = true)
    @Pattern(regexp = "^[A-HJ-NPR-Z0-9]{8}[X0-9][A-HJ-NPR-Z0-9]{3}[0-9]{5}$") // 大架号正则
    private String bigFrame;

    //// 图片地址
    //private String imgURL;
    //
    //// 新旧车标志位
    //private String oldNew = "新车";
}
