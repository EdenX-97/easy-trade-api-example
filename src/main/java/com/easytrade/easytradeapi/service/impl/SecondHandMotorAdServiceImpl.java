/**
 * @author: Hongzhang Liu
 * @description 二手摩托接口实现类
 * @date 4/7/2022 5:08 pm
 */
package com.easytrade.easytradeapi.service.impl;

import com.easytrade.easytradeapi.constant.enums.*;
import com.easytrade.easytradeapi.constant.exceptions.NewCarException;
import com.easytrade.easytradeapi.constant.exceptions.SecondHandMotorException;
import com.easytrade.easytradeapi.model.Area;
import com.easytrade.easytradeapi.model.ExampleMotor;
import com.easytrade.easytradeapi.model.SecondHandMotorAd;
import com.easytrade.easytradeapi.model.User;
import com.easytrade.easytradeapi.repository.*;
import com.easytrade.easytradeapi.service.intf.SecondHandMotorAdService;
import com.easytrade.easytradeapi.utils.JWTUtil;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;

@Service
@Slf4j
public class SecondHandMotorAdServiceImpl implements SecondHandMotorAdService {

    @Autowired
    SecondHandMotorAdRepository secondHandMotorAdRepository;

    @Autowired
    ExampleMotorRepository exampleMotorRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AreaRepository areaRepository;

    @Value("${vehiclead.create.max-num}")
    private Integer adCreateMaxNum;

    @Value("${vehiclead.realPrice.percent}")
    private long realPricePercent;

    @Override
    public ObjectId createSecondHandMotorAd(SecondHandMotorAd secondHandMotorAd, String token) {
        // 判断事故和运损不可能都为true的逻辑
        if (secondHandMotorAd.getIfAccident() && secondHandMotorAd.getIfShippingDamage()) {
            throw new SecondHandMotorException(ResultCodeEnum.FAILED,
                    "Vehicle cannot be accident and shipping damage at same time");
        }

        // 检查并获取用户
        User user = checkAndGetUser(token);

        // 如果车商未创建广告，则新建list
        ArrayList<ObjectId> secondHandMotorAdIds = user.getSecondHandMotorAdIds();
        if (secondHandMotorAdIds == null) {
            secondHandMotorAdIds = new ArrayList<>();
        }
        if (secondHandMotorAdIds.size() + 1 > adCreateMaxNum) {
            throw new SecondHandMotorException(ResultCodeEnum.FAILED, "Create second hand motor ads number over max");
        }

        // 检查地址信息
        Area area = areaRepository.findOneByProvinceAndCityAndCountyAndTown(secondHandMotorAd.getProvince(),
                secondHandMotorAd.getCity(), secondHandMotorAd.getCounty(), secondHandMotorAd.getTown());
        if (area == null) {
            throw new SecondHandMotorException(ResultCodeEnum.NOT_FOUND, "Address do not exist");
        }

        // 检查价格是否在区间内
        long price = secondHandMotorAd.getPrice();
        MotorPriceLevelEnum pricelevel = secondHandMotorAd.getPriceLevel();
        if (price < pricelevel.getMinPrice() || price > pricelevel.getMaxPrice()) {
            throw new SecondHandMotorException(ResultCodeEnum.FAILED,
                    "Created second hand motor price not within correct range");
        }

        // 根据输入的车型名称获取车辆模版信息
        ExampleMotor exampleMotor = exampleMotorRepository.findOneById(secondHandMotorAd.getExampleVehicleId());
        if (exampleMotor == null) {
            throw new SecondHandMotorException(ResultCodeEnum.NOT_FOUND,
                    "Created second hand motor model do not exist");
        }

        // 创建新的新车广告变量来储存信息，避免调用api时输入恶意数据
        SecondHandMotorAd savedSecondHandMotorAd = new SecondHandMotorAd();

        // 保存车辆模版的id，便于获取车辆模版信息；设置状态为创建；设置创建时间；地址的代码信息；其他信息
        // 保存车辆模版的id，便于获取车辆模版信息；设置状态为创建；设置创建时间；地址的代码信息；其他信息
        savedSecondHandMotorAd.setExampleVehicleId(exampleMotor.getId());
        savedSecondHandMotorAd.setUserId(user.getId());
        savedSecondHandMotorAd.setAdStatus(AdStatusEnum.CREATED);
        savedSecondHandMotorAd.setCreatedDate(new Date());
        savedSecondHandMotorAd.setProvince(secondHandMotorAd.getProvince());
        savedSecondHandMotorAd.setCity(secondHandMotorAd.getCity());
        savedSecondHandMotorAd.setCounty(secondHandMotorAd.getCounty());
        savedSecondHandMotorAd.setTown(secondHandMotorAd.getTown());
        savedSecondHandMotorAd.setPriceLevel(secondHandMotorAd.getPriceLevel());
        savedSecondHandMotorAd.setAdLevel(secondHandMotorAd.getAdLevel());
        savedSecondHandMotorAd.setPurchaseDate(secondHandMotorAd.getPurchaseDate());
        //savedSecondHandCarAd.setProductionDate(secondHandCarAd.getProductionDate());
        savedSecondHandMotorAd.setBigFrame(secondHandMotorAd.getBigFrame());
        savedSecondHandMotorAd.setLicense(secondHandMotorAd.getLicense());
        savedSecondHandMotorAd.setPrice(secondHandMotorAd.getPrice());
        savedSecondHandMotorAd.setOriginalPrice(secondHandMotorAd.getPrice());
        savedSecondHandMotorAd.setKilometers(secondHandMotorAd.getKilometers());
        savedSecondHandMotorAd.setDescription(secondHandMotorAd.getDescription());
        savedSecondHandMotorAd.setVehicleAdType(VehicleAdTypeEnum.SECONDHANDMOTORAD);
        savedSecondHandMotorAd.setShowContactNum(secondHandMotorAd.getShowContactNum());
        savedSecondHandMotorAd.setContactNum(secondHandMotorAd.getContactNum());
        savedSecondHandMotorAd.setIfAccident(secondHandMotorAd.getIfAccident());
        savedSecondHandMotorAd.setIfShippingDamage(secondHandMotorAd.getIfShippingDamage());

        // 保存新车广告到数据库，并将新车广告id保存至车商数据
        ObjectId saveCarAdId = secondHandMotorAdRepository.save(savedSecondHandMotorAd).getId();
        secondHandMotorAdIds.add(saveCarAdId);
        user.setSecondHandMotorAdIds(secondHandMotorAdIds);
        userRepository.save(user);

        // 返回创建的新车id
        return saveCarAdId;
    }

