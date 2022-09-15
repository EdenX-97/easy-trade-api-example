/*
 * @Description: 新车广告位的持久层
 * @Author: Mo Xu
 * @Date: 2021-11-13 19:56:32
 * @LastEditors: Mo Xu
 * @LastEditTime: 2022-01-19 03:33:28
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/repository/NewCarAdRepository.java
 */
package com.easytrade.easytradeapi.repository;

import java.util.List;
import com.easytrade.easytradeapi.constant.enums.AdStatusEnum;
import com.easytrade.easytradeapi.constant.enums.GearboxTypeEnum;
import com.easytrade.easytradeapi.model.NewCarAd;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface NewCarAdRepository extends MongoRepository<NewCarAd, ObjectId> {

    /**
     * 根据id获得新车广告
     *
     * @param id id
     * @return {@link NewCarAd}
     */
    public NewCarAd findOneById(ObjectId id);

    /**
     * @description: 根据广告状态来获取对应的所有广告
     * @param {AdStatusEnum} adStatus 广告状态
     * @return {List<NewCarAd>} 对应的所有广告
     */    
    public List<NewCarAd> findAllByAdStatus(AdStatusEnum adStatus);

    /**
     * @description 根据车商id得到其所属的所有新车广告
     * @param {ObjectId} userId 车商id
     * @return {List<SecondHandCarAd>} 得到的新车广告的类的集合
     */
    public List<NewCarAd> findAllByUserId(ObjectId userId);

    /**
     * @description 根据变速箱模式获得所有新车广告
     * @param {GearboxTypeEnum} gte 变速箱模式
     * @return {List<NewCarAd>}  得到的新车广告的类的集合
     */
    public List<NewCarAd> findAllByGearbox(GearboxTypeEnum gte);

    /**
     * @description 根据车辆模版id获得所有新车广告
     * @param {String} exampleCarId 车辆模版id
     * @return {List<NewCarAd>}  得到的新车广告的类的集合
     */
    public List<NewCarAd> findAllByExampleVehicleId(ObjectId exampleCarId);
}
