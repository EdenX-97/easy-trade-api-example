/*
 * @Description: 自定义JWT登录的过滤
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2021-11-04 02:14:17
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-06 18:33:58
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/security/JWTLoginFilter.java
 */
package com.easytrade.easytradeapi.security;

import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.easytrade.easytradeapi.constant.enums.ResultCodeEnum;
import com.easytrade.easytradeapi.utils.JWTUtil;
import com.easytrade.easytradeapi.utils.ReturnResultUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


public class JWTLoginFilter extends UsernamePasswordAuthenticationFilter {
    // 对AuthenticationManager进行实例化并设置
    private AuthenticationManager authenticationManager;

    public JWTLoginFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    // 获取Request中的authentication并构造为特定的UsernamePasswordAuthenticationToken
    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
            throws AuthenticationException {
        // 获取用户邮箱和密码
        String account = req.getParameter("account");
        String password = req.getParameter("password");

        // 返回authentication
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(account, password, new ArrayList<>()));
    }

    // 当权限验证成功时，返回Token和成功信息
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
            HttpServletResponse response, FilterChain chain, Authentication auth)
            throws IOException, ServletException {
        // 调用工具类，使用authentication中的用户名来构造Token
        String token = JWTUtil.generateToken(auth.getName());

        // 在Header中添加Token并返回成功信息
        response.addHeader(JWTUtil.authorization, token);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(ReturnResultUtil.success("Login success").toString());
    }

    // 权限验证失败时，返回错误信息
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
            HttpServletResponse response, AuthenticationException exception)
            throws IOException, ServletException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(ReturnResultUtil
                .failure(ResultCodeEnum.FAILED, exception.getMessage()).toString());
    }
}
