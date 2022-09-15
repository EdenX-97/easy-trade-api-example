/*
 * @Description: 邮件业务工具类
 * @Author: Mo Xu
 * @Date: 2021-11-08 20:42:00
 * @LastEditors: Mo Xu
 * @LastEditTime: 2022-01-04 03:32:07
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/utils/MailUtil.java
 */

package com.easytrade.easytradeapi.utils;

import java.util.Map;
import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


@Component
public class MailUtil {
    private static JavaMailSender mailSender;

    private static TemplateEngine templateEngine;

    private static String FROM;

    // From is the mail address of sender
    @Value("${spring.mail.username}")
    private String from;

    // Use applicationContext to init jwtConfig because static class cannot implemnted by Autowired
    @Autowired
    ApplicationContext applicationContext;

    // In postConstruct init all static variables
    @PostConstruct
    public void init() {
        mailSender = applicationContext.getBean(JavaMailSender.class);
        templateEngine = applicationContext.getBean(TemplateEngine.class);
        FROM = from;
    }

    public static void sendTemplateMail(String to, String subject, String emailTemplate,
            Map<String, Object> dataMap) throws MessagingException {
        // Fill all data in template
        Context context = new Context();
        for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
            context.setVariable(entry.getKey(), entry.getValue());
        }
        String templateContent = templateEngine.process(emailTemplate, context);
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(FROM);
        helper.setTo(to);
        helper.setSubject(subject);
        System.out.println(subject);
        helper.setText(templateContent, true); // True means it is html content
        mailSender.send(message);
    }
}
