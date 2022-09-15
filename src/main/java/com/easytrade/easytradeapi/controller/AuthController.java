/**
 * @author: Hongzhang Liu
 * @description 用于处理实名认证相关的控制器
 * @date 4/4/202211:04 am
 */
package com.easytrade.easytradeapi.controller;

import com.easytrade.easytradeapi.constant.consists.Result;
import com.easytrade.easytradeapi.constant.enums.ResultCodeEnum;
import com.easytrade.easytradeapi.service.intf.AuthService;
import com.easytrade.easytradeapi.service.intf.UserService;
import com.easytrade.easytradeapi.utils.ReturnResultUtil;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.File;

@RestController
@Validated
public class AuthController {
    @Autowired
    UserService userService;

    @Autowired
    AuthService authService;

    /**
     * 通过姓名和身份证进行实名认证
     *
     * @param firstname 名字
     * @param lastname  姓
     * @param idcard    身份证
     * @return {@link Boolean}
     */
    @PostMapping("/auth/realNameAuth")
    public Result realNameAuth(@RequestHeader(name = "Authorization") @NotNull @NotBlank String token,
                               @RequestParam @NotNull @NotBlank String firstname,
                               @RequestParam @NotNull @NotBlank String lastname,
                               @RequestParam @NotNull @NotBlank String idcard){
        authService.realNameAuthByIDandName(token, firstname, lastname, idcard);
        return ReturnResultUtil.success("Authentication success!");
    }

    /**
     * 车商身份验证
     *
     * @param token       令牌
     * @param creditCode  信用代码
     * @param companyName 公司名称
     * @return {@link Result}
     */
    @PostMapping("/auth/dealerAuth")
    public Result dealerAuth(@RequestHeader(name = "Authorization") @NotNull @NotBlank String token,
                             @RequestParam @NotNull String creditCode,
                             @RequestParam @NotNull String companyName) {
        authService.dealerAuth(token, creditCode, companyName);
        return ReturnResultUtil.success("Authentication success!");
    }
}
