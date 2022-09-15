/**
 * @author: Hongzhang Liu
 * @description 用于解析json的实体类
 * @date 4/4/202211:15 pm
 */
package com.easytrade.easytradeapi.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class SensitiveAnalysis {
    // 政治敏感监测
    private List<String> 政治敏感监测;

    // 违禁品监测
    private List<String> 违禁品监测;

    // 恶意灌水监测
    private List<String> 恶意灌水监测;

    // 色情监测
    private List<String> 色情监测;

    // 辱骂监测
    private List<String> 辱骂监测;
}
