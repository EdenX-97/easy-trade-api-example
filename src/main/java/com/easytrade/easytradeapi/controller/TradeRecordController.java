/**
 * @author: Hongzhang Liu
 * @description 交易记录操作相关控制器
 * @date 7/4/2022 5:41 pm
 */
package com.easytrade.easytradeapi.controller;

import com.easytrade.easytradeapi.constant.consists.Result;
import com.easytrade.easytradeapi.constant.enums.TradeStatusEnum;
import com.easytrade.easytradeapi.constant.enums.TradeTypeEnum;
import com.easytrade.easytradeapi.model.TradeRecord;
import com.easytrade.easytradeapi.service.intf.TradeRecordService;
import com.easytrade.easytradeapi.utils.ReturnResultUtil;
import com.sun.istack.NotNull;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@Validated
public class TradeRecordController {

    @Autowired
    TradeRecordService tradeRecordService;

    /**
     * @description: 根据交易单号找到交易记录
     * @param {String} tradeNo 交易单号
     * @return {TradeRecord} 一条交易记录
     */
    @GetMapping("/tradeRecord/getOneTradeRecordByTradeNo")
    @Transactional
    public Result getOneTradeRecordByTradeNo(@RequestParam @NotNull String tradeNo){
        TradeRecord rs = tradeRecordService.getOneTradeRecordByTradeNo(tradeNo);
        return ReturnResultUtil.success(rs);
    }

    /**
     * @description: 根据交易类型获得交易记录集合
     * @param {TradeTypeEnum}tte 交易类型
     * @return {List<TradeRecord>} 交易记录集合
     */
    @GetMapping("/tradeRecord/getAllTradeRecordByType")
    @Transactional
    public Result getAllTradeRecordByType(@RequestParam @NotNull TradeTypeEnum tte){
        List<TradeRecord> rs = tradeRecordService.getAllTradeRecordByType(tte);
        return ReturnResultUtil.success(rs);
    }

    /**
     * @description: 根据价格区间获得交易记录集合
     * @param {BigDecimal} price1 起始价格
     * @param {BigDecimal} price2 终止价格
     * @return {List<TradeRecord>} 交易记录集合
     */
    @GetMapping("/tradeRecord/getAllTradeRecordByPriceBetween")
    @Transactional
    public Result getAllTradeRecordByPriceBetween(@RequestParam @NotNull BigDecimal price1, @RequestParam @NotNull BigDecimal price2){
        List<TradeRecord> rs = tradeRecordService.getAllTradeRecordByPriceBetween(price1, price2);
        return ReturnResultUtil.success(rs);
    }

    /**
     * @description: 根据创建日期区间获得交易记录集合
     * @param {Date} date1 起始日期
     * @param {Date} date2 终止日期
     * @return {List<TradeRecord>} 交易记录集合
     */
    @GetMapping("/tradeRecord/getAllTradeRecordByCreateDateBetween")
    @Transactional
    public Result getAllTradeRecordByCreateDateBetween(@RequestParam @NotNull String date1, @RequestParam @NotNull String date2){
        List<TradeRecord> rs = tradeRecordService.getAllTradeRecordByCreateDateBetween(date1, date2);
        return ReturnResultUtil.success(rs);
    }

    /**
     * @description: 根据用户账号获得交易记录集合
     * @param {String} account 用户账号
     * @return {List<TradeRecord>} 交易记录集合
     */
    @GetMapping("/tradeRecord/getAllTradeRecordByOwnerAccount")
    @Transactional
    public Result getAllTradeRecordByOwnerAccount(@RequestParam @NotNull String account){
        List<TradeRecord> rs = tradeRecordService.getAllTradeRecordByOwnerAccount(account);
        return ReturnResultUtil.success(rs);
    }

    /**
     * @description: 根据广告id获得交易记录集合
     * @param {ObjectId} id 广告id
     * @return {List<TradeRecord>} 交易记录集合
     */
    @GetMapping("/tradeRecord/getAllTradeRecordByAdId")
    @Transactional
    public Result getAllTradeRecordByAdId(@RequestParam @NotNull ObjectId id){
        List<TradeRecord> rs = tradeRecordService.getAllTradeRecordByAdId(id);
        return ReturnResultUtil.success(rs);
    }

    /**
     * 添加贸易记录
     *
     * @param tradeNo      订单号
     * @param tte          交易类型
     * @param price        交易价格
     * @param createDate   交易创建日期
     * @param ownerAccount 拥有者账户
     * @param adId         对应广告id
     * @param tse          交易状态
     */
    @PostMapping("/tradeRecord/addTradeRecord")
    @Transactional
    public Result addTradeRecord(@RequestParam @NotNull String tradeNo,
                                 @RequestParam @NotNull TradeTypeEnum tte,
                                 @RequestParam @NotNull BigDecimal price,
                                 @RequestParam @NotNull String createDate,
                                 @RequestParam @NotNull String ownerAccount,
                                 @RequestParam @NotNull ObjectId adId,
                                 @RequestParam @NotNull TradeStatusEnum tse){
        tradeRecordService.addTradeRecord(tradeNo, tte, price, createDate, ownerAccount, adId, tse);
        return ReturnResultUtil.success("add trade record successfully");
    }

    /**
     * 根据交易记录id删除交易记录
     *
     * @param id 交易记录id
     */
    @PostMapping("/tradeRecord/deleteTradeRecordById")
    @Transactional
    public Result deleteTradeRecordById(@RequestParam @NotNull ObjectId id){
        tradeRecordService.deleteTradeRecordById(id);
        return ReturnResultUtil.success("delete trade record successfully");
    }

    /**
     * 根据交易记录id更新贸易记录
     *
     * @param id           交易记录id
     * @param tradeNo      订单号
     * @param tte          交易类型
     * @param price        交易价格
     * @param createDate   交易创建日期
     * @param ownerAccount 拥有者账户
     * @param adId         对应广告id
     * @param tse          交易状态
     */
    @PostMapping("/tradeRecord/updateTradeRecordById")
    @Transactional
    public Result updateTradeRecordById(@RequestParam @NotNull ObjectId id,
                                        @RequestParam @NotNull String tradeNo,
                                        @RequestParam @NotNull TradeTypeEnum tte,
                                        @RequestParam @NotNull BigDecimal price,
                                        @RequestParam @NotNull String createDate,
                                        @RequestParam @NotNull String ownerAccount,
                                        @RequestParam @NotNull ObjectId adId,
                                        @RequestParam @NotNull TradeStatusEnum tse){
        tradeRecordService.updateTradeRecordById(id, tradeNo, tte, price, createDate, ownerAccount, adId, tse);
        return ReturnResultUtil.success("update trade record successfully");
    }
}
