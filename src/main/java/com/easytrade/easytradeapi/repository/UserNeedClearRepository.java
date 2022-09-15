/*
 * @Description: 需要清除广告的用户持久层
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2022-01-16 18:36:13
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-16 19:54:56
 * 
 * @FilePath: 
 * /EasyBuyCar/src/main/java/com/jiandanmaiche/api/repository/UserNeedClearRepository.java
 */
package com.easytrade.easytradeapi.repository;

import com.easytrade.easytradeapi.model.UserNeedClear;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface UserNeedClearRepository extends MongoRepository<UserNeedClear, ObjectId> {
    /**
     * @description: 根据id获取需要清除广告的用户
     * @param {ObjectId} userId 用户id
     * @return {UserNeedClear} 用户类定义的变量
     */
    public UserNeedClear findOneById(ObjectId userId);
}

