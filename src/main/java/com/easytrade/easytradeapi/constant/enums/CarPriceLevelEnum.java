/*
 * @Description: 价格区间枚举类
 * @Author: Mo Xu
 * @Date: 2021-12-28 22:34:26
 * @LastEditors: Mo Xu
 * @LastEditTime: 2022-01-18 01:22:08
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/constant/enums/PriceLevelEnum.java
 */
package com.easytrade.easytradeapi.constant.enums;

import lombok.Getter;


@Getter
public enum CarPriceLevelEnum {
    ZEROTOFIVE(0, 49999), // 0-4.9999万
    FIVETOTEN(50000, 99999), // 5-9.9999万
    TENTOTWENTY(100000, 199999), // 10-19.9999万
    TWENTYTOFORTY(200000, 399999), // 20-39.9999万
    FORTYTOEIGHTY(400000, 799999), // 40-79.9999万
    EIGHTYTOONEHUNDUREDFORTY(800000, 1399999), // 80-139.9999万
    ONEHUNDUREDFORTYTOMORE(1400000, 2000000000); // 140万以上

    private long minPrice;
    private long maxPrice;

    CarPriceLevelEnum(long minPrice, long maxPrice) {
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
    }
}

