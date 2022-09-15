/*
 * @Description: 用户的持久层
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2021-11-03 03:50:29
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-13 02:18:26
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/repository/UserRepository.java
 */
package com.easytrade.easytradeapi.repository;

import com.easytrade.easytradeapi.model.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface UserRepository extends MongoRepository<User, ObjectId> {
    // /**
    //  * @description: 根据邮件地址寻找用户
    //  * @param {String} email 搜索的用户邮箱
    //  * @return {User} 用户类定义的变量
    //  */    
    // public User findOneByEmail(String email);

    // /**
    //  * @description: 根据邮件地址判断用户是否存在
    //  * @param {String} email 搜索的用户邮箱
    //  * @return {Boolean} true为存在，false为不存在
    //  */    
    // public Boolean existsByEmail(String email);

    /**
     * @description: 根据id获取用户
     * @param {ObjectId} userId 用户id
     * @return {User} 用户类定义的变量
     */    
    public User findOneById(ObjectId userId);

    /**
     * @description: 根据手机号寻找用户
     * @param {String} phone 用户手机号
     * @return {User} 用户类定义的变量
     */    
    public User findOneByPhone(String phone);

    /**
     * @description: 根据手机号判断用户是否存在
     * @param {String} phone 搜索的手机号
     * @return {Boolean} true为存在，false为不存在
     */    
    public Boolean existsByPhone(String phone);

    public Boolean existsAllById(ObjectId id);

    /**
     * 根据社会信用码判断用户是否存在
     *
     * @param creditCode 信用代码
     * @return {@link Boolean}
     */
    public Boolean existsByCreditCode(String creditCode);
}
