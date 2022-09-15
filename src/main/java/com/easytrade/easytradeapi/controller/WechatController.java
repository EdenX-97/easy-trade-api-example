/**
 * @author: Hongzhang Liu
 * @description 微信支付控制器
 * @date 20/7/2022 3:26 pm
 */
package com.easytrade.easytradeapi.controller;

import com.alibaba.fastjson.JSONArray;
import com.easytrade.easytradeapi.constant.consists.Result;
import com.easytrade.easytradeapi.service.intf.WechatPaySerivce;
import com.easytrade.easytradeapi.utils.AesUtil;
import com.easytrade.easytradeapi.utils.ReturnResultUtil;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;

@RestController
@Validated
public class WechatController {

    @Autowired
    WechatPaySerivce wechatPaySerivce;

    /**
     * 创建订单
     *
     * @param id    广告id
     * @param token 令牌
     * @return {@link String} 二维码url
     * @throws IOException ioexception
     */
    @PostMapping("/wechat/pay")
    @Transactional
    public Result createOrder(@RequestParam @NotNull ObjectId id,
                              @RequestHeader(name = "Authorization") @NotNull @NotBlank String token) throws Exception {
        System.out.println("当前广告id为：" + id);
        return ReturnResultUtil.success(wechatPaySerivce.createOrder(id, token));
    }

    /**
     * 微信支付异步通知
     */
    @PostMapping("/wechat/pay/notify.do")
    public Result payNotify(@RequestBody String notifyData) throws GeneralSecurityException, IOException {
        return ReturnResultUtil.success(wechatPaySerivce.payNotify(notifyData));
    }
}
