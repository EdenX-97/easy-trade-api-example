/*
 * @Description: 新摩托广告位的持久层
 * @Author: Hongzhang Liu
 * @Date: 2022-06-30 01:38
 */
package com.easytrade.easytradeapi.repository;

import com.easytrade.easytradeapi.constant.enums.AdStatusEnum;
import com.easytrade.easytradeapi.model.NewMotorAd;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


public interface NewMotorAdRepository extends MongoRepository<NewMotorAd, ObjectId> {

    /**
     * 根据id获得新车广告
     *
     * @param id id
     * @return {@link NewMotorAd}
     */
    public NewMotorAd findOneById(ObjectId id);

    /**
     * @description: 根据广告状态来获取对应的所有广告
     * @param {AdStatusEnum} adStatus 广告状态
     * @return {List<NewMotorAd>} 对应的所有广告
     */    
    public List<NewMotorAd> findAllByAdStatus(AdStatusEnum adStatus);
}
