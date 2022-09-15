/*
 * @Description: 用户服务层的接口
 * @Author: Mo Xu
 * @Date: 2021-11-03 03:50:09
 * @LastEditors: Mo Xu
 * @LastEditTime: 2022-01-16 19:30:02
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/service/intf/UserService.java
 */
package com.easytrade.easytradeapi.service.intf;

import com.easytrade.easytradeapi.constant.enums.SecurityQuestionEnum;
import com.easytrade.easytradeapi.model.User;
import org.bson.types.ObjectId;
import org.springframework.security.core.userdetails.UserDetails;

import javax.mail.MessagingException;
import java.util.ArrayList;
import java.util.List;

public interface UserService {
    /**
     * @description: 为Spring security提供支持的方法
     * @param {String} account 用户名或手机号
     * @return {UserDetails} Spring security的内置类，包含用户的账户、密码即权限信息
     */    
    public UserDetails loadUserByUsername(String account);

    /**
     * 根据JWT获取用户
     *
     * @param token          JWT令牌
     * @param ifWithPassword 是否包含密码
     * @return {@link User}
     */
    public User getUserByToken(String token, Boolean ifWithPassword);

    /**
     * @description: 注册方法，将输入的JSON格式的Body映射为用户类，同时进行数据校验，具体校验参照用户类
     * @param {User} user 将传入的Json格式的Body映射为用户类，并进行校验
     * @return {*}
     */
    public void register(User user, Integer verifyCode);

    /**
     * 通过电话更改密码
     *
     * @param verifyCode  验证码
     * @param newPassword 新密码
     * @param token       用户的JWT
     */
    public void changePassword(String token, Integer verifyCode, String newPassword);

    /**
     * @description: 扫描数据库，检查用户是否需要清空广告
     * @param {*}
     * @return {*}
     */    
    public void checkUserNeedClear();

    /**
     * @description: 清空需要清空的用户广告
     * @param {*}
     * @return {*}
     */    
    public void clearUserNeedClear();


    /**
     * 通过id获取一个用户对象
     *
     * @param id id 用户id
     * @return {@link User}
     */
    public User getOneById(ObjectId id);

    /**
     * 根据手机号获取用户收藏的载具列表
     *
     * @param token 用户的JWT
     * @return {@link ArrayList}<{@link ObjectId}> 返回的收藏载具列表
     */
    public ArrayList<ObjectId> getFavoriteVehicles(String token);

    /**
     * 设置用户的安全问题和答案
     *
     * @param securityQuestion 安全问题
     * @param securityAnswer   安全答案
     * @param token            用户的JWT
     */
    public void setSecurityQuestionAndAnswer(String token,
                                             SecurityQuestionEnum securityQuestion,
                                             String securityAnswer);

    /**
     * 获取安全问题
     *
     * @param token 用户的JWT
     * @return {@link SecurityQuestionEnum} 返回的安全问题
     */
    public SecurityQuestionEnum getSecurityQuestion(String token);

    /**
     * 更改密码安全
     *
     * @param phone            手机号
     * @param securityQuestion 安全问题
     * @param securityAnswer   安全答案
     * @param newPassword      新密码
     */
    public void changePasswordBySecurity(String phone,
                                         SecurityQuestionEnum securityQuestion,
                                         String securityAnswer,
                                         String newPassword);

    /**
     * 设置电子邮件
     *
     * @param token 用户的JWT
     * @param email 邮箱地址
     */
    public void setEmail(String token, String email) throws MessagingException;

    /**
     * 根据旧密码设置新密码
     *
     * @param token       用户的JWT
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    public void setPasswordByOld(String token, String oldPassword, String newPassword);

    /**
     * 获取用户所有载具广告广告
     *
     * @param token 用户的JWT
     * @return {@link List}<{@link ObjectId}> 返回的所有载具ID列表
     */
    public List<ObjectId> getUserAllVehicleAds(String token);

    /**
     * 获取用户完成的广告
     *
     * @param token 用户的JWT
     * @return {@link List}<{@link ObjectId}> 返回的载具ID列表
     */
    public List<ObjectId> getUserCompletedCarAds(String token);

    /**
     * 获取用户免费广告的次数
     *
     * @param token 令牌
     * @return long
     */
    public long getUserFreeAdNums(String token);

    ///**
    // * 收藏车辆
    // *
    // * @param token 令牌
    // * @param carId 汽车id
    // */
    //public void starCar(String token, ObjectId carId);
    //
    ///**
    // * 取消收藏车辆
    // *
    // * @param token 令牌
    // * @param carId 汽车id
    // */
    //public void cancelStarCar(String token, ObjectId carId);
}
