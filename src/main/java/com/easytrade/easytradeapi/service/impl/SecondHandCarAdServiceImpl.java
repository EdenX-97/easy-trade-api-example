/*
 * @Description: 二手车广告服务层的实现类
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2022-01-07 19:53:38
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-19 03:53:32
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/service/impl/SecondHandCarAdServiceImpl.java
 * /EasyBuyCar/src/main/java/com/jiandanmaiche/api/service/impl/SecondHandCarAdServiceImpl.java
 * /EasyBuyCar/src/main/java/com/jiandanmaiche/api/service/impl/SecondHandCarAdServiceImpl.java
 * /EasyBuyCar/src/main/java/com/jiandanmaiche/api/service/impl/SecondHandCarAdServiceImpl.java
 */
package com.easytrade.easytradeapi.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import com.easytrade.easytradeapi.constant.consists.Result;
import com.easytrade.easytradeapi.constant.enums.*;
import com.easytrade.easytradeapi.constant.exceptions.JWTException;
import com.easytrade.easytradeapi.constant.exceptions.SecondHandCarException;
import com.easytrade.easytradeapi.constant.exceptions.SecondHandMotorException;
import com.easytrade.easytradeapi.model.Area;
import com.easytrade.easytradeapi.model.ExampleCar;
import com.easytrade.easytradeapi.model.SecondHandCarAd;
import com.easytrade.easytradeapi.model.User;
import com.easytrade.easytradeapi.repository.AreaRepository;
import com.easytrade.easytradeapi.repository.ExampleCarRepository;
import com.easytrade.easytradeapi.repository.SecondHandCarAdRepository;
import com.easytrade.easytradeapi.repository.UserRepository;
import com.easytrade.easytradeapi.service.intf.SecondHandCarAdService;
import com.easytrade.easytradeapi.service.intf.VehicleAdService;
import com.easytrade.easytradeapi.utils.JWTUtil;
import com.easytrade.easytradeapi.utils.RedisUtil;
import com.easytrade.easytradeapi.utils.ReturnResultUtil;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class SecondHandCarAdServiceImpl implements SecondHandCarAdService {
    @Autowired
    ExampleCarRepository exampleCarRepository;

    @Autowired
    SecondHandCarAdRepository secondHandCarAdRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AreaRepository areaRepository;

    @Autowired
    VehicleAdService vehicleAdService;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    MongoTemplate mongoTemplate;

    @Value("${vehiclead.create.max-num}")
    private Integer adCreateMaxNum;

    @Value("${vehiclead.realPrice.percent}")
    private long realPricePercent;

    @Override
    public List<SecondHandCarAd> getPostedSecondHandCarAds() {
        List<SecondHandCarAd> allPostedSecondHandCarAds =
                secondHandCarAdRepository.findAllByAdStatus(AdStatusEnum.POSTED);
        return allPostedSecondHandCarAds;
    }

    @Override
    public void updatePostedAdsInRedis() {
        List<SecondHandCarAd> allPostedSecondHandCarAds =
                secondHandCarAdRepository.findAllByAdStatus(AdStatusEnum.POSTED);
        Result result = ReturnResultUtil.success(allPostedSecondHandCarAds);
        String redisKey = "posted:secondHandCar:SimpleKey []";
        redisUtil.set(redisKey, result);

        log.info("Complete update posted second hand car ads in redis");
    }

    @Override
    public ObjectId createSecondHandCarAd(SecondHandCarAd secondHandCarAd, String token) {
        // 判断事故和运损不可能都为true的逻辑
        if (secondHandCarAd.getIfAccident() && secondHandCarAd.getIfShippingDamage()) {
            throw new SecondHandMotorException(ResultCodeEnum.FAILED,
                    "Vehicle cannot be accident and shipping damage at same time");
        }

        // 检查并获取用户
        User user = checkAndGetUser(token);

        // 如果用户未创建广告，则新建list
        ArrayList<ObjectId> secondHandCarAdIds = user.getSecondHandCarAdIds();
        if (secondHandCarAdIds == null) {
            secondHandCarAdIds = new ArrayList<>();
        }
        if (secondHandCarAdIds.size() + 1 > adCreateMaxNum) {
            throw new SecondHandCarException(ResultCodeEnum.FAILED,
                    "Create second hand car ads number over max");
        }

        // 检查地址信息
        Area area = areaRepository.findOneByProvinceAndCityAndCountyAndTown(secondHandCarAd.getProvince(),
                secondHandCarAd.getCity(), secondHandCarAd.getCounty(), secondHandCarAd.getTown());
        if (area == null) {
            throw new SecondHandCarException(ResultCodeEnum.NOT_FOUND, "Address do not exist");
        }

        // 检查价格是否在区间内
        long price = secondHandCarAd.getPrice();
        CarPriceLevelEnum pricelevel = secondHandCarAd.getPriceLevel();
        if (price < pricelevel.getMinPrice() || price > pricelevel.getMaxPrice()) {
            throw new SecondHandCarException(ResultCodeEnum.FAILED,
                    "Created second hand car price not within correct range");
        }

        // 根据输入的车型名称获取车辆模版信息
        ExampleCar exampleCar = exampleCarRepository.findOneById(secondHandCarAd.getExampleVehicleId());
        if (exampleCar == null) {
            throw new SecondHandCarException(ResultCodeEnum.NOT_FOUND,
                    "Created second hand car model do not exist");
        }

        // 创建新的二手车广告变量来储存信息，避免调用api时输入恶意数据
        SecondHandCarAd savedSecondHandCarAd = new SecondHandCarAd();

        // 保存车辆模版的id，便于获取车辆模版信息；设置状态为创建；设置创建时间；地址的代码信息；其他信息
        savedSecondHandCarAd.setExampleVehicleId(exampleCar.getId());
        savedSecondHandCarAd.setUserId(user.getId());
        savedSecondHandCarAd.setAdStatus(AdStatusEnum.CREATED);
        savedSecondHandCarAd.setCreatedDate(new Date());
        savedSecondHandCarAd.setProvince(secondHandCarAd.getProvince());
        savedSecondHandCarAd.setCity(secondHandCarAd.getCity());
        savedSecondHandCarAd.setCounty(secondHandCarAd.getCounty());
        savedSecondHandCarAd.setTown(secondHandCarAd.getTown());
        savedSecondHandCarAd.setPriceLevel(secondHandCarAd.getPriceLevel());
        savedSecondHandCarAd.setAdLevel(secondHandCarAd.getAdLevel());
        savedSecondHandCarAd.setPurchaseDate(secondHandCarAd.getPurchaseDate());
        //savedSecondHandCarAd.setProductionDate(secondHandCarAd.getProductionDate());
        savedSecondHandCarAd.setBigFrame(secondHandCarAd.getBigFrame());
        savedSecondHandCarAd.setLicense(secondHandCarAd.getLicense());
        savedSecondHandCarAd.setPrice(secondHandCarAd.getPrice());
        savedSecondHandCarAd.setOriginalPrice(secondHandCarAd.getPrice());
        savedSecondHandCarAd.setKilometers(secondHandCarAd.getKilometers());
        savedSecondHandCarAd.setDescription(secondHandCarAd.getDescription());
        savedSecondHandCarAd.setVehicleAdType(VehicleAdTypeEnum.SECONDHANDCARAD);
        savedSecondHandCarAd.setShowContactNum(secondHandCarAd.getShowContactNum());
        savedSecondHandCarAd.setContactNum(secondHandCarAd.getContactNum());
        savedSecondHandCarAd.setIfAccident(secondHandCarAd.getIfAccident());
        savedSecondHandCarAd.setIfShippingDamage(secondHandCarAd.getIfShippingDamage());

        // 保存二手车广告到数据库，并将二手车广告id保存至车商数据
        ObjectId saveCarAdId = secondHandCarAdRepository.save(savedSecondHandCarAd).getId();
        secondHandCarAdIds.add(saveCarAdId);
        user.setSecondHandCarAdIds(secondHandCarAdIds);
        userRepository.save(user);

        // 返回创建的二手车id
        return saveCarAdId;
    }

    @Override
    public void postSecondHandCarAd(ObjectId secondHandCarId, String token) {
        // 检查并获取用户
        User user = checkAndGetUser(token);

        // 检查并获取广告
        SecondHandCarAd secondHandCarAd = checkAndGetAd(user, secondHandCarId);

        // 检查二手车广告状态，只有处于支付完成状态才能发布
        if (secondHandCarAd.getAdStatus().equals(AdStatusEnum.PAID)) {
            throw new SecondHandCarException(ResultCodeEnum.FAILED, "Have not paid");
        }

        // 检查是否已经上传完图片，必须有图片才可以发布，视频不是必须
        Boolean ifUploadedImages = vehicleAdService.checkIfUpload(secondHandCarAd.getId(),
                FileTypeEnum.IMAGE);
        if (!ifUploadedImages) {
            throw new SecondHandCarException(ResultCodeEnum.FAILED, "Have not paid");
        }

        // 检查完毕，设置广告状态为发布，并保存至数据库
        secondHandCarAd.setAdStatus(AdStatusEnum.POSTED);
        secondHandCarAdRepository.save(secondHandCarAd);
    }

    @Override
    public void cancelSecondHandCarAd(ObjectId secondHandCarId, String token) {
        // 检查并获取用户
        User user = checkAndGetUser(token);

        // 检查并获取广告
        SecondHandCarAd secondHandCarAd = checkAndGetAd(user, secondHandCarId);

        // 检查二手车广告状态，只有不处于canceled状态才能取消
        if (secondHandCarAd.getAdStatus().equals(AdStatusEnum.CANCELED)) {
            throw new SecondHandCarException(ResultCodeEnum.FAILED, "Cannot cannel canceled ad");
        }

        // 校验完毕，删除目标广告
        cancelTargetAd(secondHandCarAd, user, CancelAdReasonEnum.ACTIVELY);
    }

    @Override
    public void cancelAdsOverOneYear() {
        Date nowDate = new Date();

        // 遍历每一个二手车广告，判断是否存在超过一年
        List<SecondHandCarAd> allSecondHandCarAds = secondHandCarAdRepository.findAll();
        for (SecondHandCarAd secondHandCarAd : allSecondHandCarAds) {
            Date createDate = secondHandCarAd.getCreatedDate();
            long durationDays =
                    Duration.between(createDate.toInstant(), nowDate.toInstant()).toDays();
            if (durationDays > 365) {
                // 如果存在超过一年，自动将该广告取消
                User user =
                        userRepository.findOneById(secondHandCarAd.getUserId());
                if (user == null) {
                    throw new SecondHandCarException(ResultCodeEnum.NOT_FOUND, "User not exist");
                }
                cancelTargetAd(secondHandCarAd, user, CancelAdReasonEnum.OVERONEYEAR);
            }
        }

        log.info("Complete cancel second hand car ads over one year");
    }

    @Override
    public void cancelTargetAd(SecondHandCarAd secondHandCarAd, User user,
            CancelAdReasonEnum cancelAdReason) {
        // 设置二手车广告为取消，设置原因为主动取消
        secondHandCarAd.setAdStatus(AdStatusEnum.CANCELED);
        secondHandCarAd.setCancelAdReason(cancelAdReason);
        secondHandCarAdRepository.save(secondHandCarAd);

        // 从用户列表中删除该广告id
        ArrayList<ObjectId> userOwnAds = user.getSecondHandCarAdIds();
        userOwnAds.remove(secondHandCarAd.getId());
        user.setSecondHandCarAdIds(userOwnAds);
        userRepository.save(user);

        //// 删除图片数据
        //List<String> images = secondHandCarAd.getImages();
        //if (images != null && !images.isEmpty()) {
        //    for (String image : images) {
        //        Boolean removeSucessFlag = FileUtil.removeFile(image);
        //        if (!removeSucessFlag) {
        //            throw new SecondHandCarException(ResultCodeEnum.FAILED, "Cannot remove iamge");
        //        }
        //    }
        //}
        //
        //// 删除视频数据
        //List<String> videos = secondHandCarAd.getVideos();
        //if (videos != null && !videos.isEmpty()) {
        //    for (String video : videos) {
        //        Boolean removeSucessFlag = FileUtil.removeFile(video);
        //        if (!removeSucessFlag) {
        //            throw new SecondHandCarException(ResultCodeEnum.FAILED, "Cannot remove video");
        //        }
        //    }
        //}
    }

    @Override
    public void completeSecondHandCarAd(ObjectId secondHandCarId, long realPrice, String token) {
        // 检查并获取用户
        User user = checkAndGetUser(token);

        // 检查并获取广告
        SecondHandCarAd secondHandCarAd = checkAndGetAd(user, secondHandCarId);

        // 检查二手车广告状态，只有不处于canceled状态才能完成
        if (!secondHandCarAd.getAdStatus().equals(AdStatusEnum.POSTED)) {
            throw new SecondHandCarException(ResultCodeEnum.FAILED, "Can only complete posted ad");
        }

        // 检查真实价格
        long price = secondHandCarAd.getPrice();
        long maxRealPrice = price + price * realPricePercent / 100;
        long minRealPrice = price - price * realPricePercent / 100;
        if (realPrice < minRealPrice || realPrice > maxRealPrice) {
            throw new SecondHandCarException(ResultCodeEnum.FAILED, "Real price inappropriate");
        }

        // 设置新车广告为完成，设置完成价格，保存至数据库
        secondHandCarAd.setAdStatus(AdStatusEnum.COMPLETED);
        secondHandCarAd.setRealPrice(realPrice);
        secondHandCarAdRepository.save(secondHandCarAd);

        // 从用户列表中删除该广告id，添加到完成列表
        ArrayList<ObjectId> userCompletedAds = user.getCompletedAdIds();
        if (userCompletedAds == null) {
            userCompletedAds = new ArrayList<ObjectId>();
        }
        ArrayList<ObjectId> userOwnAds = user.getSecondHandCarAdIds();
        userOwnAds.remove(secondHandCarId);
        userCompletedAds.add(secondHandCarId);
        user.setNewCarAdIds(userOwnAds);
        user.setCompletedAdIds(userCompletedAds);
        userRepository.save(user);
    }

    /**
     * @description: 内部方法，检查并获取用户
     * @param {String} token 用户的token
     * @return {User} 实例化的用户
     */
    private User checkAndGetUser(String token) {
        // 校验token的合法性
        if (!token.startsWith("Bearer ")) {
            throw new JWTException(ResultCodeEnum.INVALID_PARAM, "Token is incorrect");
        }

        // 检查输入的用户是否存在
        String phone = JWTUtil.getValue(token);
        if (!userRepository.existsByPhone(phone)) {
            throw new SecondHandCarException(ResultCodeEnum.NOT_FOUND, "User not exist");
        }

        User user = userRepository.findOneByPhone(phone);

        return user;
    }

    /**
     * @description: 内部方法，检查并获取二手车广告
     * @param {User} user 实例化的用户
     * @param {String} secondHandCarId 二手车广告id
     * @return {SecondHandCarAd} 实例化的二手车广告
     */
    private SecondHandCarAd checkAndGetAd(User user, ObjectId secondHandCarId) {
        // 检查输入的广告是否存在
        if (!secondHandCarAdRepository.existsById(secondHandCarId)) {
            throw new SecondHandCarException(ResultCodeEnum.NOT_FOUND, "Second hand car ad not exist");
        }

        // 检查该用户是否拥有该广告
        ArrayList<ObjectId> userOwnAds = user.getSecondHandCarAdIds();
        if (!userOwnAds.contains(secondHandCarId)) {
            throw new SecondHandCarException(ResultCodeEnum.FAILED,
                    "User do not own this second car car ad");
        }

        SecondHandCarAd secondHandCarAd = secondHandCarAdRepository.findOneById(secondHandCarId);

        return secondHandCarAd;
    }

    /**
     * @description 根据账号获取其下的所有二手车广告id
     * @param {String} account 用户账号
     * @return {List<String>}
     */
    public List<SecondHandCarAd> getAllSecondHandCarAdByUserID(ObjectId userId){
        // 检查userId的合法性
        if(userId.toString().length() != 24){
            throw new SecondHandCarException(ResultCodeEnum.FAILED, "The user ID is invalid");
        }
        if(!userRepository.existsById(userId)){
            throw new SecondHandCarException(ResultCodeEnum.NOT_FOUND, "This user ID is not in the database");
        }
        if(Objects.equals(userRepository.findOneById(userId).getRole(), "ROLE_DISABLE")){
            throw new SecondHandCarException(ResultCodeEnum.FAILED, "This user account is not activated");
        }
        if(Objects.equals(userRepository.findOneById(userId).getRole(), "ROLE_DEALER")){
            throw new SecondHandCarException(ResultCodeEnum.FAILED, "This user is not ordinary individual users");
        }
        if(Objects.equals(userRepository.findOneById(userId).getRole(), "ROLE_BLOCKED")){
            throw new SecondHandCarException(ResultCodeEnum.FAILED, "This user has been banned");
        }
        return secondHandCarAdRepository.findAllByUserId(userId);
    }

    @Override
    public List<SecondHandCarAd> getAllSecondHandCarByExampleCarId(ObjectId exampleCarId) {
        // 检查数据库中是否有输入的样本车辆id
        if(!secondHandCarAdRepository.existsAllByExampleVehicleId(exampleCarId)){
            throw new SecondHandCarException(ResultCodeEnum.NOT_FOUND, "this example car id is not in the database");
        }
        return secondHandCarAdRepository.findAllByExampleVehicleId(exampleCarId);
    }

    @Override
    public List<SecondHandCarAd> getAllSecondHandCarByPurchaseDateBetween(String date1, String date2) {
        // 设置Date类型格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date input1;
        Date input2;
        try {
            // 将字符串按照指定格式转变为Date类型
            input1 = sdf.parse(date1);
            input2 = sdf.parse(date2);
            return secondHandCarAdRepository.findAllByPurchaseDateBetween(input1, input2);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    @Override
    public List<SecondHandCarAd> getAllSecondHandCarByProductionDateBetween(String date1, String date2) {
        // 设置Date类型格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date input1;
        Date input2;
        try {
            // 将字符串按照指定格式转变为Date类型
            input1 = sdf.parse(date1);
            input2 = sdf.parse(date2);
            return secondHandCarAdRepository.findAllByProductionDateBetween(input1, input2);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    @Override
    public SecondHandCarAd getOneSecondHandCarByBigFrame(String bigFrame) {
        // 检查数据库中是否存在大架号
        if(!secondHandCarAdRepository.existsByBigFrame(bigFrame)){
            throw new SecondHandCarException(ResultCodeEnum.NOT_FOUND, "this big frame is not in the database");
        }
        return secondHandCarAdRepository.findOneByBigFrame(bigFrame);
    }

    @Override
    public SecondHandCarAd getOneSecondHandCarByLicense(String license) {
        // 检查数据库中是否有输入的车牌号
        if(!secondHandCarAdRepository.existsByLicense(license)){
            throw new SecondHandCarException(ResultCodeEnum.NOT_FOUND, "this license is not in the database");
        }
        return secondHandCarAdRepository.findOneByLicense(license);
    }

    @Override
    public List<SecondHandCarAd> getAllSecondHandCarByKilometersBetween(long kilo1, long kilo2) {
        // 检查kilo1和kilo2的合法性
        if(kilo1 < 0 || kilo2 < 0){
            throw new SecondHandCarException(ResultCodeEnum.INVALID_PARAM, "kilo can not be negtive");
        }
        if(kilo1 > kilo2){
            throw new SecondHandCarException(ResultCodeEnum.INVALID_PARAM, "kilo1 has to be greater than kilo2");
        }
        return secondHandCarAdRepository.findAllByKilometersBetween(kilo1, kilo2);
    }

    //@Override
    //public void addSecondHandCarAd(String model, GearboxTypeEnum gte, ObjectId exampleCarId, String purchaseDate, String productionDate, String bigFrame, String license, long kilo, ObjectId userId) {
    //    // 检测模型名称，变速箱模式，样本车辆id，大架号，牌照号，公里数和用户id输入的合法性
    //    if(gte != GearboxTypeEnum.MANUAL && gte != GearboxTypeEnum.AUTOMATIC){
    //        throw new SecondHandCarException(ResultCodeEnum.INVALID_PARAM, "you can only input MANUAL or AUTOMATIC");
    //    }
    //    if(!exampleCarRepository.existsExampleCarById(exampleCarId)){
    //        throw new SecondHandCarException(ResultCodeEnum.NOT_FOUND, "this example car id is not in the database");
    //    }
    //    if(secondHandCarAdRepository.existsByBigFrame(bigFrame)){
    //        throw new SecondHandCarException(ResultCodeEnum.NOT_FOUND, "this big frame is already in the database");
    //    }
    //    if(secondHandCarAdRepository.existsByLicense(license)){
    //        throw new SecondHandCarException(ResultCodeEnum.NOT_FOUND, "this license is already in the database");
    //    }
    //    if(kilo < 0){
    //        throw new SecondHandCarException(ResultCodeEnum.INVALID_PARAM, "kilo can not be negtive");
    //    }
    //    if(userId.toString().length() != 24){
    //        throw new SecondHandCarException(ResultCodeEnum.FAILED, "The user ID is invalid");
    //    }
    //    if(!userRepository.existsById(userId)){
    //        throw new SecondHandCarException(ResultCodeEnum.NOT_FOUND, "This user ID is not in the database");
    //    }
    //    if(Objects.equals(userRepository.findOneById(userId).getRole(), "ROLE_DISABLE")){
    //        throw new SecondHandCarException(ResultCodeEnum.FAILED, "This user account is not activated");
    //    }
    //    if(Objects.equals(userRepository.findOneById(userId).getRole(), "ROLE_DEALER")){
    //        throw new SecondHandCarException(ResultCodeEnum.FAILED, "This user is not ordinary individual users");
    //    }
    //    if(Objects.equals(userRepository.findOneById(userId).getRole(), "ROLE_BLOCKED")){
    //        throw new SecondHandCarException(ResultCodeEnum.FAILED, "This user has been banned");
    //    }
    //    SecondHandCarAd secondHandCarAd = new SecondHandCarAd();
    //    secondHandCarAd.setModel(model);
    //    secondHandCarAd.setGearbox(gte);
    //    secondHandCarAd.setExampleCarId(exampleCarId);
    //    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    //    try {
    //        Date newPurchaseDate = sdf.parse(purchaseDate);
    //        Date newProductionDate = sdf.parse(productionDate);
    //        secondHandCarAd.setPurchaseDate(newPurchaseDate);
    //        secondHandCarAd.setProductionDate(newProductionDate);
    //        secondHandCarAd.setBigFrame(bigFrame);
    //        secondHandCarAd.setLicense(license);
    //        secondHandCarAd.setKilometers(kilo);
    //        secondHandCarAd.setUserId(userId);
    //        secondHandCarAdRepository.save(secondHandCarAd);
    //    } catch (ParseException e) {
    //        e.printStackTrace();
    //    }
    //}

    @Override
    public void deleteSecondHandCarAdById(ObjectId id) {
        // 检测二手车广告id输入的合法性
        if(!secondHandCarAdRepository.existsById(id)){
            throw new SecondHandCarException(ResultCodeEnum.FAILED, "The second hand car id is not in the database");
        }
        secondHandCarAdRepository.deleteById(id);
    }

    @Override
    public void updateSecondHandCarAdById(ObjectId id, String model, GearboxTypeEnum gte, ObjectId exampleCarId, String purchaseDate, String productionDate, String bigFrame, String license, long kilo, ObjectId userId) {
        // 检测二手车广告id，模型名称，变速箱模式，样本车辆id，公里数和用户id输入的合法性
        if(!secondHandCarAdRepository.existsById(id)){
            throw new SecondHandCarException(ResultCodeEnum.FAILED, "The second hand car id is not in the database");
        }
        if(gte != GearboxTypeEnum.MANUAL && gte != GearboxTypeEnum.AUTOMATIC){
            throw new SecondHandCarException(ResultCodeEnum.INVALID_PARAM, "you can only input MANUAL or AUTOMATIC");
        }
        if(!exampleCarRepository.existsExampleCarById(exampleCarId)){
            throw new SecondHandCarException(ResultCodeEnum.NOT_FOUND, "this example car id is not in the database");
        }
        if(kilo < 0){
            throw new SecondHandCarException(ResultCodeEnum.INVALID_PARAM, "kilo can not be negtive");
        }
        if(userId.toString().length() != 24){
            throw new SecondHandCarException(ResultCodeEnum.FAILED, "The user ID is invalid");
        }
        if(!userRepository.existsById(userId)){
            throw new SecondHandCarException(ResultCodeEnum.NOT_FOUND, "This user ID is not in the database");
        }
        if(Objects.equals(userRepository.findOneById(userId).getRole(), "ROLE_DISABLE")){
            throw new SecondHandCarException(ResultCodeEnum.FAILED, "This user account is not activated");
        }
        if(Objects.equals(userRepository.findOneById(userId).getRole(), "ROLE_DEALER")){
            throw new SecondHandCarException(ResultCodeEnum.FAILED, "This user is not ordinary individual users");
        }
        if(Objects.equals(userRepository.findOneById(userId).getRole(), "ROLE_BLOCKED")){
            throw new SecondHandCarException(ResultCodeEnum.FAILED, "This user has been banned");
        }
        SecondHandCarAd secondHandCarAd = secondHandCarAdRepository.findOneById(id);
        //secondHandCarAd.setModel(model);
        //secondHandCarAd.setGearbox(gte);
        secondHandCarAd.setExampleVehicleId(exampleCarId);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date newPurchaseDate = sdf.parse(purchaseDate);
            Date newProductionDate = sdf.parse(productionDate);
            secondHandCarAd.setPurchaseDate(newPurchaseDate);
            secondHandCarAd.setProductionDate(newProductionDate);
            secondHandCarAd.setBigFrame(bigFrame);
            secondHandCarAd.setLicense(license);
            secondHandCarAd.setKilometers(kilo);
            secondHandCarAd.setUserId(userId);
            secondHandCarAdRepository.save(secondHandCarAd);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public SecondHandCarAd getOneById(ObjectId id) {
        if(!secondHandCarAdRepository.existsById(id)){
            throw new SecondHandCarException(ResultCodeEnum.NOT_FOUND, "this second car not in the database");
        }
        SecondHandCarAd rs = secondHandCarAdRepository.findOneById(id);
        if(rs.getAdStatus() != AdStatusEnum.POSTED){
            throw new SecondHandCarException(ResultCodeEnum.NOT_FOUND, "this second car not posted yet");
        }
        return rs;
    }
}

