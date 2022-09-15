/*
 * @Description: 
 * @Author: Mo Xu
 * @Date: 2022-01-18 20:16:30
 * @LastEditors: Mo Xu
 * @LastEditTime: 2022-01-19 01:32:45
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/repository/TradeRecordRepository.java
 */
package com.easytrade.easytradeapi.repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import com.easytrade.easytradeapi.constant.enums.TradeStatusEnum;
import com.easytrade.easytradeapi.constant.enums.TradeTypeEnum;
import com.easytrade.easytradeapi.model.TradeRecord;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface TradeRecordRepository extends MongoRepository<TradeRecord, ObjectId> {
    // /**
    //  * @description: 根据id来寻找交易记录
    //  * @param {ObjectId} ownerId 拥有者id
    //  * @return {TradeRecord} 返回对应的交易记录
    //  */    
    // public TradeRecord findAllByOwnerId(ObjectId ownerId);

    /**
     * @description: 根据广告id找到对应的交易记录
     * @param {ObjectId} adId 输入的广告id
     * @return {TradeRecord} 对应的交易记录
     */    
    public TradeRecord findOneByAdId(ObjectId adId);

    /**
     * @description: 根据交易状态搜索对应交易记录
     * @param {TradeStatusEnum} status 交易状态
     * @return {List<TradeRecord>} 所有相应的记录
     */    
    public List<TradeRecord> findAllByStatus(TradeStatusEnum status);

    /**
     * @description: 根据交易单号找到交易记录
     * @param {String} tradeNo 交易单号
     * @return {TradeRecord} 一条交易记录
     */
    public TradeRecord findOneByTradeNo(String tradeNo);

    /**
     * @description: 根据交易类型获得交易记录集合
     * @param {TradeTypeEnum}tte 交易类型
     * @return {List<TradeRecord>} 交易记录集合
     */
    public List<TradeRecord> findAllByType(TradeTypeEnum tte);

    /**
     * @description: 根据价格区间获得交易记录集合
     * @param {BigDecimal} price1 起始价格
     * @param {BigDecimal} price2 终止价格
     * @return {List<TradeRecord>} 交易记录集合
     */
    public List<TradeRecord> findAllByPriceBetween(BigDecimal price1, BigDecimal price2);

    /**
     * @description: 根据创建日期区间获得交易记录集合
     * @param {Date} date1 起始日期
     * @param {Date} date2 终止日期
     * @return {List<TradeRecord>} 交易记录集合
     */
    public List<TradeRecord> findAllByCreateDateBetween(Date date1, Date date2);

    /**
     * @description: 根据用户账号获得交易记录集合
     * @param {String} account 用户账号
     * @return {List<TradeRecord>} 交易记录集合
     */
    public List<TradeRecord> findAllByOwnerAccount(String account);

    /**
     * @description: 根据广告id获得交易记录集合
     * @param {ObjectId} id 广告id
     * @return {List<TradeRecord>} 交易记录集合
     */
    public List<TradeRecord> findAllByAdId(ObjectId id);

    /**
     * @description: 根据交易单号判断是否存在交易记录
     * @param {String} tradeNo 交易单号
     * @return {Boolean} 真/假
     */
    public Boolean existsByTradeNo(String tradeNo);

    /**
     * @description: 根据交易类型判断是否存在交易记录
     * @param {TradeTypeEnum} tee
     * @return {Boolean} 真/假
     */
    public Boolean existsAllByType(TradeTypeEnum tee);

    /**
     * @description: 根据用户账号判断是否存在交易记录
     * @param {String} account
     * @return {Boolean} 真/假
     */
    public Boolean existsAllByOwnerAccount(String account);

    /**
     * @description: 根据广告id判断是否存在交易记录
     * @param {ObjectId} id
     * @return {Boolean} 真/假
     */
    public Boolean existsAllByAdId(ObjectId id);

    /**
     * 通过交易记录id找到一条交易记录
     *
     * @param id 交易记录id
     * @return {@link TradeRecord}
     */
    public TradeRecord findOneById(ObjectId id);

    /**
     * 根据订单号id和广告支付方式找到所有交易记录
     *
     * @param id 订单号
     * @param tte  tte
     * @return {@link List}<{@link TradeRecord}>
     */
    public List<TradeRecord> findAllByAdIdAndType(ObjectId id, TradeTypeEnum tte);

    /**
     * 根据订单号和状态找到一个订单
     *
     * @param tradeNo 订单号
     * @param tse     订单状态
     * @return {@link TradeRecord}
     */
    public TradeRecord findOneByTradeNoAndStatus(String tradeNo, TradeStatusEnum tse);
}
