/*
 * @Description: 付款记录类
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2022-01-18 20:02:19
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-19 01:29:37
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/model/TradeRecord.java
 */
package com.easytrade.easytradeapi.model;

import java.math.BigDecimal;
import java.util.Date;
import com.easytrade.easytradeapi.constant.enums.TradeStatusEnum;
import com.easytrade.easytradeapi.constant.enums.TradeTypeEnum;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;
import lombok.Data;


@Document(collection = "tradeRecords")
@Data
public class TradeRecord {
    @MongoId
    private ObjectId id;

    // 订单号
    private String tradeNo;

    // 交易类型
    private TradeTypeEnum type;

    // 交易价格
    private BigDecimal price;

    // 交易创建时间
    private Date createDate;

    // 拥有者id
    private String ownerAccount;

    // 对应广告id
    private ObjectId adId;

    // 交易状态
    private TradeStatusEnum status;
}
