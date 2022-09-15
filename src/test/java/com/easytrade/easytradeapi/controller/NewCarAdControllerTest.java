/**
 * @author: Hongzhang Liu
 * @description 新车广告控制器测试
 * @date 14/4/2022 4:04 pm
 */
package com.easytrade.easytradeapi.controller;

import com.easytrade.easytradeapi.utils.JWTUtil;
import com.easytrade.easytradeapi.utils.MailUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import javax.mail.MessagingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
public class NewCarAdControllerTest {
    @Value("${spring.mail.verify.prelink}")
    private String prelink;

    // 从配置文件application.properties中读取验证邮箱邮件标题
    @Value("${spring.mail.verify.subject}")
    private String subject;

    //@Test
    //void send() throws MessagingException {
    //    // 使用resources/templates下的HTML模版来发送邮件，使用了thymeleaf
    //    String emailTemplate = "registerTemplate";
    //
    //    // 使用JWT来进行邮箱验证
    //    String verifyLink = prelink + "1111111"; // 链接为前置链接+token
    //
    //    // 设置邮件内显示的注册时间
    //    SimpleDateFormat fmt = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
    //    String registerTime = fmt.format(new Date());
    //
    //    // 构造Map用于设置所需的参数
    //    Map<String, Object> dataMap = new HashMap<>();
    //    dataMap.put("verifyLink", verifyLink);
    //    dataMap.put("registerTime", registerTime);
    //
    //    // 发送邮件
    //    String to = "wsxumo@outlook.com";
    //    MailUtil.sendTemplateMail(to, subject, emailTemplate, dataMap);
    //}

}
