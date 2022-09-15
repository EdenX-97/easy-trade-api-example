/**
 * @author: Hongzhang Liu
 * @description 将Decimal128转换为BigDecimal
 * @date 8/4/2022 3:05 pm
 */
package com.easytrade.easytradeapi.utils;

import org.bson.types.Decimal128;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.math.BigDecimal;

//告诉spring从数据库读的时候用这个转换
@ReadingConverter
public class Decimal128ToBigDecimalConverterUtil implements Converter<Decimal128, BigDecimal> {
    @Override
    public BigDecimal convert(Decimal128 decimal128) {
        return decimal128.bigDecimalValue();
    }
}
