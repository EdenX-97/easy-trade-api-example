/*
 * @Description: 自定义处理用户登陆的无权限
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2021-11-03 03:56:26
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-12 22:37:21
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/security/AccessDeny.java
 */
package com.easytrade.easytradeapi.security;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.easytrade.easytradeapi.constant.enums.ResultCodeEnum;
import com.easytrade.easytradeapi.utils.ReturnResultUtil;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;


@Component
public class AccessDeny implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException exception) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter()
                .write(ReturnResultUtil.failure(ResultCodeEnum.NOT_AUTHORIZED, "User no authorization").toString());
    }
}
