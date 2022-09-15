/*
 * @Description: 二手车广告位的持久层
 * @Author: Mo Xu
 * @Date: 2021-12-21 20:37:04
 * @LastEditors: Mo Xu
 * @LastEditTime: 2022-01-19 03:53:05
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/repository/SecondHandCarAdRepository.java
 */
package com.easytrade.easytradeapi.repository;

import java.util.Date;
import java.util.List;
import com.easytrade.easytradeapi.constant.enums.AdStatusEnum;
import com.easytrade.easytradeapi.model.SecondHandCarAd;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SecondHandCarAdRepository extends MongoRepository<SecondHandCarAd, ObjectId> {
    // 根据id来获取二手车广告位
    public SecondHandCarAd findOneById(ObjectId id);

    /**
     * @description: 根据广告状态来获取对应的所有广告
     * @param {AdStatusEnum} adStatus 广告状态
     * @return {List<SecondHandCarAd>} 对应的所有广告
     */
    public List<SecondHandCarAd> findAllByAdStatus(AdStatusEnum adStatus);

    /**
     * @description 根据用户id得到其所属的所有二手车广告
     * @param {ObjectId} userId 用户的id
     * @return {List<SecondHandCarAd>} 得到的二手车类的集合
     */
    public List<SecondHandCarAd> findAllByUserId(ObjectId userId);

    /**
     * @description 根据样本车辆id获得所有二手车集合
     * @param {String} exampleCarId 样本车辆id
     * @return {List<SecondHandCarAd>} 二手车结果集合
     */
    public List<SecondHandCarAd> findAllByExampleVehicleId(ObjectId exampleCarId);

    /**
     * @description 根据购买日期区间获得所有二手车集合
     * @param {Date} date1 起始日期
     * @param {Date} date2 终止日期
     * @return {List<SecondHandCarAd>} 二手车结果集合
     */
    public List<SecondHandCarAd> findAllByPurchaseDateBetween(Date date1, Date date2);

    /**
     * @description 根据生产日期区间获得所有二手车集合
     * @param {Date} date1 起始日期
     * @param {Date} date2 终止日期
     * @return {List<SecondHandCarAd>} 二手车结果集合
     */
    public List<SecondHandCarAd> findAllByProductionDateBetween(Date date1, Date date2);

    /**
     * @description 根据大架号获得所有二手车
     * @param {String} bigFrame 大架号
     * @return {List<SecondHandCarAd>} 二手车结果集合
     */
    public SecondHandCarAd findOneByBigFrame(String bigFrame);

    /**
     * @description 根据车牌号获得二手车
     * @param {String} license 车牌号
     * @return {List<SecondHandCarAd>} 二手车结果集合
     */
    public SecondHandCarAd findOneByLicense(String license);

    /**
     * @description 根据行驶公里数区间获得所有二手车
     * @param kilo1 起始公里数
     * @param kilo2 终止公里数
     * @return {List<SecondHandCarAd>} 二手车结果集合
     */
    public List<SecondHandCarAd> findAllByKilometersBetween(long kilo1, long kilo2);

    /**
     * @description 检测数据库中是否存在输入的样本车辆id
     * @param {String} exampleCarId 样本车辆id
     * @return {List<SecondHandCarAd>} 二手车结果集合
     */
    public Boolean existsAllByExampleVehicleId(ObjectId exampleCarId);

    /**
     * @description 检测数据库中是否存在输入的大架号
     * @param {String} bigFrame 样本车辆id
     * @return {List<SecondHandCarAd>} 二手车结果集合
     */
    public Boolean existsByBigFrame(String bigFrame);

    /**
     * @description 检测数据库中是否存在输入的车牌号
     * @param {String} license 车牌号
     * @return {List<SecondHandCarAd>} 二手车结果集合
     */
    public Boolean existsByLicense(String license);
}
