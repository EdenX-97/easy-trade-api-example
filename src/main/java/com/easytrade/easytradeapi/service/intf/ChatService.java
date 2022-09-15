/*
 * @Description: 聊天功能服务层
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2022-01-11 21:09:56
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-15 21:19:46
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/service/intf/ChatService.java
 */
package com.easytrade.easytradeapi.service.intf;

import java.text.ParseException;
import java.util.List;
import com.easytrade.easytradeapi.constant.consists.Chat;
import com.easytrade.easytradeapi.model.ChatRecord;
import org.bson.types.ObjectId;


public interface ChatService {
    /**
     * @description: 发送、接受消息，使用websocket
     * @param {Chat} chat 一条消息的实体类
     * @return {*}
     */
    public void chat(Chat chat);

    /**
     * @description: 发送系统通知
     * @param {Chat} chat 一条消息的实体类
     * @return {*} 
     */    
    public void systemNotification(Chat chat);

    /**
     * 获取登录用户和另一位用户的聊天记录
     *
     * @param contactUserId 联系用户id
     * @param token         登录用户的JWT
     * @return {@link ChatRecord} 返回的聊天记录
     * @throws ParseException 解析异常
     */
    public ChatRecord getChatRecordByTwoUsers(ObjectId contactUserId, String token)
            throws ParseException;


    /**
     * 获取联系人
     *
     * @param token 登录用户的JWT
     * @return {@link List}<{@link String}> 返回的联系人列表
     */
    public List<String> getContacts(String token);

    /**
     * @description: 将联系人添加到用户的联系列表
     * @param {ObjectId} contactId 联系人id
     * @param {String} token 从header获取用户登录后的JWT，可以从中获取userId
     * @return {*}
     */     
    public void addContact(ObjectId contactId, String token);

    /**
     * @description: 将redis中的聊天记录保存至mongodb
     * @param {*}
     * @return {*}
     */            
    public void updateChatInRedisToMongodb();

    /**
     * @description: 检查聊天服务中输入的用户是否存在
     * @param {String} userId 需要检查的用户id
     * @return {Boolean} true为存在，false为不存在
     */
    public Boolean checkUserExist(String userId);

    /**
     * @description: 检查聊天服务中用户是否已在线
     * @param {String} id 需要检查的sessionId或用户id
     * @return {Boolean} true为在线，false为不在线
     */
    public Boolean checkUserOnline(String id);

    /**
     * 根据Token获得所有的聊天记录
     *
     * @param token 用户登录的JWT
     * @return {@link List}<{@link ChatRecord}> 返回的结果
     * @throws ParseException 解析异常
     */
    public List<ChatRecord> getAllRecordsByToken(String token) throws ParseException;

    /**
     * 根据Token获得所有的系统通知记录
     *
     * @param token 用户登录的JWT
     * @return {@link ChatRecord} 返回的聊天记录
     * @throws ParseException 解析异常
     */
    public ChatRecord getAllSystemChatRecordsByToken(String token) throws ParseException;

    ///**
    // * 创建一个新的聊天记录
    // *
    // * @param userOneId userOneId
    // * @param userTwoId userTwoId
    // */
    //public void createRecord(ObjectId userOneId, ObjectId userTwoId);
}

