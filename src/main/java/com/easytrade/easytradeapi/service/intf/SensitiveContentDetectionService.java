/**
 * @author: Hongzhang Liu
 * @description 用于检测敏感词汇的接口
 * @date 4/4/202210:34 pm
 */
package com.easytrade.easytradeapi.service.intf;

import java.io.UnsupportedEncodingException;

public interface SensitiveContentDetectionService {

    /**
     * 检测敏感内容
     *
     * @param str 要检测的文本
     * @return {@link String} 过滤后的输出文本
     * @throws UnsupportedEncodingException 不支持编码异常
     */
    public String detectSensitiveContent(String str) throws UnsupportedEncodingException;
}
