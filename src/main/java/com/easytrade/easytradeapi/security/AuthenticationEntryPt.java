/*
 * @Description: 自定义处理未登录用户（游客）的无权限
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2021-11-04 22:41:18
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-12 22:38:08
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/security/AuthenticationEntryPt.java
 */
package com.easytrade.easytradeapi.security;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.easytrade.easytradeapi.constant.enums.ResultCodeEnum;
import com.easytrade.easytradeapi.utils.ReturnResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;


@Component
public class AuthenticationEntryPt implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter()
                .write(ReturnResultUtil.failure(ResultCodeEnum.NOT_AUTHORIZED, "Cannot authorized").toString());
    }
}
