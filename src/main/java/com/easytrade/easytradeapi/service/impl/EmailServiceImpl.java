/*
 * @Description: 邮件业务服务层的实现类
 * @Author: Mo Xu
 * @Date: 2021-11-08 21:57:42
 * @LastEditors: Mo Xu
 * @LastEditTime: 2022-01-03 22:24:52
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/service/impl/EmailServiceImpl.java
 */
package com.easytrade.easytradeapi.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.mail.MessagingException;
import com.easytrade.easytradeapi.constant.enums.ResultCodeEnum;
import com.easytrade.easytradeapi.constant.exceptions.EmailException;
import com.easytrade.easytradeapi.constant.exceptions.UserException;
import com.easytrade.easytradeapi.model.User;
import com.easytrade.easytradeapi.repository.UserRepository;
import com.easytrade.easytradeapi.service.intf.EmailService;
import com.easytrade.easytradeapi.utils.JWTUtil;
import com.easytrade.easytradeapi.utils.MailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Claims;


@Service
public class EmailServiceImpl implements EmailService {
    @Autowired
    UserRepository userRepository;

    // 从配置文件application.properties中读取验证邮箱前置链接
    @Value("${spring.mail.verify.prelink}")
    private String prelink;

    // 从配置文件application.properties中读取验证邮箱邮件标题
    @Value("${spring.mail.verify.subject}")
    private String subject;

    @Override
    public void sendVerifyEmail(String token, String to) throws MessagingException {
        // 校验JWT
        String phone = JWTUtil.getValue(token);
        if (phone == null) {
            throw new UserException(ResultCodeEnum.INVALID_PARAM, "Token invalid");
        }

        // 检查用户是否存在
        User user = userRepository.findOneByPhone(phone);
        if (user == null) {
            throw new EmailException(ResultCodeEnum.NOT_FOUND, "Account do not exist");
        }

        // 检查用户是否已完成验证
        if (user.getEmail() != null) {
            throw new EmailException(ResultCodeEnum.FAILED, "This email already verifyed");
        }

        // 使用resources/templates下的HTML模版来发送邮件，使用了thymeleaf
        String emailTemplate = "registerTemplate";

        // 使用JWT来进行邮箱验证
        String verifyToken = JWTUtil.generateTokenForVerify(phone, to);
        String verifyLink = prelink + verifyToken; // 链接为前置链接+token

        // 设置邮件内显示的注册时间
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
        String registerTime = fmt.format(new Date());

        // 构造Map用于设置所需的参数
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("verifyLink", verifyLink);
        dataMap.put("registerTime", registerTime);

        // 发送邮件
        MailUtil.sendTemplateMail(to, subject, emailTemplate, dataMap);
    }

    @Override
    public void verifyEmail(String token) {
        // 检查Token是否以正确前缀开头
        if (!token.startsWith("Bearer ")) {
            throw new EmailException(ResultCodeEnum.INVALID_PARAM, "Token format incorrect");
        }

        // 解析Token来获取Claims，包含了用户账号
        Claims claims = JWTUtil.parseToken(token);
        String account = claims.get("account").toString();
        String email = claims.get("email").toString();

        // 检查用户是否存在
        User user = userRepository.findOneByPhone(account);
        if (user == null) {
            throw new EmailException(ResultCodeEnum.NOT_FOUND, "Account do not exist");
        }

        // 检查用户是否已完成验证
        if (user.getEmail() != null) {
            throw new EmailException(ResultCodeEnum.FAILED, "This email already verifyed");
        }

        // 设置用户的Email并保存
        user.setEmail(email);
        userRepository.save(user);
    }
}
