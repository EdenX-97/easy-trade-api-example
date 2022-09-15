/*
 * @Description: 消息类，用于聊天功能
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2022-01-10 22:04:18
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-14 17:21:25
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/constant/consists/Chat.java
 */
package com.easytrade.easytradeapi.constant.consists;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.ToString;
import org.bson.types.ObjectId;


@Data
@ToString
public class Chat {
    // 发送者
    @NotNull
    private ObjectId from;

    // 接受者
    @NotNull
    private ObjectId to;

    // 消息内容
    @NotNull
    private String content;

    // 发送时间
    @NotNull
//    @Size(min = 13, max = 13)
    private String date;
}
