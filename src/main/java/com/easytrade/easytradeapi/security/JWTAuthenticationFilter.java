/*
 * @Description: 自定义JWT权限过滤
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2021-11-04 02:55:49
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-06 19:48:04
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/security/JWTAuthenticationFilter.java
 */
package com.easytrade.easytradeapi.security;

import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.easytrade.easytradeapi.constant.enums.ResultCodeEnum;
import com.easytrade.easytradeapi.constant.exceptions.JWTException;
import com.easytrade.easytradeapi.utils.JWTUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import io.jsonwebtoken.Claims;


public class JWTAuthenticationFilter extends BasicAuthenticationFilter {
    // 类的构造函数
    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    // 从HTTP Request获取Token信息并校验
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain chain) throws IOException, ServletException, RuntimeException {
        // 从请求中获取Header并提取Token，该过滤器只在Token已存在的情况下进行验证
        String token = request.getHeader(JWTUtil.authorization);
        if (token == null || token.isEmpty()) {
            chain.doFilter(request, response);
            return;
        }

        // 调用getAuthentication函数来对请求中的Token进行验证
        UsernamePasswordAuthenticationToken authentication = getAuthentication(request, response, token);

        // 设置认证权限
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }

    // 对Token进行验证的方法
    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request,
            HttpServletResponse response, String token) throws RuntimeException {
        // Token必须以正确前缀开头
        if (!token.startsWith("Bearer ")) {
            throw new JWTException(ResultCodeEnum.INVALID_PARAM, "Token is incorrect");
        }

        // 将Token解析为Claims，其中包含了构造该Token的信息，只有在Token中包含的secret正确的情况下才能成功解析
        Claims claims = JWTUtil.parseToken(token);

        // 从Claims中获取创建时间和过期时间
        long issuedAt = claims.getIssuedAt().getTime();
        long expirationTime = claims.getExpiration().getTime();

        // 获取当前时间
        long nowTime = System.currentTimeMillis();

        // 如果Token已过期，则报错
        if (nowTime > expirationTime) {
            throw new JWTException(ResultCodeEnum.FAILED, "Token is expired");
        }

        // // 如果目前时间超过了Token的创建时间+一半的过期时间，并且小于过期时间，则刷新Token并返回
        // if ((issuedAt + ((expirationTime - issuedAt) / 2)) < nowTime && nowTime < expirationTime) {
        //     // Create and set refreshed token
        //     String refreshToken = JWTUtil.refreshToken(claims);
        //     response.setHeader(JWTUtil.authorization, refreshToken);
        // }

        // 如果通过过期校验，则提取Claims中的用户手机号，返回认证后的Token，该Token为spring security内置类，用于权限认证
        String phone = claims.get("value").toString();
        ArrayList<GrantedAuthority> authorities = new ArrayList<>();
        return new UsernamePasswordAuthenticationToken(phone, null, authorities);
    }
}
