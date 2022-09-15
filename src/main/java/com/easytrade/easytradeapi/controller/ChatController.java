/*
 * @Description: 聊天控制器
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2022-01-10 22:19:51
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-16 18:13:51
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/controller/ChatController.java
 */
package com.easytrade.easytradeapi.controller;

import java.text.ParseException;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import com.easytrade.easytradeapi.constant.consists.Chat;
import com.easytrade.easytradeapi.constant.consists.Result;
import com.easytrade.easytradeapi.service.intf.ChatService;
import com.easytrade.easytradeapi.utils.ReturnResultUtil;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Validated
public class ChatController {
    @Autowired
    private ChatService chatService;

    // 每周二、四、六凌晨3.10的定时
    private final static String updateChatInRedisToMongodb = "0 10 3 ? * TUE,THU,SAT";

    /**
     * @description: 根据websocket实现的聊天，发送聊天信息
     * @param {Chat} chat 一条聊天消息
     * @return {Result} 结果信息
     */
    @MessageMapping("/chat/sendChat")
    @Transactional
    public Result chat(@Validated @NotNull Chat chat) {
        chatService.chat(chat);
        return ReturnResultUtil.success();
    }

    /**
     * @description: 发送系统消息
     * @param {Chat} chat 一条聊天消息
     * @return {Result} 结果信息
     */
    @PostMapping("/chat/systemNotification")
    @Transactional
    public Result systemNotification(@Validated @NotNull Chat chat) {
        chatService.systemNotification(chat);
        return ReturnResultUtil.success();
    }

    // /**
    // * @description: 获取某一用户的系统消息
    // * @param {ObjectId} userId 用户id
    // * @param {String} token 用户登录后header中存在的JWT
    // * @return {Result} 结果信息，包含所有系统通知
    // */
    // @GetMapping("/chat/getSystemNotification")
    // @Transactional
    // public Result getSystemNotification(ObjectId userId,
    // @RequestHeader(name = "Authorization") @NotNull @NotBlank String token) {
    // return ReturnResultUtil.success(chatService.getSystemNotification(userId, token));
    // }


    /**
     * 获取登录用户和另一位用户的聊天记录
     *
     * @param contactUserId 联系用户id
     * @param token         登录用户的JWT
     * @return {@link Result} 返回的聊天记录
     * @throws ParseException 解析异常
     */
    @GetMapping("/chat/getChatRecordByTwoUsers")
    public Result getChatRecordByTwoUsers(@RequestParam @NotNull ObjectId contactUserId,
            @RequestHeader(name = "Authorization") @NotNull @NotBlank String token)
            throws ParseException {
        return ReturnResultUtil.success(chatService.getChatRecordByTwoUsers(contactUserId, token));
    }

    /**
     * 获取联系人
     *
     * @param token  登录用户的JWT
     * @return {@link Result}
     */
    @GetMapping("/chat/getContacts")
    public Result getContacts(@RequestHeader(name = "Authorization") @NotNull @NotBlank String token) {
        return ReturnResultUtil.success(chatService.getContacts(token));
    }

    /**
     * @description: 将联系人添加到用户的联系列表
     * @param {ObjectId} contactId 联系人id
     * @param {String} token 用户登录后，返回到header中的JWT
     * @return {Result} 返回信息
     */
    @PostMapping("/chat/addContact")
    @Transactional
    public Result addContact(@RequestParam @NotNull ObjectId contactId,
            @RequestHeader(name = "Authorization") @NotNull @NotBlank String token) {
        chatService.addContact(contactId, token);
        return ReturnResultUtil.success();
    }

    /**
     * @description: 将redis中的聊天记录缓存更新到mongodb，固定时间自动执行
     * @param {*}
     * @return {Result} 结果信息
     */
    @GetMapping("/chat/updateChatInRedisToMongodb")
    @Scheduled(cron = updateChatInRedisToMongodb)
    @Transactional
    public Result updateChatInRedisToMongodb() {
        chatService.updateChatInRedisToMongodb();
        return ReturnResultUtil.success();
    }

    /**
     * @description: 检查聊天服务中输入的用户是否存在
     * @param {String} userId 需要检查的用户id
     * @return {Result} 结果信息，包含判断结果，true为存在，false为不存在
     */
    @GetMapping("/chat/checkUserExist")
    public Result checkUserExist(String userId) {
        return ReturnResultUtil.success(chatService.checkUserExist(userId));
    }

    /**
     * @description: 检查聊天服务中用户是否已在线
     * @param {String} id 需要检查的sessionId或用户id
     * @return {Result} 结果信息，包含判断结果，true为在线，false为不在线
     */
    @GetMapping("/chat/checkUserOnline")
    public Result checkUserOnline(String id) {
        return ReturnResultUtil.success(chatService.checkUserOnline(id));
    }

    /**
     * 根据Token获得所有的聊天记录
     *
     * @param token 用户登录的JWT
     * @return {@link Result} 返回的结果
     * @throws ParseException 解析异常
     */
    @GetMapping("/chat/getAllRecordsByToken")
    public Result getAllRecordsByToken(
            @RequestHeader(name = "Authorization") @NotNull @NotBlank String token) throws ParseException {
        return ReturnResultUtil.success(chatService.getAllRecordsByToken(token));
    }

    /**
     * 根据Token获得所有的系统通知记录
     *
     * @param token 用户登录的JWT
     * @return {@link Result} 返回的聊天记录
     * @throws ParseException 解析异常
     */
    @GetMapping("/chat/getAllSystemChatRecordsByToken")
    public Result getAllSystemChatRecordsByToken(
            @RequestHeader(name = "Authorization") @NotNull @NotBlank String token) throws ParseException {
        return ReturnResultUtil.success(chatService.getAllSystemChatRecordsByToken(token));
    }

    ///**
    // * 创建一个新的聊天记录
    // *
    // * @param userOneId userOneId
    // * @param userTwoId userTwoId
    // */
    //@PostMapping("/chat/createRecord")
    //public Result createRecord(@RequestParam @NotNull ObjectId userOneId,
    //                           @RequestParam @NotNull ObjectId userTwoId){
    //    chatService.createRecord(userOneId, userTwoId);
    //    return ReturnResultUtil.success("create chat record successfully");
    //}
}

