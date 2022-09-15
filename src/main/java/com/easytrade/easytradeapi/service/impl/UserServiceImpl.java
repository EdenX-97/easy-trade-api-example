/*
 * @Description: 用户服务层的实现类
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2021-11-03 03:54:18
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-19 01:05:49
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/service/impl/UserServiceImpl.java
 */
package com.easytrade.easytradeapi.service.impl;

import java.time.Duration;
import java.util.*;
import java.util.regex.Pattern;
import com.easytrade.easytradeapi.constant.consists.LoginRecord;
import com.easytrade.easytradeapi.constant.enums.*;
import com.easytrade.easytradeapi.constant.exceptions.JWTException;
import com.easytrade.easytradeapi.constant.exceptions.UserException;
import com.easytrade.easytradeapi.model.NewCarAd;
import com.easytrade.easytradeapi.model.SecondHandCarAd;
import com.easytrade.easytradeapi.model.User;
import com.easytrade.easytradeapi.model.UserNeedClear;
import com.easytrade.easytradeapi.repository.NewCarAdRepository;
import com.easytrade.easytradeapi.repository.SecondHandCarAdRepository;
import com.easytrade.easytradeapi.repository.UserNeedClearRepository;
import com.easytrade.easytradeapi.repository.UserRepository;
import com.easytrade.easytradeapi.service.intf.EmailService;
import com.easytrade.easytradeapi.service.intf.NewCarAdService;
import com.easytrade.easytradeapi.service.intf.SecondHandCarAdService;
import com.easytrade.easytradeapi.service.intf.UserService;
import com.easytrade.easytradeapi.utils.JWTUtil;
import com.easytrade.easytradeapi.utils.RedisUtil;
import io.jsonwebtoken.Claims;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import javax.mail.MessagingException;


