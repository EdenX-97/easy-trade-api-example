package com.easytrade.easytradeapi.constant.enums;

import lombok.Getter;

@Getter
public enum MotorPriceLevelEnum {
    ZEROTOFIVE(0, 4999), // 0-4999
    FIVETOTEN(5000, 9999), // 5000-9999
    TENTOFIFTY(10000, 49999), // 1-4.9999万
    FIFTYTOTEN(50000, 99999), // 5-9.9999万
    TENTOTWENTY(100000, 199999), // 10-19.9999万
    TWENTYTOMORE(200000, 2000000000); // 20万以上

    private long minPrice;
    private long maxPrice;

    MotorPriceLevelEnum(long minPrice, long maxPrice) {
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
    }
}
