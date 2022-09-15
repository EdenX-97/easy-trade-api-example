/*
 * @Description: 聊天记录的保存类
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2022-01-11 22:22:20
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-11 22:25:35
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/model/ChatRecord.java
 */
package com.easytrade.easytradeapi.model;

import java.util.ArrayList;
import com.easytrade.easytradeapi.constant.consists.Chat;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;
import lombok.Data;


@Document(collection = "chatRecords")
@Data
public class ChatRecord {
    @MongoId
    private ObjectId id;

    private ObjectId userOneId;

    private ObjectId userTwoId;

    private ArrayList<Chat> chats;
}

