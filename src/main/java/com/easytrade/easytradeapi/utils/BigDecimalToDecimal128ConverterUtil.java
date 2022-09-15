/**
 * @author: Hongzhang Liu
 * @description 将bigdecimal转换为mongodb中的decimal128
 * @date 8/4/2022 3:02 pm
 */
package com.easytrade.easytradeapi.utils;

import org.bson.types.Decimal128;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

import java.math.BigDecimal;

//告诉spring往数据库写的时候用这个转换
@WritingConverter
public class BigDecimalToDecimal128ConverterUtil implements Converter<BigDecimal, Decimal128> {
    @Override
    public Decimal128 convert(BigDecimal bigDecimal) {
        return new Decimal128(bigDecimal);
    }
}
