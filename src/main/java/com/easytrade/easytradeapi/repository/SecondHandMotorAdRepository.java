/*
 * @Description: 二手摩托广告位的持久层
 * @Author: Hongzhang Liu
 * @Date: 2022-06-30 01:39
 * */
package com.easytrade.easytradeapi.repository;

import com.easytrade.easytradeapi.constant.enums.AdStatusEnum;
import com.easytrade.easytradeapi.model.SecondHandMotorAd;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SecondHandMotorAdRepository extends MongoRepository<SecondHandMotorAd, ObjectId> {

    /**
     * 找到一个通过id
     *
     * @param id id
     * @return {@link SecondHandMotorAd}
     */
    public SecondHandMotorAd findOneById(ObjectId id);

    /**
     * @description: 根据广告状态来获取对应的所有广告
     * @param {AdStatusEnum} adStatus 广告状态
     * @return {List<SecondHandCarAd>} 对应的所有广告
     */
    public List<SecondHandMotorAd> findAllByAdStatus(AdStatusEnum adStatus);

}