@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NewCarAdRepository newCarAdRepository;

    @Autowired
    private NewCarAdService newCarAdService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private SecondHandCarAdService secondHandCarAdService;

    @Autowired
    private SecondHandCarAdRepository secondHandCarAdRepository;

    @Autowired
    private UserNeedClearRepository userNeedClearRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private RedisUtil redisUtil;

    @Value("${chat.system.contactId}")
    private ObjectId systemContactId;

    @Value("${user.clear.days}")
    private long clearUserDays;

    @Value("${user.clear.buffer}")
    private long bufferDays;

    // redis数据自定义过期时间，用于点击、浏览、收藏，默认为7天
    @Value("${spring.redis.custom.expire}")
    private long expire;

    @Value("${user.security.max}")
    private long securityMax;

    @Value("${user.security.min}")
    private long securityMin;

    @Override
    public UserDetails loadUserByUsername(String account) {
        // 获取用户
        User user = getUser(account);

        // 构建一个spring security的内置类，用来保存用户信息，并返回
        return new org.springframework.security.core.userdetails.User(user.getPhone(),
                user.getPassword(),
                AuthorityUtils.commaSeparatedStringToAuthorityList(user.getRole()));
    }

    @Override
    public User getUserByToken(String token, Boolean ifWithPassword) {
        // 校验token的合法性
        if (!token.startsWith("Bearer ")) {
            throw new JWTException(ResultCodeEnum.INVALID_PARAM, "Token is incorrect");
        }

        // 将Token解析为Claims，其中包含了构造该Token的信息，只有在Token中包含的secret正确的情况下才能成功解析
        Claims claims = JWTUtil.parseToken(token);

        // 从Claims中获取创建时间和过期时间
        long issuedAt = claims.getIssuedAt().getTime();
        long expirationTime = claims.getExpiration().getTime();

        // 获取当前时间
        long nowTime = System.currentTimeMillis();

        // 如果Token已过期，则报错
        if (nowTime > expirationTime) {
            throw new JWTException(ResultCodeEnum.FAILED, "Token is expired");
        }

        // 如果通过过期校验，则提取Claims中的用户手机号
        String phone = claims.get("value").toString();

        // 校验并获取用户
        User user = getUser(phone);

        // 为安全性，将密码设空
        if (!ifWithPassword) {
            user.setPassword(null);
        }

        return user;
    }

    @Override
    public void register(User user, Integer verifyCode) {
        // 校验用户是否已存在
        String phone = user.getPhone();
        if (userRepository.existsByPhone(phone)) {
            throw new UserException(ResultCodeEnum.FAILED, "Register phone exist");
        }

        // 检查验证码是否正确
        checkVerifyCode(phone, verifyCode);

        // 验证码正确后，开始将用户保存至数据库，同时删除redis数据库中的验证码
        String registerVerifyKey = "verify:code:" + phone;
        redisUtil.del(registerVerifyKey);

        // 设置默认添加到联系人的系统通知
        ArrayList<ObjectId> contacts = new ArrayList<ObjectId>();
        contacts.add(systemContactId);

        // 新建用户类来保存，以免传入的user包含其他信息，防止某些恶意调用API
        User savedUser = new User();
        savedUser.setPhone(phone);
        savedUser.setRole(UserStatusEnum.VERIFIED.getStatus());
        // 密码需要加密
        savedUser.setPassword(passwordEncoder.encode(user.getPassword()));
        savedUser.setContacts(contacts);
        userRepository.save(savedUser);
    }

    @Override
    public void changePassword(String token, Integer verifyCode, String newPassword) {
        // 校验并根据token获取用户手机号
        String phone = JWTUtil.getValue(token);
        if (phone == null) {
            throw new UserException(ResultCodeEnum.INVALID_PARAM, "Token invalid");
        }

        // 检查验证码是否正确
        checkVerifyCode(phone, verifyCode);

        // 校验并获取用户
        User user = getUser(phone);

        // 校验新密码是否合法
        String passwordRegex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])[a-zA-Z0-9]{6,20}$";
        if (Pattern.matches(passwordRegex, newPassword)) {
            throw new UserException(ResultCodeEnum.INVALID_PARAM, "New password invalid");
        }

        // 检查新旧密码是否相同
        String oldPassword = user.getPassword();
        if (passwordEncoder.matches(newPassword, oldPassword)) {
            throw new UserException(ResultCodeEnum.FAILED, "New password and now are same");
        }

        // 加密并修改密码为新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public void checkUserNeedClear() {
        List<User> allUsers = userRepository.findAll();
        if (allUsers == null || allUsers.isEmpty()) {
            throw new UserException(ResultCodeEnum.NOT_FOUND, "No user in database");
        }

        // 获取目前在清空列表中的用户，作为判断依据
        List<UserNeedClear> allUserNeedClears = userNeedClearRepository.findAll();

        // 遍历每一个用户，确认是否需要清空
        for (User user : allUsers) {
            ArrayList<LoginRecord> loginRecords = user.getLoginRecords();
            if (loginRecords == null) {
                // 如果用户从未登录，则跳过
                continue;
            } else {
                // 如果用户有登录记录，进行判断
                Date lastLoginDate = loginRecords.get(loginRecords.size() - 1).getLoginDate();
                Date nowTime = new Date();

                // 如果用户没有进入清空广告的缓冲期，则跳过
                long durationDays =
                        Duration.between(lastLoginDate.toInstant(), nowTime.toInstant()).toDays();
                if (durationDays < clearUserDays - bufferDays) {
                    continue;
                }

                // 如果用户没有可以取消的广告，则跳过
                ArrayList<ObjectId> newCarAdIds = user.getNewCarAdIds();
                ArrayList<ObjectId> secondHandCarAdIds = user.getSecondHandCarAdIds();
                if (newCarAdIds == null && secondHandCarAdIds == null) {
                    continue;
                }

                // 如果用户已经在清空列表中，则跳过
                boolean skipUserFlag = false;
                for (UserNeedClear userNeedClear : allUserNeedClears) {
                    if (userNeedClear.getId().equals(user.getId())) {
                        skipUserFlag = true;
                        break;
                    }
                }
                if (skipUserFlag) {
                    continue;
                }

                // 通过所有判断
                // 计算到期时间
                Calendar cal = Calendar.getInstance();
                cal.setTime(lastLoginDate);
                cal.add(Calendar.DATE, 60);

                // 将用户添加至清空名单
                UserNeedClear userNeedClear = new UserNeedClear();
                userNeedClear.setId(user.getId());
                userNeedClear.setLastLoginDate(lastLoginDate);
                userNeedClear.setClearDate(cal.getTime());
                userNeedClearRepository.save(userNeedClear);
            }
        }

        log.info("Complete check user need clear");
    }

    @Override
    public void clearUserNeedClear() {
        // 获取目前在清空列表中的用户
        List<UserNeedClear> allUserNeedClears = userNeedClearRepository.findAll();

        // 如果列表内为空，则不需要清除，直接返回
        if (allUserNeedClears == null) {
            return;
        }

        // 遍历每一个需要清除的用户，进行判断
        for (UserNeedClear userNeedClear : allUserNeedClears) {
            Date clearDate = userNeedClear.getClearDate();
            Date nowTime = new Date();
            // 如果时间没有达到清除时间，不需要清除，直接返回
            if (nowTime.before(clearDate)) {
                return;
            }

            // 开始清除用户的所有广告
            User user = userRepository.findOneById(userNeedClear.getId());
            ArrayList<ObjectId> newCarAdIds = user.getNewCarAdIds();
            if (newCarAdIds != null) {
                for (ObjectId newCarAdId : newCarAdIds) {
                    NewCarAd newCarAd = newCarAdRepository.findOneById(newCarAdId);
                    newCarAdService.cancelTargetAd(newCarAd, user, CancelAdReasonEnum.NOLOGIN);;
                }
            }
            ArrayList<ObjectId> secondHandCarAdIds = user.getSecondHandCarAdIds();
            if (secondHandCarAdIds != null) {
                for (ObjectId secondHandCarAd : secondHandCarAdIds) {
                    SecondHandCarAd secondHandCar =
                            secondHandCarAdRepository.findOneById(secondHandCarAd);
                    secondHandCarAdService.cancelTargetAd(secondHandCar, user,
                            CancelAdReasonEnum.NOLOGIN);;
                }
            }
        }

        log.info("Complete clear user need clear");
    }

    @Override
    public User getOneById(ObjectId id) {
        if(!userRepository.existsAllById(id)){
            throw new UserException(ResultCodeEnum.NOT_FOUND, "This user not in the database");
        }
        User rs = userRepository.findOneById(id);
        rs.setPassword("");
        return userRepository.findOneById(id);
    }

    @Override
    public ArrayList<ObjectId> getFavoriteVehicles(String token) {
        // 判断用户是否存在
        String phone = JWTUtil.getValue(token);
        if (phone == null) {
            throw new UserException(ResultCodeEnum.INVALID_PARAM, "Token invalid");
        }
        if(!userRepository.existsByPhone(phone)){
            throw new UserException(ResultCodeEnum.NOT_FOUND, "This user not exist");
        }

        // 根据key来判断redis中是否存在数据
        String userFavoriteKey = "records:favorites:users:" + phone;
        ArrayList<ObjectId> userFavorites = new ArrayList<>();;
        if (redisUtil.hasKey(userFavoriteKey)) {
            // 如果redis中存在数据，直接从redis中获取
            Set<String> favoriteSet = redisUtil.sGet(userFavoriteKey);
            if (favoriteSet == null || favoriteSet.isEmpty()) {
                userFavorites = new ArrayList<>();
            }
            // 将String的载具id改为ObjectId并添加
            for (String favoriteVehicle : favoriteSet) {
                userFavorites.add(new ObjectId(favoriteVehicle));
            }
        } else {
            // redis中不存在数据，从mongodb获取
            User user = userRepository.findOneByPhone(phone);
            userFavorites = user.getFavoriteList();
            if (userFavorites == null || userFavorites.isEmpty()) {
                userFavorites = new ArrayList<>();
            }

            // 同时将数据缓存到redis
            for (ObjectId favoriteVehicle : userFavorites) {
                redisUtil.sSetAndTime(userFavoriteKey, expire, favoriteVehicle.toString());
            }
        }

        return userFavorites;
    }

    @Override
    public void setSecurityQuestionAndAnswer(String token,
                                             SecurityQuestionEnum securityQuestion,
                                             String securityAnswer) {
        // 校验安全答案是否合法
        if (securityAnswer.length() > securityMax || securityAnswer.length() < securityMin) {
            throw new UserException(ResultCodeEnum.INVALID_PARAM,
                    "Cannot input answer length more than 10 or less than 1");
        }

        // 获取用户
        String phone = JWTUtil.getValue(token);
        if (phone == null) {
            throw new UserException(ResultCodeEnum.INVALID_PARAM, "Token invalid");
        }
        User user = getUser(phone);

        // 设置安全问题和答案
        user.setSecurityQuestion(securityQuestion);
        user.setSecurityAnswer(securityAnswer);

        // 保存用户
        userRepository.save(user);
    }

    @Override
    public SecurityQuestionEnum getSecurityQuestion(String token) {
        // 获取用户
        String phone = JWTUtil.getValue(token);
        if (phone == null) {
            throw new UserException(ResultCodeEnum.INVALID_PARAM, "Token invalid");
        }
        User user = getUser(phone);

        // 检查用户是否已设置安全问题
        SecurityQuestionEnum securityQuestion = user.getSecurityQuestion();
        if (securityQuestion == null) {
            throw new UserException(ResultCodeEnum.NOT_FOUND,
                    "User do not set security question");
        }

        // 返回安全问题
        return securityQuestion;
    }

    @Override
    public void changePasswordBySecurity(String phone, SecurityQuestionEnum securityQuestion,
                                         String securityAnswer, String newPassword) {
        // 校验安全答案是否合法
        if (securityAnswer.length() > securityMax || securityAnswer.length() < securityMin) {
            throw new UserException(ResultCodeEnum.INVALID_PARAM,
                    "Cannot input answer length more than 10 or less than 1");
        }

        // 校验手机号是否合法
        String phoneRegex = "^1[0-9]{10}$";
        if (!Pattern.matches(phoneRegex, phone)) {
            throw new UserException(ResultCodeEnum.INVALID_PARAM, "Input phone invalid");
        }

        // 校验密码是否合法
        String passwordRegex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])[a-zA-Z0-9]{6,20}$";
        if (!Pattern.matches(passwordRegex, newPassword)) {
            throw new UserException(ResultCodeEnum.INVALID_PARAM, "Input new password invalid");
        }

        // 获取用户
        User user = getUser(phone);

        // 校验安全问题是否符合
        if (user.getSecurityQuestion() != securityQuestion) {
            throw new UserException(ResultCodeEnum.FAILED, "Input security question wrong");
        }

        // 校验安全回答是否符合
        if (!user.getSecurityAnswer().equals(securityAnswer)) {
            throw new UserException(ResultCodeEnum.FAILED, "Input security answer wrong");
        }

        // 校验密码是否相同
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new UserException(ResultCodeEnum.FAILED, "Input same password");
        }

        // 通过校验，设置密码
        user.setPassword(passwordEncoder.encode(newPassword));

        // 保存用户到数据库
        userRepository.save(user);
    }

    @Override
    public void setEmail(String token, String email) throws MessagingException {
        // 校验邮箱格式
        String emailRegex = "^([a-zA-Z0-9_.-])+@(([a-zA-Z0-9-])+\\.)+([a-zA-Z0-9]{2,4})+$";
        if (!Pattern.matches(emailRegex, email)) {
            throw new UserException(ResultCodeEnum.INVALID_PARAM, "Email format invalid");
        }

        // 获取用户
        String phone = JWTUtil.getValue(token);
        if (phone == null) {
            throw new UserException(ResultCodeEnum.INVALID_PARAM, "Token invalid");
        }
        User user = getUser(phone);

        // 已设置过邮箱，则抛出异常
        if (user.getEmail() != null || "".equals(user.getEmail())) {
            throw new UserException(ResultCodeEnum.FAILED, "Already set email");
        }

        // 调用邮箱服务，向用户邮箱发出激活链接
        emailService.sendVerifyEmail(token, email);
    }

    @Override
    public void setPasswordByOld(String token, String oldPassword, String newPassword) {
        // 校验新旧密码是否相同
        if (oldPassword.equals(newPassword)) {
            throw new UserException(ResultCodeEnum.INVALID_PARAM, "Input same password");
        }

        // 校验密码格式
        String passwordRegex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])[a-zA-Z0-9]{6,20}$";
        if (!Pattern.matches(passwordRegex, oldPassword) || !Pattern.matches(passwordRegex, newPassword)) {
            throw new UserException(ResultCodeEnum.INVALID_PARAM, "Password format invalid");
        }

        // 获取用户
        String phone = JWTUtil.getValue(token);
        if (phone == null) {
            throw new UserException(ResultCodeEnum.INVALID_PARAM, "Token invalid");
        }
        User user = getUser(phone);

        // 校验旧密码是否正确
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new UserException(ResultCodeEnum.FAILED, "Input old password wrong");
        }

        // 加密并设置新密码
        user.setPassword(passwordEncoder.encode(newPassword));

        // 保存用户到数据库
        userRepository.save(user);
    }

    @Override
    public List<ObjectId> getUserAllVehicleAds(String token) {
        // 校验并获取用户
        String phone = JWTUtil.getValue(token);
        if (phone == null) {
            throw new UserException(ResultCodeEnum.INVALID_PARAM, "Token invalid");
        }
        User user = getUser(phone);

        List<ObjectId> allVehicleAds = new ArrayList<>();
        List<ObjectId> secondHandCarAds = user.getSecondHandCarAdIds();
        if (secondHandCarAds != null) {
            allVehicleAds.addAll(secondHandCarAds);
        }

        List<ObjectId> newCarAds = user.getNewCarAdIds();
        if (newCarAds != null) {
            allVehicleAds.addAll(newCarAds);
        }

        List<ObjectId> newMotorAds = user.getNewMotorAdIds();
        if (newMotorAds != null) {
            allVehicleAds.addAll(newMotorAds);
        }

        List<ObjectId> secondHandMotorAds = user.getSecondHandMotorAdIds();
        if (secondHandMotorAds != null) {
            allVehicleAds.addAll(secondHandMotorAds);
        }

        return allVehicleAds;
    }

    @Override
    public List<ObjectId> getUserCompletedCarAds(String token) {
        // 校验并获取用户
        String phone = JWTUtil.getValue(token);
        if (phone == null) {
            throw new UserException(ResultCodeEnum.INVALID_PARAM, "Token invalid");
        }
        User user = getUser(phone);

        List<ObjectId> completedAds = user.getCompletedAdIds();
        if (completedAds == null) {
            completedAds = new ArrayList<>();
        }

        return completedAds;
    }

    @Override
    public long getUserFreeAdNums(String token) {
        // 校验并获取用户
        String phone = JWTUtil.getValue(token);
        if (phone == null) {
            throw new UserException(ResultCodeEnum.INVALID_PARAM, "Token invalid");
        }
        User user = getUser(phone);

        return user.getFreeAdNums();
    }

    //@Override
    //public void starCar(String token, ObjectId carId) {
    //    User rs = getUserByToken(token);
    //    if(!newCarAdRepository.existsById(carId) && !secondHandCarAdRepository.existsById(carId)){
    //        throw new UserException(ResultCodeEnum.NOT_FOUND, "this car not in the database");
    //    }
    //    if(rs.getStarList().size() >= 50){
    //        throw new UserException(ResultCodeEnum.FAILED, "star up to 50 cars");
    //    }
    //    if(rs.getStarList().contains(carId)){
    //        throw new UserException(ResultCodeEnum.FAILED, "already star this car");
    //    }
    //    rs.getStarList().add(carId);
    //    userRepository.save(rs);
    //}

    //@Override
    //public void cancelStarCar(String token, ObjectId carId) {
    //    User rs = getUserByToken(token);
    //    if(!newCarAdRepository.existsById(carId) && !secondHandCarAdRepository.existsById(carId)){
    //        throw new UserException(ResultCodeEnum.NOT_FOUND, "this car not in the database");
    //    }
    //    if(!rs.getStarList().contains(carId)){
    //        throw new UserException(ResultCodeEnum.FAILED, "not star this car yet");
    //    }
    //    rs.getStarList().remove(carId);
    //    userRepository.save(rs);
    //}

    /**
     * @description: 内部方法，根据输入的账号/手机号来获取对应的用户
     * @param {String} account 输入的用户账号/手机号
     * @return {User} 用户类的实例
     */
    private User getUser(String phone) {
        // 判断手机号是否合法
        String phoneRegex = "^1[0-9]{10}$";
        if (!Pattern.matches(phoneRegex, phone)) {
            throw new UserException(ResultCodeEnum.INVALID_PARAM, "Phone format wrong");
        }

        // 判断用户是否存在
        if (!userRepository.existsByPhone(phone)) {
            throw new UserException(ResultCodeEnum.NOT_FOUND, "User not exist");
        }

        User user = userRepository.findOneByPhone(phone);

        return user;
    }

    /**
     * 检查验证码，如果不合法、不存在、不正确则直接抛出异常
     *
     * @param verifyCode 验证码
     * @param phone      手机号
     */
    private void checkVerifyCode(String phone, Integer verifyCode) {
        // 校验六位数字验证码的合法性
        String verifyCodeRegex = "^[0-9]{6}$";
        if (Pattern.matches(verifyCodeRegex, verifyCodeRegex)) {
            throw new UserException(ResultCodeEnum.INVALID_PARAM, "Wrong verify code format");
        }

        // 从redis中提取用户验证信息，verify:code:phone， value为验证码
        String registerVerifyKey = "verify:code:" + phone;

        // 验证码不存在
        if (!redisUtil.hasKey(registerVerifyKey)) {
            throw new UserException(ResultCodeEnum.NOT_FOUND,
                    "Have not sent verify code or expire");
        }

        // 确认验证码是否正确
        Integer savedVerifyCode = Integer.valueOf(redisUtil.get(registerVerifyKey).toString());
        if (!savedVerifyCode.equals(verifyCode)) {
            throw new UserException(ResultCodeEnum.FAILED, "Verify code is wrong");
        }
    }
}
