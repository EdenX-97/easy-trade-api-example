/*
 * @Description: 需要清空的用户类
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2022-01-16 18:23:17
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-17 00:10:25
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/model/UserNeedClear.java
 */
package com.easytrade.easytradeapi.model;

import java.util.Date;
import javax.validation.constraints.NotNull;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;
import lombok.Data;


@Document(collection = "userNeedClears")
@Data
public class UserNeedClear {
    // 用户的id
    @MongoId
    private ObjectId id;

    // 用户最后的登录时间
    @NotNull
    private Date lastLoginDate;

    // 用户预计清除所有广告的时间
    @NotNull
    private Date clearDate;
}

