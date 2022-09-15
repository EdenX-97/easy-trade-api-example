/**
 * @author: Hongzhang Liu
 * @description 用于敏感内容检测的控制器
 * @date 4/4/202210:40 pm
 */
package com.easytrade.easytradeapi.controller;

import com.easytrade.easytradeapi.constant.consists.Result;
import com.easytrade.easytradeapi.service.intf.SensitiveContentDetectionService;
import com.easytrade.easytradeapi.utils.ReturnResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.io.UnsupportedEncodingException;
import java.util.List;

@RestController
public class SensitiveContentDetectionController {

    @Autowired
    SensitiveContentDetectionService sensitiveContentDetectionService;

    /**
     * 检测敏感内容
     *
     * @param str 要检测的文本
     * @return {@link String} 过滤后的输出文本
     * @throws UnsupportedEncodingException 不支持编码异常
     */
    @PostMapping("/detect/detectSensitiveContent")
    public Result detectSensitiveContent(@RequestParam @NotNull String str) throws UnsupportedEncodingException {
        return ReturnResultUtil.success(sensitiveContentDetectionService.detectSensitiveContent(str));
    }
}
