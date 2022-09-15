/*
 * @Description: 地区信息的持久层
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2022-01-02 20:50:06
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-02 21:57:32
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/repository/AreaRepository.java
 */
package com.easytrade.easytradeapi.repository;

import com.easytrade.easytradeapi.model.Area;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface AreaRepository extends MongoRepository<Area, ObjectId> {
    /**
     * @description: 根据镇信息搜索地区
     * @param {String} town 搜索的镇名
     * @return {Area} 地区类
     */
    public Area findOneByProvinceAndCityAndCountyAndTown(String province, String city, String county, String town);

    /**
     * @description: 根据地区代码删除地区数据
     * @param {long} code 地区代码
     */
    public void deleteByCode(long code);

    /**
     * @description: 根据地区代码查询地区是否存在
     * @param {long} code 地区代码
     * @return {boolean} 是/否
     */
    public boolean existsByCode(long code);

    /**
     * @description: 根据地区代码查找到一条地区记录
     * @param {long} code 地区代码
     * @return {Area} 地区记录
     */
    public Area findOneByCode(long code);
}

