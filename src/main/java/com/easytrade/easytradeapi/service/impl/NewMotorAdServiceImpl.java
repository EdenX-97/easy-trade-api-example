/**
 * @author: Hongzhang Liu
 * @description 新摩托接口实现类
 * @date 4/7/2022 5:08 pm
 */
package com.easytrade.easytradeapi.service.impl;

import com.easytrade.easytradeapi.constant.enums.*;
import com.easytrade.easytradeapi.constant.exceptions.NewCarException;
import com.easytrade.easytradeapi.constant.exceptions.NewMotorException;
import com.easytrade.easytradeapi.constant.exceptions.SecondHandMotorException;
import com.easytrade.easytradeapi.model.Area;
import com.easytrade.easytradeapi.model.ExampleMotor;
import com.easytrade.easytradeapi.model.NewMotorAd;
import com.easytrade.easytradeapi.model.User;
import com.easytrade.easytradeapi.repository.AreaRepository;
import com.easytrade.easytradeapi.repository.ExampleMotorRepository;
import com.easytrade.easytradeapi.repository.NewMotorAdRepository;
import com.easytrade.easytradeapi.repository.UserRepository;
import com.easytrade.easytradeapi.service.intf.NewMotorAdService;
import com.easytrade.easytradeapi.utils.JWTUtil;
import com.easytrade.easytradeapi.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;

@Service
@Slf4j
public class NewMotorAdServiceImpl implements NewMotorAdService {

    @Autowired
    NewMotorAdRepository newMotorAdRepository;

    @Autowired
    ExampleMotorRepository exampleMotorRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AreaRepository areaRepository;

    @Autowired
    RedisUtil redisUtil;

    @Value("${vehiclead.create.max-num}")
    private Integer adCreateMaxNum;

    @Value("${vehiclead.realPrice.percent}")
    private long realPricePercent;

    @Override
    public ObjectId createNewMotorAd(NewMotorAd newMotorAd, String token) {
        // 判断事故和运损不可能都为true的逻辑
        if (newMotorAd.getIfAccident() && newMotorAd.getIfShippingDamage()) {
            throw new SecondHandMotorException(ResultCodeEnum.FAILED,
                    "Vehicle cannot be accident and shipping damage at same time");
        }

        // 检查并获取用户
        User dealer = checkAndGetUser(token);

        // 如果车商未创建广告，则新建list
        ArrayList<ObjectId> newMotorAdIds = dealer.getNewMotorAdIds();
        if (newMotorAdIds == null) {
            newMotorAdIds = new ArrayList<>();
        }
        if (newMotorAdIds.size() + 1 > adCreateMaxNum) {
            throw new NewMotorException(ResultCodeEnum.FAILED, "Create new motor ads number over max");
        }

        // 检查地址信息
        Area area = areaRepository.findOneByProvinceAndCityAndCountyAndTown(newMotorAd.getProvince(),
                newMotorAd.getCity(), newMotorAd.getCounty(), newMotorAd.getTown());
        if (area == null) {
            throw new NewMotorException(ResultCodeEnum.NOT_FOUND, "Address do not exist");
        }

        // 检查价格是否在区间内
        long price = newMotorAd.getPrice();
        MotorPriceLevelEnum pricelevel = newMotorAd.getPriceLevel();
        if (price < pricelevel.getMinPrice() || price > pricelevel.getMaxPrice()) {
            throw new NewMotorException(ResultCodeEnum.FAILED,
                    "Created new motor price not within correct range");
        }

        // 根据输入的车型名称获取车辆模版信息
        ExampleMotor exampleMotor = exampleMotorRepository.findOneById(newMotorAd.getExampleVehicleId());
        if (exampleMotor == null) {
            throw new NewMotorException(ResultCodeEnum.NOT_FOUND,
                    "Created new motor model do not exist");
        }

        // 创建新的新车广告变量来储存信息，避免调用api时输入恶意数据
        NewMotorAd savedNewMotorAd = new NewMotorAd();

        // 保存车辆模版的id，便于获取车辆模版信息；设置状态为创建；设置创建时间；地址的代码信息；其他信息
        savedNewMotorAd.setExampleVehicleId(exampleMotor.getId());
        savedNewMotorAd.setUserId(dealer.getId());
        savedNewMotorAd.setAdStatus(AdStatusEnum.CREATED);
        savedNewMotorAd.setCreatedDate(new Date());
        savedNewMotorAd.setBigFrame(newMotorAd.getBigFrame());
        savedNewMotorAd.setProvince(newMotorAd.getProvince());
        savedNewMotorAd.setCity(newMotorAd.getCity());
        savedNewMotorAd.setCounty(newMotorAd.getCounty());
        savedNewMotorAd.setTown(newMotorAd.getTown());
        savedNewMotorAd.setPriceLevel(newMotorAd.getPriceLevel());
        savedNewMotorAd.setAdLevel(newMotorAd.getAdLevel());
        savedNewMotorAd.setPrice(newMotorAd.getPrice());
        savedNewMotorAd.setOriginalPrice(newMotorAd.getPrice());
        savedNewMotorAd.setKilometers(newMotorAd.getKilometers());
        savedNewMotorAd.setDescription(newMotorAd.getDescription());
        savedNewMotorAd.setVehicleAdType(VehicleAdTypeEnum.NEWMOTORAD);
        savedNewMotorAd.setShowContactNum(newMotorAd.getShowContactNum());
        savedNewMotorAd.setContactNum(newMotorAd.getContactNum());
        savedNewMotorAd.setIfAccident(newMotorAd.getIfAccident());
        savedNewMotorAd.setIfShippingDamage(newMotorAd.getIfShippingDamage());


        // 保存新车广告到数据库，并将新车广告id保存至车商数据
        ObjectId saveCarAdId = newMotorAdRepository.save(savedNewMotorAd).getId();
        newMotorAdIds.add(saveCarAdId);
        dealer.setNewMotorAdIds(newMotorAdIds);
        userRepository.save(dealer);

        // 返回创建的新车id
        return saveCarAdId;
    }

