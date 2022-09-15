/*
 * @Description: Spring security框架的配置类
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2021-11-03 04:05:43
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-05-06 15:20:44
 * 
 * @FilePath: /undefined/Users/xumo/Entrepreneurship/easyTradeAPI/EasyTradeApi/src/main/java/com/easytrade/easytradeapi/config/SecurityConfig.java
 */
package com.easytrade.easytradeapi.config;

import com.easytrade.easytradeapi.security.*;
import com.easytrade.easytradeapi.service.intf.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.filter.CorsFilter;


@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    UserService userService;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    // 自定义处理用户登录权限验证
    @Autowired
    AuthenticationProv authenticationProvider;

    // 自定义处理未登录用户（游客）的无权限
    @Autowired
    AuthenticationEntryPt authenticationEnryPoint;

    // 登出服务在前端完成
    // // 自定义处理用户登出
    // @Autowired
    // AuthenticationLogout authenticationLogout;

    // 自定义处理用户登陆的无权限
    @Autowired
    AccessDeny accessDeny;

    @Autowired
    ExceptionFilter exceptionFilter;

    // 设置全权限接口，即不需要权限就可以调用
    private static final String[] AUTH_WHITELIST = {
        "/user/register", 
        "/user/verifyEmail",
        "/api-docs/**", 
        "/swagger-ui/**", 
        "/swagger-ui.html", 
        "/example/car/**",
        "/car/**",
            "/motor/**",
        "/area/**",
        "/sms/**",
        "/pay",
        "/promoCode/**",
        "/chat/**",
        "/ws/**",
        "/websocket/**",
        "/vehicle/**",
        "/alipay/**",
        "/auth/**",
        "/email/**",
        "/report/**",
        "/detect/**",
        "/tradeRecord/**",
        "/user/getUserByToken",
        "/user/checkUserNeedClear", 
        "/user/getOneById",
        "/user/starCar",
        "/user/cancelStarCar",
        "/vehicle/getFavoriteUsers",
        "/user/getFavoriteVehicles",
        "/user/setSecurityQuestionAndAnswer",
        "/user/getSecurityQuestion",
        "/chat/getChatRecordByTwoUsers",
        "/user/getUserSecondHandCarAds",
        "/vehicle/getVehicleById",
        "/vehicle/getVehicleTitleById",
        "/vehicle/getVehicleTypeById",
        "/user/getUserCompletedCarAds",
        "/vehicle/getAdPrice",
        "/vehicle/getPriceLevel",
        "/vehicle/getFilterCars",
        "/example/motor/getExampleMotorById",
        "/vehicle/getVehicleType",
        "/example/motor/getAllBrands",
        "/example/motor/getAllSeriesByBrand",
        "/example/motor/getModelsBySeries",
        "/example/motor/getMotorByModel",
        "/wechat/pay/**",
            "/wechat/pay/notify/**"
    };

    // 管理员才有权限调用的接口
    private static final String[] AUTH_ADMIN = {
        "/vehicle/updateAdInRedisToMongodb",
        "/example/car/upload",
        "/area/upload",
        "/promoCode/cancel",
        "/chat/systemNotification",
        "/chat/getSystemNotification",
        "/chat/getChatRecordByTwoUsers",
        "/chat/checkUserExist",
        "/chat/checkUserOnline",
        "/user/checkUserNeedClear",
        "/user/clearUserNeedClear",
        "/car/secondHandCar/cancelAdsOverOneYeaer",
        "/car/newCar/cancelAdsOverOneYeaer",
        "/alipay/checkRefundSuccess",
        "/alipay/refundAd",
        "/alipay/cancelTradesOverTime",

        "/area/addArea",
        "/area/deleteAreaByCode",
        "/area/updateAreaByCode",
        "/car/newCar/cancelNewCarAd",
        "/car/newcar/addNewCarAd",
        "/car/newcar/deleteNewCarA",
        "/car/newcar/updateNewCarAd",
        "/promoCode/create",
        "/promoCode/cancel",
        "/promoCode/addPromoCode",
        "/promoCode/deletePromoCodeByCode",
        "/promoCode/updatePromoCodeByCode",
        "/report/cancelReportByObjectId",
        "/report/getAllReportRecordBySubmitUserId",
        "/report/getAllReportRecordByReportedUserId",
        "/car/secondHandCar/createSecondHandCarAd",
        "/car/secondHandCar/postSecondHandCarAd",
        "/car/secondHandCar/cancelSecondHandCarAd",
        "/car/secondHandCar/addSecondHandCarAd",
        "/car/secondHandCar/deleteSecondHandCarAdById",
        "/car/secondHandCar/updateSecondHandCarAdById",
        "/detect/detectSensitiveWord",
        "/detect/detectAdv",
        "/tradeRecord/addTradeRecord",
        "/tradeRecord/deleteTradeRecordById",
        "/tradeRecord/updateTradeRecordById",
         "/example/motor/upload"
    };

    // 用户可以调用的接口
    private static final String[] AUTH_USER = {
            "/chat/sendChat",
            "/chat/getContacts",
            "/chat/addContact",
        "/promoCode/use",
        "/report/submitReport",
        "/report/cancelReportByObjectId",
        "/report/getAllReportRecordBySubmitUserId",
        "/report/getAllReportRecordBySubmitUserId",
        "/car/secondHandCar/createSecondHandCarAd",
        "/car/secondHandCar/postSecondHandCarAd",
        "/car/secondHandCar/cancelSecondHandCarAd",
        "/car/secondHandCar/addSecondHandCarAd",
        "/car/secondHandCar/deleteSecondHandCarAdById",
        "/car/secondHandCar/updateSecondHandCarAdById",
        "/car/secondHandCar/completeSecondHandCarAd",
        "/motor/secondHandMotor/createSecondHandMotorAd"
    };

    // 车商可以调用的接口
    private static final String[] AUTH_DEALER = {
        "/chat/sendChat",
        "/chat/getContacts",
        "/chat/addContact",
        "/car/newCar/createNewCarAd",
        "/car/newCar/postNewCarAd",
        "/car/newCar/cancelNewCarAd",
        "/car/newCar/completeNewCarAd",
        "/car/newcar/addNewCarAd",
        "/car/newcar/deleteNewCarAd",
        "/car/newcar/updateNewCarAd",
        "/promoCode/use",
        "/report/submitReport",
        "/report/cancelReportByObjectId",
        "/report/getAllReportRecordBySubmitUserId",
        "/car/secondHandCar/completeSecondHandCarAd",
        "/motor/newMotor/createNewMotorAd",
        "/motor/secondHandMotor/createSecondHandMotorAd"
    };

    //// 用户和车商都可以调用的接口
    //private static final String[] AUTH_USER_DEALER = {
    //    "/car/secondHandCar/completeSecondHandCarAd",
    //        "/motor/newMotor/createNewMotorAd",
    //        "/motor/secondHandMotor/createSecondHandMotorAd"
    //};

    // 配置authenticationProvider
    @Override
    public void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authenticationProvider);
    }

    // Spring security配置
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        // 禁用CSRF保护，因为服务端提供API服务
        httpSecurity
            .csrf().disable();

        // 配置其他请求
        httpSecurity
            .authorizeRequests().antMatchers(AUTH_WHITELIST).permitAll() // AUTH_WHITELIST中的地址不需要权限验证
            .antMatchers(AUTH_ADMIN).hasRole("ADMIN")
            .antMatchers(AUTH_USER).hasRole("USER")
            .antMatchers(AUTH_DEALER).hasRole("DEALER")
            //.antMatchers(AUTH_USER).hasRole("AUTH_USER_DEALER")
            //.antMatchers(AUTH_DEALER).hasRole("AUTH_USER_DEALER")
            .anyRequest().authenticated() // 配置其他需要权限验证地址

            .and()
                .addFilter(new JWTLoginFilter(authenticationManager())) // Token登录过滤
                .addFilter(new JWTAuthenticationFilter(authenticationManager())) // Token权限验证过滤
                .addFilterBefore(exceptionFilter, CorsFilter.class)
                .formLogin().loginProcessingUrl("/login").permitAll() // 配置登录路径，允许所有用户访问
            .and()
                .exceptionHandling() // 配置异常处理
                .accessDeniedHandler(accessDeny) // 处理用户登陆的无权限
                .authenticationEntryPoint(authenticationEnryPoint) // 处理未登录用户（游客）的无权限
            .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 使用JWT，因此不需要使用session
        ;
    }
}
