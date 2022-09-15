/*
 * @Description: 聊天记录的持久层
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2022-01-11 22:26:15
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-11 22:28:21
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/repository/ChatRecordRepository.java
 */
package com.easytrade.easytradeapi.repository;

import com.easytrade.easytradeapi.model.ChatRecord;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface ChatRecordRepository extends MongoRepository<ChatRecord, ObjectId> {
    /**
     * @description: 查询两个用户间的聊天记录
     * @param {ObjectId} userOneId 用户1的id
     * @param {ObjectId} userTwoId 用户2的id
     * @return {ChatRecord} 两个用户的聊天记录
     */    
    public ChatRecord findOneByUserOneIdAndUserTwoId(ObjectId userOneId, ObjectId userTwoId);

    /**
     * @description: 查询两个用户间的聊天记录是否存在
     * @param {ObjectId} userOneId 用户1的id
     * @param {ObjectId} userTwoId 用户2的id
     * @return {Boolean} true为存在，false为不存在
     */    
    public Boolean existsByUserOneIdAndUserTwoId(ObjectId userOneId, ObjectId userTwoId);
}

