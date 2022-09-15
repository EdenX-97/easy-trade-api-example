/**
 * @author: Hongzhang Liu
 * @description 交易记录接口的实现类
 * @date 7/4/2022 5:41 pm
 */
package com.easytrade.easytradeapi.service.impl;

import com.easytrade.easytradeapi.constant.enums.ResultCodeEnum;
import com.easytrade.easytradeapi.constant.enums.TradeStatusEnum;
import com.easytrade.easytradeapi.constant.enums.TradeTypeEnum;
import com.easytrade.easytradeapi.constant.exceptions.TradeRecordException;
import com.easytrade.easytradeapi.model.TradeRecord;
import com.easytrade.easytradeapi.repository.NewCarAdRepository;
import com.easytrade.easytradeapi.repository.SecondHandCarAdRepository;
import com.easytrade.easytradeapi.repository.TradeRecordRepository;
import com.easytrade.easytradeapi.repository.UserRepository;
import com.easytrade.easytradeapi.service.intf.TradeRecordService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class TradeRecordServiceImpl implements TradeRecordService {

    @Autowired
    TradeRecordRepository tradeRecordRepository;

    @Autowired
    NewCarAdRepository newCarAdRepository;

    @Autowired
    SecondHandCarAdRepository secondHandCarAdRepository;

    @Autowired
    UserRepository userRepository;

    /**
     * @description: 根据交易单号找到交易记录
     * @param {String} tradeNo 交易单号
     * @return {TradeRecord} 一条交易记录
     */
    @Override
    public TradeRecord getOneTradeRecordByTradeNo(String tradeNo) {
        if(!tradeRecordRepository.existsByTradeNo(tradeNo)){
            throw new TradeRecordException(ResultCodeEnum.NOT_FOUND, "this trade number is not in the database");
        }
        return tradeRecordRepository.findOneByTradeNo(tradeNo);
    }

    /**
     * @description: 根据交易类型获得交易记录集合
     * @param {TradeTypeEnum}tte 交易类型
     * @return {List<TradeRecord>} 交易记录集合
     */
    @Override
    public List<TradeRecord> getAllTradeRecordByType(TradeTypeEnum tte) {
        if(!tradeRecordRepository.existsAllByType(tte)){
            throw new TradeRecordException(ResultCodeEnum.NOT_FOUND, "this trade type is not in the database");
        }
        return tradeRecordRepository.findAllByType(tte);
    }

    /**
     * @description: 根据价格区间获得交易记录集合
     * @param {BigDecimal} price1 起始价格
     * @param {BigDecimal} price2 终止价格
     * @return {List<TradeRecord>} 交易记录集合
     */
    @Override
    public List<TradeRecord> getAllTradeRecordByPriceBetween(BigDecimal price1, BigDecimal price2) {
        if(price1.doubleValue() > price2.doubleValue()){
            throw new TradeRecordException(ResultCodeEnum.INVALID_PARAM, "price1 has to be greater than price2");
        }
        if(price1.doubleValue() < 0 || price2.doubleValue() < 0){
            throw new TradeRecordException(ResultCodeEnum.INVALID_PARAM, "the input price can not be negtive");
        }
        return tradeRecordRepository.findAllByPriceBetween(price1, price2);
    }

    /**
     * @description: 根据创建日期区间获得交易记录集合
     * @param {Date} date1 起始日期
     * @param {Date} date2 终止日期
     * @return {List<TradeRecord>} 交易记录集合
     */
    @Override
    public List<TradeRecord> getAllTradeRecordByCreateDateBetween(String date1, String date2) {
        // 设置Date类型格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date input1;
        Date input2;
        try {
            // 将字符串按照指定格式转变为Date类型
            input1 = sdf.parse(date1);
            input2 = sdf.parse(date2);
            return tradeRecordRepository.findAllByCreateDateBetween(input1, input2);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    /**
     * @description: 根据用户账号获得交易记录集合
     * @param {String} account 用户账号
     * @return {List<TradeRecord>} 交易记录集合
     */
    @Override
    public List<TradeRecord> getAllTradeRecordByOwnerAccount(String account) {
        if(!userRepository.existsByPhone(account)){
            throw new TradeRecordException(ResultCodeEnum.NOT_FOUND, "the account is not in the database");
        }
        if(!tradeRecordRepository.existsAllByOwnerAccount(account)){
            throw new TradeRecordException(ResultCodeEnum.NOT_FOUND, "this account do not have trade record");
        }
        return tradeRecordRepository.findAllByOwnerAccount(account);
    }

    /**
     * @description: 根据广告id获得交易记录集合
     * @param {ObjectId} id 广告id
     * @return {List<TradeRecord>} 交易记录集合
     */
    @Override
    public List<TradeRecord> getAllTradeRecordByAdId(ObjectId id) {
        if(!newCarAdRepository.existsById(id) && !secondHandCarAdRepository.existsById(id)){
            throw new TradeRecordException(ResultCodeEnum.NOT_FOUND, "this id is not in the database");
        }
        if(!tradeRecordRepository.existsAllByAdId(id)){
            throw new TradeRecordException(ResultCodeEnum.NOT_FOUND, "there is no record of this AD id");
        }
        return tradeRecordRepository.findAllByAdId(id);
    }

    @Override
    public void addTradeRecord(String tradeNo, TradeTypeEnum tte, BigDecimal price, String createDate, String ownerAccount, ObjectId adId, TradeStatusEnum tse) {
        // 检测交易单号，交易类型，交易价格，创建日期，拥有者账号，广告id和交易状态输入的合法性
        if(!tte.equals(TradeTypeEnum.ALIPAY) && !tte.equals(TradeTypeEnum.WECHATPAY)){
            throw new TradeRecordException(ResultCodeEnum.INVALID_PARAM, "tte must be one of ALIPAY or WECHATPAY");
        }
        if(price.doubleValue() < 0){
            throw new TradeRecordException(ResultCodeEnum.INVALID_PARAM, "the input price can not be negtive");
        }
        if(!userRepository.existsByPhone(ownerAccount)){
            throw new TradeRecordException(ResultCodeEnum.NOT_FOUND, "this account is not in the database");
        }
        if(!newCarAdRepository.existsById(adId) && !secondHandCarAdRepository.existsById(adId)){
            throw new TradeRecordException(ResultCodeEnum.NOT_FOUND, "this adId is not in the database");
        }
        if(!tse.equals(TradeStatusEnum.FREE) && !tse.equals(TradeStatusEnum.PAYING) && !tse.equals(TradeStatusEnum.PAID) && !tse.equals(TradeStatusEnum.CLOSED) && !tse.equals(TradeStatusEnum.REFUNDED) &&  !tse.equals(TradeStatusEnum.REFUNDING)){
            throw new TradeRecordException(ResultCodeEnum.INVALID_PARAM, "tte must be one of FREE, PAYING, PAID, CLOSED, REFUNDED or REFUNDING");
        }
        TradeRecord tradeRecord = new TradeRecord();
        tradeRecord.setTradeNo(tradeNo);
        tradeRecord.setType(tte);
        tradeRecord.setPrice(price);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date newCreateDate = sdf.parse(createDate);
            tradeRecord.setCreateDate(newCreateDate);
            tradeRecord.setOwnerAccount(ownerAccount);
            tradeRecord.setAdId(adId);
            tradeRecord.setStatus(tse);
            tradeRecordRepository.save(tradeRecord);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteTradeRecordById(ObjectId id) {
        // 检测交易记录输入的合法性
        if(!tradeRecordRepository.existsById(id)){
            throw new TradeRecordException(ResultCodeEnum.NOT_FOUND, "this trade record id is not in the database");
        }
        tradeRecordRepository.deleteById(id);
    }

    @Override
    public void updateTradeRecordById(ObjectId id, String tradeNo, TradeTypeEnum tte, BigDecimal price, String createDate, String ownerAccount, ObjectId adId, TradeStatusEnum tse) {
        // 检测交易记录id，交易单号，交易类型，交易价格，创建日期，拥有者账号，广告id和交易状态输入的合法性
        if(!tradeRecordRepository.existsById(id)){
            throw new TradeRecordException(ResultCodeEnum.NOT_FOUND, "this trade record id is not in the database");
        }
        if(!tte.equals(TradeTypeEnum.ALIPAY) && !tte.equals(TradeTypeEnum.WECHATPAY)){
            throw new TradeRecordException(ResultCodeEnum.INVALID_PARAM, "tte must be one of ALIPAY or WECHATPAY");
        }
        if(price.doubleValue() < 0){
            throw new TradeRecordException(ResultCodeEnum.INVALID_PARAM, "the input price can not be negtive");
        }
        if(!userRepository.existsByPhone(ownerAccount)){
            throw new TradeRecordException(ResultCodeEnum.NOT_FOUND, "this account is not in the database");
        }
        if(!newCarAdRepository.existsById(adId) && !secondHandCarAdRepository.existsById(adId)){
            throw new TradeRecordException(ResultCodeEnum.NOT_FOUND, "this adId is not in the database");
        }
        if(!tse.equals(TradeStatusEnum.FREE) && !tse.equals(TradeStatusEnum.PAYING) && !tse.equals(TradeStatusEnum.PAID) && !tse.equals(TradeStatusEnum.CLOSED) && !tse.equals(TradeStatusEnum.REFUNDED) &&  !tse.equals(TradeStatusEnum.REFUNDING)){
            throw new TradeRecordException(ResultCodeEnum.INVALID_PARAM, "tte must be one of FREE, PAYING, PAID, CLOSED, REFUNDED or REFUNDING");
        }
        TradeRecord tradeRecord = tradeRecordRepository.findOneById(id);
        tradeRecord.setTradeNo(tradeNo);
        tradeRecord.setType(tte);
        tradeRecord.setPrice(price);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date newCreateDate = sdf.parse(createDate);
            tradeRecord.setCreateDate(newCreateDate);
            tradeRecord.setOwnerAccount(ownerAccount);
            tradeRecord.setAdId(adId);
            tradeRecord.setStatus(tse);
            tradeRecordRepository.save(tradeRecord);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
