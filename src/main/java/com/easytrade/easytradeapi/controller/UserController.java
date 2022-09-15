/*
 * @Description: 用户控制器
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2021-11-02 23:59:09
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-16 20:12:58
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/controller/UserController.java
 */

package com.easytrade.easytradeapi.controller;

import javax.mail.MessagingException;
import javax.validation.constraints.NotNull;
import com.easytrade.easytradeapi.constant.consists.Result;
import com.easytrade.easytradeapi.constant.enums.SecurityQuestionEnum;
import com.easytrade.easytradeapi.model.User;
import com.easytrade.easytradeapi.service.intf.UserService;
import com.easytrade.easytradeapi.utils.ReturnResultUtil;
import javax.validation.constraints.NotBlank;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;


@RestController
@Validated
public class UserController {
    @Autowired
    UserService userService;

    // 每天凌晨3.30的定时
    private static final String checkUserNeedClearSchedule = "0 30 3 * * ?";

    // 每天凌晨3.50的定时
    private static final String clearUserNeedClearSchedule = "0 50 3 * * ?";

    /**
     * 根据JWT获取用户
     *
     * @param token JWT令牌
     * @return {@link Result} 返回的结果信息
     */
    @GetMapping("/user/getUserByToken")
    public Result getUserByToken(@RequestParam @NotNull String token) {
        User user = userService.getUserByToken(token, false);
        return ReturnResultUtil.success(user);
    }

    /**
     * @description: 注册方法，将输入的JSON格式的Body映射为用户类，同时进行数据校验，具体校验参照用户类
     * @param {User} user 将传入的Json格式的Body映射为用户类，并进行校验
     * @return {Result} 结果信息
     */
    @PostMapping("/user/register")
    public Result register(@RequestBody @Validated @NotNull User user,
                           @RequestParam @NotNull Integer verifyCode) {
        userService.register(user, verifyCode);
        return ReturnResultUtil.success();
    }

    /**
     * 更改密码
     *
     * @param verifyCode  验证码
     * @param newPassword 新密码
     * @param token       用户的JWT
     * @return {@link Result} 返回的结果
     */
    @PostMapping("/user/changePassword")
    public Result changePassword(@RequestHeader(name = "Authorization") @NotNull @NotBlank String token,
                                 @RequestParam @NotNull Integer verifyCode,
                                 @RequestParam @NotNull String newPassword) {
        userService.changePassword(token, verifyCode, newPassword);
        return ReturnResultUtil.success();
    }

    /**
     * @description: 扫描数据库，检查用户是否需要清空广告，固定时间执行
     * @param {*}
     * @return {Result} 结果信息
     */
    @PostMapping("/user/checkUserNeedClear")
    @Scheduled(cron = checkUserNeedClearSchedule)
    public Result checkUserNeedClear() {
        userService.checkUserNeedClear();
        return ReturnResultUtil.success();
    }

    /**
     * @description: 清空需要清空的用户广告
     * @param {*}
     * @return {Result} 结果信息
     */    
    @PostMapping("/user/clearUserNeedClear")
    @Scheduled(cron = clearUserNeedClearSchedule)
    public Result clearUserNeedClear() {
        userService.clearUserNeedClear();
        return ReturnResultUtil.success();
    }

    /**
     * 通过id获取一个用户对象
     *
     * @param id id 用户id
     * @return {@link User}
     */
    @GetMapping("/user/getOneById")
    public Result getOneById(@RequestParam @NotNull ObjectId id) {
        return ReturnResultUtil.success(userService.getOneById(id));
    }

    /**
     * 根据手机号获取用户收藏的载具列表
     *
     * @param token 用户的JWT
     * @return {@link ArrayList}<{@link ObjectId}> 返回的收藏载具列表
     */
    @GetMapping("/user/getFavoriteVehicles")
    public ArrayList<ObjectId> getFavoriteVehicles(
            @RequestHeader(name = "Authorization") @NotNull @NotBlank String token) {
        return userService.getFavoriteVehicles(token);
    }

    /**
     * 设置用户的安全问题和答案
     *
     * @param securityQuestion 安全问题
     * @param securityAnswer   安全答案
     * @param token            用户的JWT
     * @return {@link Result} 返回的结果
     */
    @PostMapping("/user/setSecurityQuestionAndAnswer")
    public Result setSecurityQuestionAndAnswer(@RequestHeader(name = "Authorization") @NotNull @NotBlank String token,
                                               @RequestParam @NotNull SecurityQuestionEnum securityQuestion,
                                               @RequestParam @NotNull String securityAnswer) {
        userService.setSecurityQuestionAndAnswer(token, securityQuestion, securityAnswer);
        return ReturnResultUtil.success();
    }

