/*
 * @Description: 优惠码的持久层
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2022-01-10 03:50:57
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-10 18:50:19
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/repository/PromoCodeRepository.java
 * 
 */
package com.easytrade.easytradeapi.repository;

import com.easytrade.easytradeapi.model.PromoCode;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

public interface PromoCodeRepository extends MongoRepository<PromoCode, ObjectId> {
    /**
     * @description: 根据代码来搜索优惠码
     * @param {String} code 搜索的优惠码
     * @return {PromoCode} 返回优惠码实体
     */    
    public PromoCode findOneByCode(String code);

    /**
     * @description: 根据免费标准广告次数获取推广码
     * @param {long} freeAdNums 免费标准广告次数
     * @return {List<PromoCode>} 推广码集合
     */
    public List<PromoCode> findAllByFreeAdNums(long freeAdNums);

    /**
     * @description: 根据免费标准广告次数区间获取推广码
     * @param {long} freeAdNums1 免费标准广告次数
     * @param {long} freeAdNums2 免费标准广告次数
     * @return {List<PromoCode>} 推广码集合
     */
    public List<PromoCode> findAllByFreeAdNumsBetween(long freeAdNums1, long freeAdNums2);

    /**
     * @description: 根据创建日期区间获取推广码
     * @param date1 {Date} 起始日期
     * @param date2 {Date} 结束日期
     * @return {List<PromoCode>} 推广码集合
     */
    public List<PromoCode> findAllByCreateDateBetween(Date date1, Date date2);

    /**
     * @description: 根据过期日期区间获取推广码
     * @param date1 {Date} 起始日期
     * @param date2 {Date} 结束日期
     * @return {List<PromoCode>} 推广码集合
     */
    public List<PromoCode> findAllByExpireDateBetween(Date date1, Date date2);

    /**
     * @description: 根据使用次数区间获取推广码
     * @param {long} times1 起始值
     * @param {long} times2 结束值
     * @return {List<PromoCode>} 推广码集合
     */
    public List<PromoCode> findAllByUsedTimesBetween(long times1, long times2);

    /**
     * @description: 根据最大使用次数区间获取推广码
     * @param {long} times1 起始值
     * @param {long} times2 结束值
     * @return {List<PromoCode>} 推广码集合
     */
    public List<PromoCode> findAllByMaxUseTimesBetween(long times1, long times2);

    /**
     * @description: 根据适用用户类型获取推广码
     * @param {String} status 用户类型
     * @return {List<PromoCode>} 推广码集合
     */
    public List<PromoCode> findAllBySuitableUserStatus(String status);

    /**
     * 删除优惠码通过代码号
     *
     * @param code 代码号
     */
    public void deleteByCode(String code);


    /**
     * 通过代码号判断优惠码是否存在
     *
     * @param code 代码
     * @return boolean 是否存在
     */
    public boolean existsByCode(String code);
}

