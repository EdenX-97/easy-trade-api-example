/**
 * @author: Hongzhang Liu
 * @description 用于完成实名认证的接口
 * @date 4/4/202212:54 am
 */
package com.easytrade.easytradeapi.service.intf;

import org.bson.types.ObjectId;

import java.io.File;

public interface AuthService {
    /**
     * 通过姓名和身份证进行实名认证
     *
     * @param firstname 名字
     * @param lastname  姓
     * @param idcard    身份证
     * @return {@link Boolean}
     */
    Boolean realNameAuthByIDandName(String token, String firstname, String lastname, String idcard);


    /**
     * 车商身份验证
     *
     * @param token       令牌
     * @param creditCode  信用代码
     * @param companyName 公司名称
     * @return {@link Boolean}
     */
    void dealerAuth(String token, String creditCode, String companyName);
}
