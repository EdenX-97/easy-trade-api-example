/**
 * @author: Hongzhang Liu
 * @description 崭新摩托车广告
 * @date 30/6/2022 12:14 am
 */
package com.easytrade.easytradeapi.model;

import com.easytrade.easytradeapi.constant.consists.VehicleAd;
import com.easytrade.easytradeapi.constant.enums.MotorPriceLevelEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Document(collection = "newMotorAds")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class NewMotorAd extends VehicleAd {
    // 价格区间
    @NotNull
    private MotorPriceLevelEnum priceLevel;

    // 大架号
    @NotNull
    @Indexed(name = "bigFrame", unique = true)
    @Pattern(regexp = "^[A-HJ-NPR-Z0-9]{8}[X0-9][A-HJ-NPR-Z0-9]{3}[0-9]{5}$") // 大架号正则
    private String bigFrame;
}