    /**
     * 获取安全问题
     *
     * @param token 用户的JWT
     * @return {@link Result} 返回的结果
     */
    @GetMapping("/user/getSecurityQuestion")
    public Result getSecurityQuestion(@RequestHeader(name = "Authorization") @NotNull @NotBlank String token) {
        return ReturnResultUtil.success(userService.getSecurityQuestion(token));
    }

    /**
     * 更改密码安全
     *
     * @param phone            手机号
     * @param securityQuestion 安全问题
     * @param securityAnswer   安全答案
     * @param newPassword      新密码
     * @return {@link Result} 返回的结果
     */
    @PostMapping("/user/changePasswordBySecurity")
    public Result changePasswordBySecurity(@RequestParam @NotNull String phone,
                                           @RequestParam @NotNull SecurityQuestionEnum securityQuestion,
                                           @RequestParam @NotNull String securityAnswer,
                                           @RequestParam @NotNull String newPassword) {
        userService.changePasswordBySecurity(phone, securityQuestion, securityAnswer, newPassword);
        return ReturnResultUtil.success();
    }

    /**
     * 设置电子邮件
     *
     * @param token 用户的JWT
     * @param email 邮箱地址
     * @return {@link Result} 返回的结果信息
     * @throws MessagingException 通讯异常
     */
    @PostMapping("/user/setEmail")
    public Result setEmail(@RequestHeader(name = "Authorization") @NotNull @NotBlank String token,
                           @RequestParam @NotNull String email) throws MessagingException {
        userService.setEmail(token, email);
        return ReturnResultUtil.success();
    }

    /**
     * 根据旧密码设置新密码
     *
     * @param token       用户的JWT
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return {@link Result} 返回的结果
     */
    @PostMapping("/user/setPasswordByOld")
    public Result setPasswordByOld(@RequestHeader(name = "Authorization") @NotNull @NotBlank String token,
                                   @RequestParam @NotNull String oldPassword,
                                   @RequestParam @NotNull String newPassword) {
        userService.setPasswordByOld(token, oldPassword, newPassword);
        return ReturnResultUtil.success();
    }

    /**
     * 获取用户所有载具ID列表
     *
     * @param token 用户的JWT
     * @return {@link Result} 返回的结果
     */
    @GetMapping("/user/getUserAllVehicleAds")
    public Result getUserAllVehicleAds(@RequestHeader(name = "Authorization") @NotNull @NotBlank String token) {
        return ReturnResultUtil.success(userService.getUserAllVehicleAds(token));
    }

    /**
     * 获取用户完成的广告
     *
     * @param token 用户的JWT
     * @return {@link Result} 返回的结果
     */
    @GetMapping("/user/getUserCompletedCarAds")
    public Result getUserCompletedCarAds(@RequestHeader(name = "Authorization") @NotNull @NotBlank String token) {
        return ReturnResultUtil.success(userService.getUserCompletedCarAds(token));
    }

    /**
     * 获取用户免费广告次数
     *
     * @param token 用户的JWT
     * @return {@link Result} 返回的结果
     */
    @GetMapping("/user/getUserFreeAdNums")
    public Result getUserFreeAdNums(@RequestHeader(name = "Authorization") @NotNull @NotBlank String token) {
        return ReturnResultUtil.success(userService.getUserFreeAdNums(token));
    }

    ///**
    // * 收藏车辆
    // *
    // * @param token 令牌
    // * @param carId 汽车id
    // */
    //@PostMapping("/user/starCar")
    //public Result starCar(@RequestParam @NotNull String token,
    //                      @RequestParam @NotNull ObjectId carId){
    //    userService.starCar(token, carId);
    //    return ReturnResultUtil.success("star car successfully");
    //}
    //
    ///**
    // * 取消收藏车辆
    // *
    // * @param token 令牌
    // * @param carId 汽车id
    // */
    //@PostMapping("/user/cancelStarCar")
    //public Result cancelStarCar(@RequestParam @NotNull String token,
    //                            @RequestParam @NotNull ObjectId carId){
    //    userService.cancelStarCar(token, carId);
    //    return ReturnResultUtil.success("cancel star car successfully");
    //}
}
