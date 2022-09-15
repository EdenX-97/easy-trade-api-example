/*
 * @Description: 优惠码服务层实现类
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2022-01-10 03:23:17
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-10 20:31:16
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/service/impl/PromoCodeServiceImpl.java
 */
package com.easytrade.easytradeapi.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import com.easytrade.easytradeapi.constant.enums.ResultCodeEnum;
import com.easytrade.easytradeapi.constant.enums.UserStatusEnum;
import com.easytrade.easytradeapi.constant.exceptions.NewCarException;
import com.easytrade.easytradeapi.constant.exceptions.PromoCodeException;
import com.easytrade.easytradeapi.model.PromoCode;
import com.easytrade.easytradeapi.model.User;
import com.easytrade.easytradeapi.repository.PromoCodeRepository;
import com.easytrade.easytradeapi.repository.UserRepository;
import com.easytrade.easytradeapi.service.intf.PromoCodeService;
import com.easytrade.easytradeapi.utils.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class PromoCodeServiceImpl implements PromoCodeService {
    @Autowired
    PromoCodeRepository promoCodeRepository;

    @Autowired
    UserRepository userRepository;

    public String createPromoCode(long freeAdNums, long maxUseTimes, long expireDay,
            UserStatusEnum suitableUserStatus) {
        // 首先随机生成六位的数字+大写字母代码，为了避免混淆，没有01ILO
        String code = "";
        char[] includeWords = "23456789ABCDEFGHJKMNPQRSTUVWXYZ".toCharArray();
        for (int i = 0; i < 6; i++) {
            char word = includeWords[(int) (Math.random() * 31)];
            code += String.valueOf(word);
        }

        // 创建并保存优惠码至数据库
        Date nowTime = new Date();
        PromoCode promoCode = new PromoCode();
        promoCode.setCode(code);
        promoCode.setCreateDate(nowTime);
        promoCode.setExpireDate(new Date(nowTime.getTime() + expireDay * 86400000));
        promoCode.setFreeAdNums(freeAdNums);
        promoCode.setMaxUseTimes(maxUseTimes);
        promoCode.setSuitableUserStatus(suitableUserStatus.getStatus());
        promoCodeRepository.save(promoCode);
        return code;
    }

    public void usePromoCode(String code, String token) {
        // 检查用户是否存在
        String account = JWTUtil.getValue(token);
        if (!userRepository.existsByPhone(account)) {
            throw new NewCarException(ResultCodeEnum.NOT_FOUND, "User not exist");
        }

        // 校验输入的优惠码格式
        if (!Pattern.matches("^[2-9A-HJ-KMNP-Z]{6}$", code)) {
            throw new PromoCodeException(ResultCodeEnum.INVALID_PARAM,
                    "Input promo code format wrong");
        }

        // 检查用户是否已经使用过该优惠码
        User user = userRepository.findOneByPhone(account);
        ArrayList<String> usedPromoCodes = user.getUsedPromoCodes();
        if (usedPromoCodes == null) {
            usedPromoCodes = new ArrayList<>();
        }
        if (usedPromoCodes.contains(code)) {
            throw new PromoCodeException(ResultCodeEnum.FAILED, "User have used this promo code");
        }

        // 检查优惠码是否存在/次数用完/过期/用户是否符合
        PromoCode promoCode = promoCodeRepository.findOneByCode(code);
        Date nowTime = new Date();
        if (promoCode == null) {
            throw new PromoCodeException(ResultCodeEnum.NOT_FOUND, "Promo code not exist");
        }
        if (promoCode.getMaxUseTimes() != -1 && promoCode.getMaxUseTimes() <= promoCode.getUsedTimes() + 1) {
            throw new PromoCodeException(ResultCodeEnum.FAILED, "Promo code over max use times");
        }
        if (promoCode.getExpireDate().before(nowTime)) {
            throw new PromoCodeException(ResultCodeEnum.FAILED, "Promo code expire");
        }
        if (!promoCode.getSuitableUserStatus().equals(user.getRole())) {
            System.out.println(promoCode.getSuitableUserStatus());
            System.out.println(user.getRole());
            throw new PromoCodeException(ResultCodeEnum.FAILED,
                    "Promo code cannot use by this kind of user");
        }

        // 通过检查，增加该优惠码的被使用次数并保存至数据库
        promoCode.setUsedTimes(promoCode.getUsedTimes() + 1);
        promoCodeRepository.save(promoCode);

        // 将优惠码添加至用户使用过的优惠码列表，然后增加用户的免费广告数量并保存
        usedPromoCodes.add(code);
        user.setUsedPromoCodes(usedPromoCodes);
        user.setFreeAdNums(user.getFreeAdNums() + promoCode.getFreeAdNums());
        userRepository.save(user);
    }

    public void cancelPromoCode(String code) {
        // 校验输入的优惠码格式
        if (!Pattern.matches("^[2-9A-HJ-KMNP-Z]{6}$", code)) {
            throw new PromoCodeException(ResultCodeEnum.INVALID_PARAM,
                    "Input promo code format wrong");
        }

        // 检查优惠码是否存在
        PromoCode promoCode = promoCodeRepository.findOneByCode(code);
        if (promoCode == null) {
            throw new PromoCodeException(ResultCodeEnum.NOT_FOUND, "Promo code not exist");
        }

        // 将该优惠码的最大使用次数设为0，并保存
        promoCode.setMaxUseTimes(0);
        promoCodeRepository.save(promoCode);
    }

    @Override
    public List<PromoCode> getAllPromoCodeByFreeAdNums(long freeAdNums) {
        // 免费标准广告小于0输入不合法，抛出异常
        if(freeAdNums < 0){
            throw new PromoCodeException(ResultCodeEnum.FAILED, "invalid freeAdNums, can not be negtive");
        }
        return promoCodeRepository.findAllByFreeAdNums(freeAdNums);
    }

    @Override
    public List<PromoCode> getAllPromoCodeByFreeAdNumsBetween(long freeAdNums1, long freeAdNums2) {
        // 免费标准广告次数输入要大于等于0
        if(freeAdNums1 < 0 || freeAdNums2 < 0){
            throw new PromoCodeException(ResultCodeEnum.INVALID_PARAM, "invalid freeAdNums, can not be negtive");
        }
        return promoCodeRepository.findAllByFreeAdNumsBetween(freeAdNums1, freeAdNums2);
    }

    @Override
    public List<PromoCode> getAllPromoCodeByCreateDateBetween(String date1, String date2) {
        // 设置Date类型格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date input1;
        Date input2;
        try {
            // 将字符串按照指定格式转变为Date类型
            input1 = sdf.parse(date1);
            input2 = sdf.parse(date2);
            return promoCodeRepository.findAllByCreateDateBetween(input1, input2);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    @Override
    public List<PromoCode> getAllPromoCodeByExpireDateBetween(String date1, String date2) {
        // 设置Date类型格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date input1;
        Date input2;
        try {
            // 将字符串按照指定格式转变为Date类型
            input1 = sdf.parse(date1);
            input2 = sdf.parse(date2);
            return promoCodeRepository.findAllByExpireDateBetween(input1, input2);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    @Override
    public List<PromoCode> getAllPromoCodeByUsedTimesBetween(long times1, long times2) {
        // 判断使用次数区间合法性
        if(times1 < 0 || times2 < 0){
            throw new PromoCodeException(ResultCodeEnum.INVALID_PARAM, "used times can not be negtive");
        }
        if(times1 > times2){
            throw new PromoCodeException(ResultCodeEnum.INVALID_PARAM, "times2 has to be greater than times1");
        }
        return promoCodeRepository.findAllByUsedTimesBetween(times1, times2);
    }

    @Override
    public List<PromoCode> getAllPromoCodeByMaxUsedTimesBetween(long times1, long times2) {
        // 判断使用次数区间合法性
        if(times1 < 0 || times2 < 0){
            throw new PromoCodeException(ResultCodeEnum.INVALID_PARAM, "used times can not be negtive");
        }
        if(times1 > times2){
            throw new PromoCodeException(ResultCodeEnum.INVALID_PARAM, "times2 has to be greater than times1");
        }
        return promoCodeRepository.findAllByMaxUseTimesBetween(times1, times2);
    }

    @Override
    public List<PromoCode> getAllPromoCodeBySuitableUserStatus(String status) {
        return promoCodeRepository.findAllBySuitableUserStatus(status);
    }

    @Override
    public void addPromoCode(String code, long freeAdNums, String createDate, String expireDate, long usedTimes, long maxUseTimes, String suitableUserStatus) {
        // 判断重复以及输入数据的合法性
        if(promoCodeRepository.existsByCode(code)){
           throw new PromoCodeException(ResultCodeEnum.FAILED, "already exists this promocode in database");
        }
        if(freeAdNums < 0){
            throw new PromoCodeException(ResultCodeEnum.INVALID_PARAM, "freeAdNums can not be negtive");
        }
        if(usedTimes < 0){
            throw new PromoCodeException(ResultCodeEnum.INVALID_PARAM, "usedTimes can not be negtive");
        }
        if(maxUseTimes < -1){
            throw new PromoCodeException(ResultCodeEnum.INVALID_PARAM, "usedTimes has to be greater than -1");
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date newCreateDate = sdf.parse(createDate);
            Date newExpireDate = sdf.parse(expireDate);
            PromoCode promoCode = new PromoCode();
            promoCode.setCode(code);
            promoCode.setFreeAdNums(freeAdNums);
            promoCode.setCreateDate(newCreateDate);
            promoCode.setExpireDate(newExpireDate);
            promoCode.setUsedTimes(usedTimes);
            promoCode.setMaxUseTimes(maxUseTimes);
            promoCode.setSuitableUserStatus(suitableUserStatus);
            promoCodeRepository.save(promoCode);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deletePromoCodeByCode(String code) {
        // 判断数据库中是否存在输入的代码号
        if(!promoCodeRepository.existsByCode(code)){
            throw new PromoCodeException(ResultCodeEnum.NOT_FOUND, "do not have this PromoCode in database");
        }
        promoCodeRepository.deleteByCode(code);
    }

    public void updatePromoCodeByCode(String code, long freeAdNums, String createDate, String expireDate, long usedTimes, long maxUseTimes, String suitableUserStatus){
        // 判断重复以及输入数据的合法性
        if(!promoCodeRepository.existsByCode(code)){
            throw new PromoCodeException(ResultCodeEnum.NOT_FOUND, "do not have this PromoCode in database");
        }
        if(freeAdNums < 0){
            throw new PromoCodeException(ResultCodeEnum.INVALID_PARAM, "freeAdNums can not be negtive");
        }
        if(usedTimes < 0){
            throw new PromoCodeException(ResultCodeEnum.INVALID_PARAM, "usedTimes can not be negtive");
        }
        if(maxUseTimes < -1){
            throw new PromoCodeException(ResultCodeEnum.INVALID_PARAM, "usedTimes has to be greater than -1");
        }
        PromoCode promoCode = promoCodeRepository.findOneByCode(code);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            promoCode.setFreeAdNums(freeAdNums);
            Date newCreateDate = sdf.parse(createDate);
            Date newExpireDate = sdf.parse(expireDate);
            promoCode.setCreateDate(newCreateDate);
            promoCode.setExpireDate(newExpireDate);
            promoCode.setUsedTimes(usedTimes);
            promoCode.setMaxUseTimes(maxUseTimes);
            promoCode.setSuitableUserStatus(suitableUserStatus);
            promoCodeRepository.save(promoCode);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}

