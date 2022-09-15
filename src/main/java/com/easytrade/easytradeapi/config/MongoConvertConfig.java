/**
 * @author: Hongzhang Liu
 * @description 将BigDecimal转换为Decimal128的配置类
 * @date 8/4/2022 3:04 pm
 */
package com.easytrade.easytradeapi.config;

import com.easytrade.easytradeapi.utils.BigDecimalToDecimal128ConverterUtil;
import com.easytrade.easytradeapi.utils.Decimal128ToBigDecimalConverterUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import java.util.ArrayList;
import java.util.List;

// mongoCustomConversions会由spring进行管理, 按照加入的转换器,在数据库读写时对数据类型进行转换
@Configuration
public class MongoConvertConfig {
    @Bean
    public MongoCustomConversions mongoCustomConversions() {
        List<Converter<?, ?>> converterList = new ArrayList<>();
        converterList.add(new BigDecimalToDecimal128ConverterUtil());
        converterList.add(new Decimal128ToBigDecimalConverterUtil());
        return new MongoCustomConversions(converterList);
    }
}
