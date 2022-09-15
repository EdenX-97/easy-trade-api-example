/*
 * @Description: 车辆模版的持久层
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2021-12-25 20:48:56
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-01 21:24:40
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/repository/ExampleCarRepository.java
 */
package com.easytrade.easytradeapi.repository;

import com.easytrade.easytradeapi.model.ExampleCar;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface ExampleCarRepository extends MongoRepository<ExampleCar, ObjectId> {
    /**
     * @description: 根据车型名称来获取车辆模版
     * @param {String} model 传入的车型名
     * @return {*}
     */
    public ExampleCar findOneByModel(String model);

    /**
     * @description: 根据车型id来获取车辆模版集合
     * @param {String} id 传入的车型id
     * @return {*}
     */
    public Boolean existsExampleCarById(ObjectId id);

    /**
     * 根据型号名判断模版是否存在
     *
     * @param model 型号名
     * @return {@link Boolean} 存在则返回true，否则返回false
     */
    public Boolean existsExampleCarByModel(String model);


    /**
     * 通过id找到一个样本车
     *
     * @param id id 样本车id
     * @return {@link ExampleCar}
     */
    public ExampleCar findOneById(ObjectId id);
}
