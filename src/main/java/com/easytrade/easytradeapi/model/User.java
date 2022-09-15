/*
 * @Description: 基础的用户类
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2021-11-03 02:48:37
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-14 17:28:59
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/model/User.java
 */
package com.easytrade.easytradeapi.model;

import java.util.ArrayList;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.easytrade.easytradeapi.constant.consists.LoginRecord;
import com.easytrade.easytradeapi.constant.enums.SecurityQuestionEnum;
import com.easytrade.easytradeapi.constant.enums.UserStatusEnum;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;
import lombok.Data;


@Document(collection = "users")
@Data
public class User {
    @MongoId
    private ObjectId id;

    // 电话号码，注册时需要验证，同时作为账号
    @NotNull
    @Pattern(regexp = "^1[0-9]{10}$")
    @Indexed(name = "phone", unique = true)
    private String phone;

    // 密码必须包括大写字母、小写字母和数字，长度在6-20位
    @NotNull
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])[a-zA-Z0-9]{6,20}$")
    private String password;

    // 邮箱，注册时可先不设置
    @Pattern(regexp = "^([a-zA-Z0-9_.-])+@(([a-zA-Z0-9-])+\\.)+([a-zA-Z0-9]{2,4})+$")
    private String email;

    // 名，注册时可以为null，在完成实名认证时自动填入
    private String firstname;

    // 姓，注册时可以为null，在完成实名认证时自动填入
    private String lastname;

    /**
     * 安全问题
     */
    private SecurityQuestionEnum securityQuestion;

    /**
     * 安全答案
     */
    @Size(min = 1, max = 10)
    private String securityAnswer;

    // 身份证
    private String idcard;

    /**
     * 公司信用代码
     */
    @Pattern(regexp = "^[0-9A-HJ-NPQRTUWXY]{2}\\d{6}[0-9A-HJ-NPQRTUWXY]{10}$")
    private String creditCode;

    /**
     * 公司名称
     */
    private String companyName;

    // 拥有的免费广告数量
    private long freeAdNums = 0;

    // 用户登录记录
    private ArrayList<LoginRecord> loginRecords;

    // 联系人列表
    private ArrayList<ObjectId> contacts;

    // 使用过的优惠码
    private ArrayList<String> usedPromoCodes;

    // 用户所拥有的二手车广告id
    private ArrayList<ObjectId> secondHandCarAdIds;

    // 用户所拥有的新车广告id
    private ArrayList<ObjectId> newCarAdIds;

    // 用户已完成的广告id
    private ArrayList<ObjectId> completedAdIds;

    // 用户所拥有的新摩托车广告id
    private ArrayList<ObjectId> newMotorAdIds;

    // 用户所拥有的二手摩托车广告id
    private ArrayList<ObjectId> secondHandMotorAdIds;

    // 用户角色，默认为disable未激活，完成手机绑定、邮件激活、实名认证、车商认证时更改
    private String role = UserStatusEnum.DISABLE.getStatus();

    // 收藏夹列表，最多为50个
    private ArrayList<ObjectId> favoriteList = new ArrayList<>();
}
