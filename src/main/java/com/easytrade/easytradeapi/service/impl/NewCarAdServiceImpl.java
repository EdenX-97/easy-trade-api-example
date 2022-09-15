/*
 * @Description: 新车广告服务层实现类
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2022-01-02 00:44:24
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-19 03:49:43
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/service/impl/NewCarAdServiceImpl.java
 */
package com.easytrade.easytradeapi.service.impl;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import com.easytrade.easytradeapi.constant.consists.Result;
import com.easytrade.easytradeapi.constant.enums.*;
import com.easytrade.easytradeapi.constant.exceptions.NewCarException;
import com.easytrade.easytradeapi.constant.exceptions.SecondHandCarException;
import com.easytrade.easytradeapi.constant.exceptions.SecondHandMotorException;
import com.easytrade.easytradeapi.model.Area;
import com.easytrade.easytradeapi.model.ExampleCar;
import com.easytrade.easytradeapi.model.NewCarAd;
import com.easytrade.easytradeapi.model.User;
import com.easytrade.easytradeapi.repository.AreaRepository;
import com.easytrade.easytradeapi.repository.ExampleCarRepository;
import com.easytrade.easytradeapi.repository.NewCarAdRepository;
import com.easytrade.easytradeapi.repository.UserRepository;
import com.easytrade.easytradeapi.service.intf.NewCarAdService;
import com.easytrade.easytradeapi.service.intf.VehicleAdService;
import com.easytrade.easytradeapi.utils.JWTUtil;
import com.easytrade.easytradeapi.utils.RedisUtil;
import com.easytrade.easytradeapi.utils.ReturnResultUtil;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class NewCarAdServiceImpl implements NewCarAdService {
    @Autowired
    ExampleCarRepository exampleCarRepository;

    @Autowired
    NewCarAdRepository newCarAdRepository;

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
    public List<NewCarAd> getPostedNewCarAds() {
        return newCarAdRepository.findAllByAdStatus(AdStatusEnum.POSTED);
    }

    @Override
    public void updatePostedAdsInRedis() {
        List<NewCarAd> allPostedNewCarAds =
                newCarAdRepository.findAllByAdStatus(AdStatusEnum.POSTED);
        Result result = ReturnResultUtil.success(allPostedNewCarAds);
        String redisKey = "posted:newCar:SimpleKey []";
        redisUtil.set(redisKey, result);

        log.info("Complete update posted new car ads in redis");
    }

    @Override
    public ObjectId createNewCarAd(NewCarAd newCarAd, String token) {
        // 判断事故和运损不可能都为true的逻辑
        if (newCarAd.getIfAccident() && newCarAd.getIfShippingDamage()) {
            throw new SecondHandMotorException(ResultCodeEnum.FAILED,
                    "Vehicle cannot be accident and shipping damage at same time");
        }

        // 检查并获取用户
        User dealer = checkAndGetUser(token);

        // 如果车商未创建广告，则新建list
        ArrayList<ObjectId> newCarAdIds = dealer.getNewCarAdIds();
        if (newCarAdIds == null) {
            newCarAdIds = new ArrayList<>();
        }
        if (newCarAdIds.size() + 1 > adCreateMaxNum) {
            throw new NewCarException(ResultCodeEnum.FAILED, "Create new car ads number over max");
        }

        // 检查地址信息
        Area area = areaRepository.findOneByProvinceAndCityAndCountyAndTown(newCarAd.getProvince(),
                newCarAd.getCity(), newCarAd.getCounty(), newCarAd.getTown());
        if (area == null) {
            throw new NewCarException(ResultCodeEnum.NOT_FOUND, "Address do not exist");
        }

        // 检查价格是否在区间内
        long price = newCarAd.getPrice();
        CarPriceLevelEnum pricelevel = newCarAd.getPriceLevel();
        if (price < pricelevel.getMinPrice() || price > pricelevel.getMaxPrice()) {
            throw new NewCarException(ResultCodeEnum.FAILED,
                    "Created new car price not within correct range");
        }

        // 根据输入的车型名称获取车辆模版信息
        ExampleCar exampleCar = exampleCarRepository.findOneById(newCarAd.getExampleVehicleId());
        if (exampleCar == null) {
            throw new NewCarException(ResultCodeEnum.NOT_FOUND,
                    "Created new car model do not exist");
        }

        // 创建新的新车广告变量来储存信息，避免调用api时输入恶意数据
        NewCarAd savedNewCarAd = new NewCarAd();

        // 保存车辆模版的id，便于获取车辆模版信息；设置状态为创建；设置创建时间；地址的代码信息；其他信息
        savedNewCarAd.setExampleVehicleId(exampleCar.getId());
        savedNewCarAd.setUserId(dealer.getId());
        savedNewCarAd.setAdStatus(AdStatusEnum.CREATED);
        savedNewCarAd.setCreatedDate(new Date());
        savedNewCarAd.setBigFrame(newCarAd.getBigFrame());
        savedNewCarAd.setProvince(newCarAd.getProvince());
        savedNewCarAd.setCity(newCarAd.getCity());
        savedNewCarAd.setCounty(newCarAd.getCounty());
        savedNewCarAd.setTown(newCarAd.getTown());
        savedNewCarAd.setPriceLevel(newCarAd.getPriceLevel());
        savedNewCarAd.setAdLevel(newCarAd.getAdLevel());
        savedNewCarAd.setPrice(newCarAd.getPrice());
        savedNewCarAd.setOriginalPrice(newCarAd.getPrice());
        savedNewCarAd.setKilometers(newCarAd.getKilometers());
        savedNewCarAd.setDescription(newCarAd.getDescription());
        savedNewCarAd.setVehicleAdType(VehicleAdTypeEnum.NEWCARAD);
        savedNewCarAd.setShowContactNum(newCarAd.getShowContactNum());
        savedNewCarAd.setContactNum(newCarAd.getContactNum());
        savedNewCarAd.setIfAccident(newCarAd.getIfAccident());
        savedNewCarAd.setIfShippingDamage(newCarAd.getIfShippingDamage());

        // 保存新车广告到数据库，并将新车广告id保存至车商数据
        ObjectId saveCarAdId = newCarAdRepository.save(savedNewCarAd).getId();
        newCarAdIds.add(saveCarAdId);
        dealer.setNewCarAdIds(newCarAdIds);
        userRepository.save(dealer);

        // 返回创建的新车id
        return saveCarAdId;
    }

    @Override
    public void postNewCarAd(ObjectId newCarId, String token) {
        // 检查并获取用户
        User dealer = checkAndGetUser(token);

        // 检查并获取广告
        NewCarAd newCarAd = checkAndGetAd(dealer, newCarId);

        // 检查新车广告状态，只有处于支付完成状态才能发布
        if (newCarAd.getAdStatus().equals(AdStatusEnum.PAID)) {
            throw new NewCarException(ResultCodeEnum.FAILED, "Have not paid");
        }

        // 检查是否已经上传完图片，必须有图片才可以发布，视频不是必须
        Boolean ifUploadedImages = vehicleAdService.checkIfUpload(newCarAd.getId(), FileTypeEnum.IMAGE);
        if (!ifUploadedImages) {
            throw new SecondHandCarException(ResultCodeEnum.FAILED, "Have not paid");
        }

        // 检查完毕，设置广告状态为发布，并保存至数据库
        newCarAd.setAdStatus(AdStatusEnum.POSTED);
        newCarAdRepository.save(newCarAd);
    }

    @Override
    public void cancelNewCarAd(ObjectId newCarId, String token) {
        // 检查并获取用户
        User dealer = checkAndGetUser(token);

        // 检查并获取广告
        NewCarAd newCarAd = checkAndGetAd(dealer, newCarId);

        // 检查新车广告状态，只有不处于canceled状态才能取消
        if (newCarAd.getAdStatus().equals(AdStatusEnum.CANCELED)) {
            throw new NewCarException(ResultCodeEnum.FAILED, "Cannot cannel canceled ad");
        }

        // 校验完毕，删除目标广告
        cancelTargetAd(newCarAd, dealer, CancelAdReasonEnum.ACTIVELY);
    }

    @Override
    public void cancelAdsOverOneYeaer() {
        Date nowDate = new Date();

        // 遍历每一个二手车广告，判断是否存在超过一年
        List<NewCarAd> allNewCarAds = newCarAdRepository.findAll();
        for (NewCarAd newCarAd : allNewCarAds) {
            Date createDate = newCarAd.getCreatedDate();
            long durationDays =
                    Duration.between(createDate.toInstant(), nowDate.toInstant()).toDays();
            if (durationDays > 365) {
                // 如果存在超过一年，自动将该广告取消
                User user = userRepository.findOneById(newCarAd.getUserId());
                if (user == null) {
                    throw new NewCarException(ResultCodeEnum.NOT_FOUND, "User not exist");
                }
                cancelTargetAd(newCarAd, user, CancelAdReasonEnum.OVERONEYEAR);
            }
        }

        log.info("Complete cancel ads over one year");
    }

    @Override
    public void cancelTargetAd(NewCarAd newCarAd, User dealer, CancelAdReasonEnum cancelAdReason) {
        // 设置新车广告为取消，设置原因为主动取消，并保存至数据库
        newCarAd.setAdStatus(AdStatusEnum.CANCELED);
        newCarAd.setCancelAdReason(cancelAdReason);
        newCarAdRepository.save(newCarAd);

        // 从用户列表中删除该广告id
        ArrayList<ObjectId> dealerOwnAds = dealer.getNewCarAdIds();
        dealerOwnAds.remove(newCarAd.getId());
        dealer.setNewCarAdIds(dealerOwnAds);
        userRepository.save(dealer);

        //// 删除图片数据
        //List<String> images = newCarAd.getImages();
        //if (images != null && !images.isEmpty()) {
        //    for (String image : images) {
        //        Boolean removeSucessFlag = FileUtil.removeFile(image);
        //        if (!removeSucessFlag) {
        //            throw new NewCarException(ResultCodeEnum.FAILED, "Cannot remove iamge");
        //        }
        //    }
        //}
        //
        //// 删除视频数据
        //List<String> videos = newCarAd.getVideos();
        //if (videos != null && !videos.isEmpty()) {
        //    for (String video : videos) {
        //        Boolean removeSucessFlag = FileUtil.removeFile(video);
        //        if (!removeSucessFlag) {
        //            throw new NewCarException(ResultCodeEnum.FAILED, "Cannot remove video");
        //        }
        //    }
        //}
    }

    @Override
    public void completeNewCarAd(ObjectId newCarId, long realPrice, String token) {
        // 检查并获取用户
        User dealer = checkAndGetUser(token);

        // 检查并获取广告
        NewCarAd newCarAd = checkAndGetAd(dealer, newCarId);

        // 检查新车广告状态，只有处于posted状态才能完成
        if (!newCarAd.getAdStatus().equals(AdStatusEnum.POSTED)) {
            throw new NewCarException(ResultCodeEnum.FAILED, "Can only complete posted ad");
        }

        // 检查真实价格
        long price = newCarAd.getPrice();
        long maxRealPrice = price + price * realPricePercent / 100;
        long minRealPrice = price - price * realPricePercent / 100;
        if (realPrice < minRealPrice || realPrice > maxRealPrice) {
            throw new NewCarException(ResultCodeEnum.FAILED, "Real price inappropriate");
        }

        // 设置新车广告为完成，设置完成价格，保存至数据库
        newCarAd.setAdStatus(AdStatusEnum.COMPLETED);
        newCarAd.setRealPrice(realPrice);
        newCarAdRepository.save(newCarAd);

        // 从用户列表中删除该广告id，添加到完成列表
        ArrayList<ObjectId> dealerCompletedAds = dealer.getCompletedAdIds();
        if (dealerCompletedAds == null) {
            dealerCompletedAds = new ArrayList<ObjectId>();
        }
        ArrayList<ObjectId> dealerOwnAds = dealer.getNewCarAdIds();
        dealerOwnAds.remove(newCarId);
        dealerCompletedAds.add(newCarId);
        dealer.setNewCarAdIds(dealerOwnAds);
        dealer.setCompletedAdIds(dealerCompletedAds);
        userRepository.save(dealer);
    }

    @Override
    public List<NewCarAd> getAllNewCarAdByUserID(ObjectId userId) {
        // 检查userId的合法性
        if(userId.toString().length() != 24){
            throw new NewCarException(ResultCodeEnum.FAILED, "The user ID is invalid");
        }
        if(!userRepository.existsById(userId)){
            throw new NewCarException(ResultCodeEnum.NOT_FOUND, "This user ID is not in the database");
        }
        if(Objects.equals(userRepository.findOneById(userId).getRole(), "ROLE_DISABLE")){
            throw new NewCarException(ResultCodeEnum.FAILED, "This user account is not activated");
        }
        if(Objects.equals(userRepository.findOneById(userId).getRole(), "ROLE_BLOCKED")){
            throw new NewCarException(ResultCodeEnum.FAILED, "This user has been banned");
        }
        if(!Objects.equals(userRepository.findOneById(userId).getRole(), "ROLE_DEALER")){
            throw new NewCarException(ResultCodeEnum.FAILED, "This user is not a dealer account");
        }
        return newCarAdRepository.findAllByUserId(userId);
    }

    @Override
    public List<NewCarAd> getAllNewCarAdByGearboxTypeEnum(GearboxTypeEnum gte) {
        // 检测变速箱模式输入是否合法
        if(!gte.equals(GearboxTypeEnum.AUTOMATIC) && !gte.equals(GearboxTypeEnum.MANUAL)){
            throw new NewCarException(ResultCodeEnum.NOT_FOUND, "The input gearbox mode does not exist");
        }
        return newCarAdRepository.findAllByGearbox(gte);
    }

    @Override
    public List<NewCarAd> getAllNewCarAdByExampleCarId(ObjectId exampleCarId) {
        // 检测样本车辆id输入是否合法
        if (!exampleCarRepository.existsExampleCarById(exampleCarId)){
            throw new NewCarException(ResultCodeEnum.NOT_FOUND, "this example car id not in the database");
        }
        return newCarAdRepository.findAllByExampleVehicleId(exampleCarId);
    }

    //@Override
    //public void addNewCarAd(String model, GearboxTypeEnum gte, ObjectId exampleCarId, ObjectId userId, String imgURL) {
    //    // 检测变速箱模式，样本车辆id以及用户id输入是否合法
    //    if(!gte.equals(GearboxTypeEnum.AUTOMATIC) && !gte.equals(GearboxTypeEnum.MANUAL)){
    //        throw new NewCarException(ResultCodeEnum.NOT_FOUND, "The input gearbox mode does not exist");
    //    }
    //    if (!exampleCarRepository.existsExampleCarById(exampleCarId)){
    //        throw new NewCarException(ResultCodeEnum.NOT_FOUND, "this example car id not in the database");
    //    }
    //    if(userId.toString().length() != 24){
    //        throw new NewCarException(ResultCodeEnum.FAILED, "The user ID is invalid");
    //    }
    //    if(!userRepository.existsById(userId)){
    //        throw new NewCarException(ResultCodeEnum.NOT_FOUND, "This user ID is not in the database");
    //    }
    //    if(Objects.equals(userRepository.findOneById(userId).getRole(), "ROLE_DISABLE")){
    //        throw new NewCarException(ResultCodeEnum.FAILED, "This user account is not activated");
    //    }
    //    if(Objects.equals(userRepository.findOneById(userId).getRole(), "ROLE_BLOCKED")){
    //        throw new NewCarException(ResultCodeEnum.FAILED, "This user has been banned");
    //    }
    //    if(!Objects.equals(userRepository.findOneById(userId).getRole(), "ROLE_DEALER")){
    //        throw new NewCarException(ResultCodeEnum.FAILED, "This user is not a dealer account");
    //    }
    //    NewCarAd newCarAd = new NewCarAd();
    //    newCarAd.setModel(model);
    //    newCarAd.setGearbox(gte);
    //    newCarAd.setExampleCarId(exampleCarId);
    //    newCarAd.setUserId(userId);
    //    newCarAd.setImgURL(imgURL);
    //    newCarAdRepository.save(newCarAd);
    //}

    @Override
    public void deleteNewCarAd(ObjectId id) {
        // 检测新车广告id输入是否合法
        if(!newCarAdRepository.existsById(id)){
            throw new NewCarException(ResultCodeEnum.FAILED, "this new car is not in the database");
        }
        newCarAdRepository.deleteById(id);
    }

    //@Override
    //public void updateNewCarAd(ObjectId id, String model, GearboxTypeEnum gte, ObjectId exampleCarId, ObjectId userId) {
    //    // 检测新车广告id，变速箱模式，样本车辆id以及用户id输入是否合法
    //    if(!newCarAdRepository.existsById(id)){
    //        throw new NewCarException(ResultCodeEnum.FAILED, "this new car is not in the database");
    //    }
    //    if(!gte.equals(GearboxTypeEnum.AUTOMATIC) && !gte.equals(GearboxTypeEnum.MANUAL)){
    //        throw new NewCarException(ResultCodeEnum.NOT_FOUND, "The input gearbox mode does not exist");
    //    }
    //    if (!exampleCarRepository.existsExampleCarById(exampleCarId)){
    //        throw new NewCarException(ResultCodeEnum.NOT_FOUND, "this example car id not in the database");
    //    }
    //    if(userId.toString().length() != 24){
    //        throw new NewCarException(ResultCodeEnum.FAILED, "The user ID is invalid");
    //    }
    //    if(!userRepository.existsById(userId)){
    //        throw new NewCarException(ResultCodeEnum.NOT_FOUND, "This user ID is not in the database");
    //    }
    //    if(Objects.equals(userRepository.findOneById(userId).getRole(), "ROLE_DISABLE")){
    //        throw new NewCarException(ResultCodeEnum.FAILED, "This user account is not activated");
    //    }
    //    if(Objects.equals(userRepository.findOneById(userId).getRole(), "ROLE_BLOCKED")){
    //        throw new NewCarException(ResultCodeEnum.FAILED, "This user has been banned");
    //    }
    //    if(!Objects.equals(userRepository.findOneById(userId).getRole(), "ROLE_DEALER")){
    //        throw new NewCarException(ResultCodeEnum.FAILED, "This user is not a dealer account");
    //    }
    //    NewCarAd newCarAd = newCarAdRepository.findOneById(id);
    //    newCarAd.setModel(model);
    //    newCarAd.setGearbox(gte);
    //    newCarAd.setExampleCarId(exampleCarId);
    //    newCarAd.setUserId(userId);
    //    newCarAdRepository.save(newCarAd);
    //}

    @Override
    public List<String> getAllModel() {
        Criteria c = Criteria.where("adStatus").is("POSTED");
        Query q = new Query();
        q.addCriteria(c);
        List<NewCarAd> cars = mongoTemplate.find(q, NewCarAd.class, "newCarAds");
        List<String> rs = new ArrayList<>();
        for(NewCarAd temp : cars){
            if(!rs.contains(temp.getModel())){
                rs.add(temp.getModel());
            }
        }
        return rs;
    }

    @Override
    public List<GearboxTypeEnum> getAllGearbox() {
        Criteria c = Criteria.where("adStatus").is("POSTED");
        Query q = new Query();
        q.addCriteria(c);
        List<NewCarAd> cars = mongoTemplate.find(q, NewCarAd.class, "newCarAds");
        List<GearboxTypeEnum> rs = new ArrayList<>();
        for(NewCarAd temp : cars){
            if(!rs.contains(temp.getGearbox())){
                rs.add(temp.getGearbox());
            }
        }
        return rs;
    }

    @Override
    public NewCarAd getOneById(ObjectId id) {
        if(!newCarAdRepository.existsById(id)){
            throw new NewCarException(ResultCodeEnum.NOT_FOUND, "this car not in the database");
        }
        NewCarAd rs = newCarAdRepository.findOneById(id);
        if(rs.getAdStatus() != AdStatusEnum.POSTED){
            throw new NewCarException(ResultCodeEnum.NOT_FOUND, "this car not posted yet");
        }
        return rs;
    }

    /**
     * @description: 内部方法，检查并获取用户
     * @param {String} token 用户的token
     * @return {User} 实例化的用户
     */
    private User checkAndGetUser(String token) {
        // 检查输入的用户是否存在
        String tokenAccount = JWTUtil.getValue(token);
        if (!userRepository.existsByPhone(tokenAccount)) {
            throw new NewCarException(ResultCodeEnum.NOT_FOUND, "User not exist");
        }

        User dealer = userRepository.findOneByPhone(tokenAccount);

        return dealer;
    }

    /**
     * @description: 内部方法，检查并获取新车广告
     * @param {User} dealer 实例化的车商用户
     * @param {String} newCarId 新车广告id
     * @return {NewCarAd} 实例化的新车广告
     */
    private NewCarAd checkAndGetAd(User dealer, ObjectId newCarId) {
        // 检查输入的广告是否存在
        if (!newCarAdRepository.existsById(newCarId)) {
            throw new NewCarException(ResultCodeEnum.NOT_FOUND, "New car ad not exist");
        }

        // 检查该车商是否拥有该广告
        ArrayList<ObjectId> dealerOwnAds = dealer.getNewCarAdIds();
        if (!dealerOwnAds.contains(newCarId)) {
            throw new NewCarException(ResultCodeEnum.FAILED,
                    "Dealer do not own this second car car ad");
        }

        NewCarAd newCarAd = newCarAdRepository.findOneById(newCarId);

        return newCarAd;
    }

}