    @Override
    public void completeSecondHandMotorAd(ObjectId secondHandMotorId, long realPrice, String token) {
        // 检查并获取用户
        User user = checkAndGetUser(token);

        // 检查并获取广告
        SecondHandMotorAd secondHandMotorAd = checkAndGetAd(user, secondHandMotorId);

        // 检查二手车广告状态，只有不处于canceled状态才能完成
        if (!secondHandMotorAd.getAdStatus().equals(AdStatusEnum.POSTED)) {
            throw new SecondHandMotorException(ResultCodeEnum.FAILED, "Can only complete posted ad");
        }

        // 检查真实价格
        long price = secondHandMotorAd.getPrice();
        long maxRealPrice = price + price * realPricePercent / 100;
        long minRealPrice = price - price * realPricePercent / 100;
        if (realPrice < minRealPrice || realPrice > maxRealPrice) {
            throw new SecondHandMotorException(ResultCodeEnum.FAILED, "Real price inappropriate");
        }

        // 设置新车广告为完成，设置完成价格，保存至数据库
        secondHandMotorAd.setAdStatus(AdStatusEnum.COMPLETED);
        secondHandMotorAd.setRealPrice(realPrice);
        secondHandMotorAdRepository.save(secondHandMotorAd);

        // 从用户列表中删除该广告id，添加到完成列表
        ArrayList<ObjectId> userCompletedAds = user.getCompletedAdIds();
        if (userCompletedAds == null) {
            userCompletedAds = new ArrayList<ObjectId>();
        }
        ArrayList<ObjectId> userOwnAds = user.getSecondHandMotorAdIds();
        userOwnAds.remove(secondHandMotorId);
        userCompletedAds.add(secondHandMotorId);
        user.setSecondHandMotorAdIds(userOwnAds);
        user.setCompletedAdIds(userCompletedAds);
        userRepository.save(user);
    }

    @Override
    public void cancelSecondHandMotorAd(ObjectId secondHandMotorId, String token) {
        // 检查并获取用户
        User user = checkAndGetUser(token);

        // 检查并获取广告
        SecondHandMotorAd secondHandMotorAd = checkAndGetAd(user, secondHandMotorId);

        // 检查二手车广告状态，只有不处于canceled状态才能取消
        if (secondHandMotorAd.getAdStatus().equals(AdStatusEnum.CANCELED)) {
            throw new SecondHandMotorException(ResultCodeEnum.FAILED, "Cannot cancel canceled ad");
        }

        // 校验完毕，删除目标广告
        cancelTargetAd(secondHandMotorAd, user, CancelAdReasonEnum.ACTIVELY);
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
     * @description: 内部方法，检查并获取二手车广告
     * @param {User} user 实例化的用户
     * @param {String} secondHandCarId 二手车广告id
     * @return {SecondHandCarAd} 实例化的二手车广告
     */
    private SecondHandMotorAd checkAndGetAd(User user, ObjectId secondHandMotorId) {
        // 检查输入的广告是否存在
        if (!secondHandMotorAdRepository.existsById(secondHandMotorId)) {
            throw new SecondHandMotorException(ResultCodeEnum.NOT_FOUND, "Second hand motor ad not exist");
        }

        // 检查该用户是否拥有该广告
        ArrayList<ObjectId> userOwnAds = user.getSecondHandMotorAdIds();
        if (!userOwnAds.contains(secondHandMotorId)) {
            throw new SecondHandMotorException(ResultCodeEnum.FAILED,
                    "User do not own this second hand motor ad");
        }

        SecondHandMotorAd secondHandMotorAd = secondHandMotorAdRepository.findOneById(secondHandMotorId);

        return secondHandMotorAd;
    }

    @Override
    public void cancelTargetAd(SecondHandMotorAd secondHandMotorAd, User user, CancelAdReasonEnum cancelAdReason) {
        // 设置二手车广告为取消，设置原因为主动取消
        secondHandMotorAd.setAdStatus(AdStatusEnum.CANCELED);
        secondHandMotorAd.setCancelAdReason(cancelAdReason);
        secondHandMotorAdRepository.save(secondHandMotorAd);

        // 从用户列表中删除该广告id
        ArrayList<ObjectId> userOwnAds = user.getSecondHandMotorAdIds();
        userOwnAds.remove(secondHandMotorAd.getId());
        user.setSecondHandMotorAdIds(userOwnAds);
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
}
