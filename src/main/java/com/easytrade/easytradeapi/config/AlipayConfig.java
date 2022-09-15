/*
 * @Description: 支付宝服务配置类
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2022-01-08 00:54:03
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-18 01:37:04
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/config/AlipayConfig.java
 */
package com.easytrade.easytradeapi.config;

import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.kernel.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AlipayConfig {
    @Value("${alipay.protocal}")
    private String protocal;

    @Value("${alipay.gatewayHost}")
    private String gatewayHost;

    @Value("${alipay.signType}")
    private String signType;

    @Value("${alipay.appId}")
    private String appId;

    @Value("${alipay.merchantPrivateKey}")
    private String merchantPrivateKey;

    @Value("${alipay.alipayPublicKey}")
    private String alipayPublicKey;

    @Bean
    public Config getConfig() {
        Config config = new Config();
        config.protocol = this.protocal;
        config.gatewayHost = this.gatewayHost;
        config.signType = this.signType;
        config.appId = this.appId;
        config.merchantPrivateKey = this.merchantPrivateKey;
        config.alipayPublicKey = this.alipayPublicKey;
        Factory.setOptions(config);
        
        return config;
    }
}
