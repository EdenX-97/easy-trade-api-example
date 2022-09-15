/**
 * @author: Hongzhang Liu
 * @description 二手摩托车广告
 * @date 30/6/2022 12:15 am
 */
package com.easytrade.easytradeapi.model;

import com.easytrade.easytradeapi.constant.consists.VehicleAd;
import com.easytrade.easytradeapi.constant.enums.CarPriceLevelEnum;
import com.easytrade.easytradeapi.constant.enums.MotorPriceLevelEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Date;

@Document(collection = "secondHandMotorAds")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SecondHandMotorAd extends VehicleAd {
    // 购买日期
    @NotNull
    private Date purchaseDate;

    // 生产日期
    //@NotNull
    private Date productionDate;

    // 大架号
    @NotNull
    @Indexed(name = "bigFrame", unique = true)
    @Pattern(regexp = "^[A-HJ-NPR-Z0-9]{8}[X0-9][A-HJ-NPR-Z0-9]{3}[0-9]{5}$") // 大架号正则
    private String bigFrame;

    // 牌照号
    @Pattern(regexp = "^([京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领A-Z]{1}[a-zA-Z](([DF]((?![IO])[a-zA-Z0-9](?![IO]))[0-9]{4})|([0-9]{5}[DF]))|[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领A-Z]{1}[A-Z]{1}[A-Z0-9]{4}[A-Z0-9挂学警港澳]{1})$") // 车牌正则，包含普通车牌和新能源车牌
    private String license;

    // 价格区间
    @NotNull
    private MotorPriceLevelEnum priceLevel;
}