    @Override
    public void completeNewMotorAd(ObjectId newMotorId, long realPrice, String token) {
        // 检查并获取用户
        User dealer = checkAndGetUser(token);

        // 检查并获取广告
        NewMotorAd newMotorAd = checkAndGetAd(dealer, newMotorId);

        // 检查新车广告状态，只有处于posted状态才能完成
        if (!newMotorAd.getAdStatus().equals(AdStatusEnum.POSTED)) {
            throw new NewMotorException(ResultCodeEnum.FAILED, "Can only complete posted ad");
        }

        // 检查真实价格
        long price = newMotorAd.getPrice();
        long maxRealPrice = price + price * realPricePercent / 100;
        long minRealPrice = price - price * realPricePercent / 100;
        if (realPrice < minRealPrice || realPrice > maxRealPrice) {
            throw new NewMotorException(ResultCodeEnum.FAILED, "Real price inappropriate");
        }

        // 设置新车广告为完成，设置完成价格，保存至数据库
        newMotorAd.setAdStatus(AdStatusEnum.COMPLETED);
        newMotorAd.setRealPrice(realPrice);
        newMotorAdRepository.save(newMotorAd);

        // 从用户列表中删除该广告id，添加到完成列表
        ArrayList<ObjectId> dealerCompletedAds = dealer.getCompletedAdIds();
        if (dealerCompletedAds == null) {
            dealerCompletedAds = new ArrayList<ObjectId>();
        }
        ArrayList<ObjectId> dealerOwnAds = dealer.getNewMotorAdIds();
        dealerOwnAds.remove(newMotorId);
        dealerCompletedAds.add(newMotorId);
        dealer.setNewMotorAdIds(dealerOwnAds);
        dealer.setCompletedAdIds(dealerCompletedAds);
        userRepository.save(dealer);
    }

    @Override
    public void cancelNewMotorAd(ObjectId newMotorId, String token) {
        // 检查并获取用户
        User dealer = checkAndGetUser(token);

        // 检查并获取广告
        NewMotorAd newMotorAd = checkAndGetAd(dealer, newMotorId);

        // 检查新车广告状态，只有不处于canceled状态才能取消
        if (newMotorAd.getAdStatus().equals(AdStatusEnum.CANCELED)) {
            throw new NewMotorException(ResultCodeEnum.FAILED, "Cannot cancel canceled ad");
        }

        // 校验完毕，删除目标广告
        cancelTargetAd(newMotorAd, dealer, CancelAdReasonEnum.ACTIVELY);
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
     * @description: 内部方法，检查并获取新摩托车广告
     * @param {User} dealer 实例化的车商用户
     * @param {String} newCarId 新摩托车广告id
     * @return {NewCarAd} 实例化的摩托车广告
     */
    private NewMotorAd checkAndGetAd(User dealer, ObjectId newMotorId) {
        // 检查输入的广告是否存在
        if (!newMotorAdRepository.existsById(newMotorId)) {
            throw new NewCarException(ResultCodeEnum.NOT_FOUND, "New motor ad not exist");
        }

        // 检查该车商是否拥有该广告
        ArrayList<ObjectId> dealerOwnAds = dealer.getNewMotorAdIds();
        if (!dealerOwnAds.contains(newMotorId)) {
            throw new NewMotorException(ResultCodeEnum.FAILED,
                    "Dealer do not own this new car motor ad");
        }

        NewMotorAd newMotorAd = newMotorAdRepository.findOneById(newMotorId);

        return newMotorAd;
    }

    @Override
    public void cancelTargetAd(NewMotorAd newMotorAd, User dealer, CancelAdReasonEnum cancelAdReason) {
        // 设置新车广告为取消，设置原因为主动取消，并保存至数据库
        newMotorAd.setAdStatus(AdStatusEnum.CANCELED);
        newMotorAd.setCancelAdReason(cancelAdReason);
        newMotorAdRepository.save(newMotorAd);

        // 从用户列表中删除该广告id
        ArrayList<ObjectId> dealerOwnAds = dealer.getNewMotorAdIds();
        dealerOwnAds.remove(newMotorAd.getId());
        dealer.setNewMotorAdIds(dealerOwnAds);
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

//    @Override
//    public void updatePostedAdsInRedis() {
//        List<NewMotorAd> allPostedNewMotorAds =
//                newMotorAdRepository.findAllByAdStatus(AdStatusEnum.POSTED);
//        String redisKey = "posted:newMotor";
//        redisUtil.set(redisKey, JSON.toJSONString(allPostedNewMotorAds));
//
//        log.info("Complete update posted new motor ads in redis");
//    }
}
