/*
 * @Description: 短信服务工具类
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2022-01-04 02:35:45
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-05 20:36:31
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/utils/SMSUtil.java
 */
package com.easytrade.easytradeapi.utils;

import javax.annotation.PostConstruct;
import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.QuerySendDetailsRequest;
import com.aliyun.dysmsapi20170525.models.QuerySendDetailsResponse;
import com.aliyun.dysmsapi20170525.models.QuerySendDetailsResponseBody.QuerySendDetailsResponseBodySmsSendDetailDTOsSmsSendDetailDTO;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.teaopenapi.models.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class SMSUtil {
    @Value("${sms.aliyun.access-key-id}")
    private String accessKeyId;

    @Value("${sms.aliyun.access-key-secret}")
    private String accessKeySecret;

    @Value("${sms.aliyun.endpoint}")
    private String endpoint;

    // @Value("${sms.aliyun.verify.sign-name}")
    // private String verifySignName;

    // @Value("${sms.aliyun.verify.template-code}")
    // private String verifyTemplateCode;

    private static Client client;

    private static String staticVerifySignName = "简单买卖";

    private static String staticVerifyTemplateCode = "SMS_244975239";

    /**
     * @description: 初始化，读取配置文件中的参数来创建client
     * @param {*}
     * @return {*}
     */
    @PostConstruct
    public void init() throws Exception {
        client = createClient(accessKeyId, accessKeySecret, endpoint);
        // staticVerifySignName = verifySignName;
        // staticVerifyTemplateCode = verifyTemplateCode;
    }

    /**
     * @description: 创建client
     * @param {String} accessKeyId 阿里云短信服务的id
     * @param {String} accessKeySecret 阿里云短信服务的密钥
     * @param {String} endpoint 阿里云短息服务的服务地址
     * @return {Client} 返回的实例
     */
    private Client createClient(String accessKeyId, String accessKeySecret, String endpoint)
            throws Exception {
        Config config = new Config();
        config.accessKeyId = accessKeyId;
        config.accessKeySecret = accessKeySecret;
        config.endpoint = endpoint;
        return new Client(config);
    }

    public static void sendOneVerifyMessage(String phone, String code) throws Exception {
        // 构造发送的短信请求
        SendSmsRequest sendReq = new SendSmsRequest();
        sendReq.setPhoneNumbers(phone);
        sendReq.setSignName(staticVerifySignName);
        sendReq.setTemplateCode(staticVerifyTemplateCode);
        sendReq.setTemplateParam("{\"code\": \"" + code + "\"}");

        System.out.println(staticVerifySignName);
        System.out.println(staticVerifyTemplateCode);

        // 发送短信
        SendSmsResponse res = client.sendSms(sendReq);
        System.out.println(res.body.message);
    }

    public static String getOneMessageResponse(String phone, String date) throws Exception {
        // 构造请求
        QuerySendDetailsRequest request = new QuerySendDetailsRequest();
        request.setPageSize(1L); // 一页记录数，1-50
        request.setCurrentPage(1L); // 起始页数
        request.setPhoneNumber(phone);
        request.setSendDate(date); // 时间格式需要为yyyyMMdd，如20220101

        // 发送请求并解析返回信息
        QuerySendDetailsResponse response = client.querySendDetails(request);
        QuerySendDetailsResponseBodySmsSendDetailDTOsSmsSendDetailDTO dto =
                response.body.smsSendDetailDTOs.smsSendDetailDTO.get(0);
        long sendStatus = dto.getSendStatus();

        // 构造返回的信息
        String message = null;
        if (sendStatus == 3) {
            message = "发送成功，接收时间为:" + dto.receiveDate;
        } else if (sendStatus == 2) {
            message = "发送失败";
        } else {
            message = "正在发送中...";
        }

        return message;
    }
}
