/*
 * @Description: 手机短信服务层的实现
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2022-01-03 19:27:52
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-06 19:24:58
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/service/impl/ShortMessageServiceImpl.java
 * /EasyBuyCar/src/main/java/com/jiandanmaiche/api/service/impl/ShortMessageServiceImpl.java
 * /EasyBuyCar/src/main/java/com/jiandanmaiche/api/service/impl/ShortMessageServiceImpl.java
 */
package com.easytrade.easytradeapi.service.impl;

import java.util.Random;
import java.util.regex.Pattern;
import com.easytrade.easytradeapi.constant.enums.ResultCodeEnum;
import com.easytrade.easytradeapi.constant.enums.UserStatusEnum;
import com.easytrade.easytradeapi.constant.exceptions.EmailException;
import com.easytrade.easytradeapi.constant.exceptions.SMSException;
import com.easytrade.easytradeapi.constant.exceptions.UserException;
import com.easytrade.easytradeapi.model.User;
import com.easytrade.easytradeapi.repository.UserRepository;
import com.easytrade.easytradeapi.service.intf.ShortMessageService;
import com.easytrade.easytradeapi.utils.RedisUtil;
import com.easytrade.easytradeapi.utils.SMSUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class ShortMessageServiceImpl implements ShortMessageService {
    @Autowired
    RedisUtil redisUtil;

    @Autowired
    UserRepository userRepository;

    @Value("${sms.verify.expire}")
    private long verifyCodeExpire;

    @Value("${sms.verify.resend}")
    private long resendExpire;
    
    public void sendVerifyMessage(String phone) {
        // 校验手机号格式
        String phoneRegex = "^1[0-9]{10}$";
        if (!Pattern.matches(phoneRegex, phone)) {
            throw new SMSException(ResultCodeEnum.INVALID_PARAM, "Wrong phone format");
        }

        // 随机创建验证码
        String code = "";
        Random randomNum = new Random();
        for (int i = 0; i < 6; i++) {
            // 取大于等于0，小于10的伪随机数
            code += randomNum.nextInt(10);
        }

        // 检查redis中是否存在验证码，一分钟之后才能重新发送验证码，即过期时间小于9分钟 = 540秒
        String verifyCodeKey = "verify:code:" + phone;
        if (redisUtil.hasKey(verifyCodeKey)) {
            long expire = redisUtil.getExpire(verifyCodeKey);
            if (expire > verifyCodeExpire - resendExpire) {
                throw new SMSException(ResultCodeEnum.FAILED, "Cannot resend verify code within 1 minutes");
            }
        }

        // 发送验证码短信
        try {
            SMSUtil.sendOneVerifyMessage(phone, code);
        } catch (Exception e) {
            throw new SMSException(ResultCodeEnum.FAILED, e.getMessage());
        }

        // 发送成功后，将验证码保存到redis，用于校验，过期时间为10分钟 = 600秒 
        redisUtil.set(verifyCodeKey, code, verifyCodeExpire);
    }

    public void verifyPhone(String phone, String verifyCode) {
        // 校验验证码格式
        String codeRegex = "^[0-9]{6}$";
        if (!Pattern.matches(codeRegex, verifyCode)) {
            throw new SMSException(ResultCodeEnum.INVALID_PARAM, "Wrong phone format");
        }

        // 从redis中获取保存的验证码，用于校验
        String savedCodeKey = "verify:code:" + phone;
        if (!redisUtil.hasKey(savedCodeKey)) {
            throw new SMSException(ResultCodeEnum.NOT_FOUND, "Verify code not exist");
        }
        String savedCode = redisUtil.get(savedCodeKey).toString();

        // 如果验证码不正确则报错
        if (!savedCode.equals(verifyCode)) {
            throw new SMSException(ResultCodeEnum.FAILED, "Input verify code wrong");
        }

        // 校验成功后，更改用户的状态
        // 获取用户，检查用户是否存在
        User user = userRepository.findOneByPhone(phone);
        if (user == null) {
            throw new UserException(ResultCodeEnum.NOT_FOUND, "User not exist");
        }

        // 只有用户状态为Disable时才能进行验证
        if (!user.getRole().equals(UserStatusEnum.DISABLE.getStatus())) {
            throw new EmailException(ResultCodeEnum.FAILED, "Cannot verify user that is not disable");
        }

        // 更改用户状态并保存
        user.setRole(UserStatusEnum.VERIFIED.getStatus());
        userRepository.save(user);

        // 删除redis中的数据
        redisUtil.del(savedCodeKey);
    }

    public String getVerifyCodeStatus(String phone, String date) {
        String status = "";
        try {
            status = SMSUtil.getOneMessageResponse(phone, date);
        } catch (Exception e) {
            throw new UserException(ResultCodeEnum.FAILED, "Find verify code failed: " + e.getMessage());
        }
        return status;
    }
}

