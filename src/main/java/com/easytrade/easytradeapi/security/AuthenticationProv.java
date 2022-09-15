/*
 * @Description: 自定义处理用户登录权限验证
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2021-11-03 03:56:39
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-16 19:19:38
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/security/AuthenticationProv.java
 */
package com.easytrade.easytradeapi.security;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.regex.Pattern;

import com.easytrade.easytradeapi.constant.consists.LoginRecord;
import com.easytrade.easytradeapi.constant.enums.ResultCodeEnum;
import com.easytrade.easytradeapi.constant.exceptions.UserException;
import com.easytrade.easytradeapi.model.User;
import com.easytrade.easytradeapi.model.UserNeedClear;
import com.easytrade.easytradeapi.repository.UserNeedClearRepository;
import com.easytrade.easytradeapi.repository.UserRepository;
import com.easytrade.easytradeapi.service.intf.UserService;
import com.easytrade.easytradeapi.utils.IPUtil;
import com.easytrade.easytradeapi.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;


@Component
public class AuthenticationProv implements AuthenticationProvider {
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserNeedClearRepository userNeedClearRepository;

    @Value("${user.clear.days}")
    private long clearUserDays;

    @Value("${user.clear.buffer}")
    private long bufferDays;

    // 验证用户登录信息
    @Override
    public Authentication authenticate(Authentication authentication) {
        // 从传入的authentication中提取账号和密码
        String account = authentication.getName();
        String password = authentication.getCredentials().toString();

        // 根据用户名从服务层中获取用户详情类（为spring security的内置类）
        UserDetails userDetail = userService.loadUserByUsername(account);
        // 如果用户不存在，则报错，进入异常处理
        if (userDetail == null) {
            throw new UserException(ResultCodeEnum.NOT_FOUND, "User not exist");
        }

        // 如果是验证码登录，则校验验证码
        String verifyCodeRegex = "^[0-9]{6}$";
        if (Pattern.matches(verifyCodeRegex, password)) {
            // 检查验证码是否正确
            // 从redis中提取用户验证信息，key为register:account:phone， value为验证码
            String registerVerifyKey = "verify:code:" + account;
            if (!redisUtil.hasKey(registerVerifyKey)) {
                throw new UserException(ResultCodeEnum.NOT_FOUND,
                        "Have not sent verify code or expire");
            }
            String savedVerifyCode = redisUtil.get(registerVerifyKey).toString();
            if (!savedVerifyCode.equals(password)) {
                throw new UserException(ResultCodeEnum.FAILED, "Verify code is wrong");
            }

            // 验证码正确后，删除redis数据库中的验证码
            redisUtil.del(registerVerifyKey);
        } else {
            // 密码登录，如果登录时输入的密码与数据库中密码不符合，报错
            if (!passwordEncoder.matches(password, userDetail.getPassword())) {
                throw new UserException(ResultCodeEnum.FAILED, "Password incorrect");
            }
        }

        // 登录成功，添加登录记录
        User user = userRepository.findOneByPhone(account);
        ArrayList<LoginRecord> loginRecords = user.getLoginRecords();
        Date nowTime = new Date();
        if (loginRecords == null) {
            // 如果用户从未登录，则新建list
            loginRecords = new ArrayList<LoginRecord>();
        } else {
            // 如果用户有登录记录，判断上一次记录相差时间，同一用户10秒内不可重复登录，防止恶意频繁登录
            Date lastLoginDate = loginRecords.get(loginRecords.size() - 1).getLoginDate();
            if (Duration.between(lastLoginDate.toInstant(), nowTime.toInstant()).toSeconds() < 10) {
                throw new UserException(ResultCodeEnum.FAILED, "Cannot login frequently");
            }

            // 对用户登录记录进行判断，如果用户在清空广告期限内登录，则将用户移出取消广告列表
            long durationDays = Duration.between(lastLoginDate.toInstant(), nowTime.toInstant()).toDays();
            if (durationDays >= clearUserDays - bufferDays && durationDays <= clearUserDays) {
                // 如果存在，说明用户已进入清空列表，将其移出
                UserNeedClear userNeedCelar = userNeedClearRepository.findOneById(user.getId());
                if (userNeedCelar != null) {
                    userNeedClearRepository.delete(userNeedCelar);
                }
            }
        }

        // 将新登录记录保存至mongodb
        LoginRecord loginRecord = new LoginRecord();
        loginRecord.setLoginIp(IPUtil.getIpAddr());
        loginRecord.setLoginDate(nowTime);
        loginRecords.add(loginRecord);
        user.setLoginRecords(loginRecords);
        userRepository.save(user);

        // 从用户详情类中获取authorities并且构造返回权限验证信息
        Collection<? extends GrantedAuthority> authorities = userDetail.getAuthorities();
        Authentication userToken =
                new UsernamePasswordAuthenticationToken(account, password, authorities);

        return userToken;
    }

    // 用于支持authentication类的设置
    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }
}
