/*
 * @Description: 基础的载具广告位，父类，没有实例
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2021-11-03 02:36:23
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-18 20:16:10
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/constant/consists/VehicleAd.java
 */
package com.easytrade.easytradeapi.constant.consists;

import com.easytrade.easytradeapi.constant.enums.*;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.MongoId;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class TempVehicleAd {
    @MongoId
    private ObjectId id;

    // 拥有者id
    private ObjectId userId;

    // 省
    @NotNull
    private String province;

    // 市
    @NotNull
    private String city;

    // 县
    @NotNull
    private String county;

    // 区
    @NotNull
    private String town;

    // 价格区间
    @NotNull
    private CarPriceLevelEnum priceLevel;

    // 广告等级
    @NotNull
    private AdLevelEnum adLevel;

    // 出售价格
    @NotNull
    private long price;

    // 卖家完成时输入的真实价格
    private long realPrice;

    // 广告位的浏览数，默认为0
    private long views = 0;

    // 广告位的点击数，默认为0
    private long clicks = 0;

    // 收藏该广告位的用户账号，长度即收藏数
    private ArrayList<String> favoriteUsers;

    /**
     * 模版载具的id
     */
    @NotNull
    private ObjectId exampleVehicleId;

    // 图片地址
    private List<String> images;

    // 视频地址
    private List<String> videos;

    // 用户自己填写的广告描述，字数在1-200之间，包括标点符号
    @Size(min = 1, max = 200)
    private String description;

    // 广告创建时间
    private Date createdDate;

    // 广告状态，创建后支付完可以上传图片和视频，然后可以发布
    private AdStatusEnum adStatus;

    // 广告取消原因
    private CancelAdReasonEnum cancelAdReason;

    // 公里数
    @NotNull
    private long kilometers;

    // 广告类型, EXAMPLECAR, NEWCARAD, SECONDHANDCARAD
    private VehicleAdTypeEnum vehicleAdType;

    // 型号
    private String model;

    // 车系
    private String series;

    // 级别
    private String level;

    // 引擎
    private String engine;

    // 变速箱
    private String gearbox;

    // 品牌
    private String brand;

    // 能源
    private String energy;

    // 最大马力
    private String maximumHorsepower;

    // 最大扭矩
    private String maximumTorque;

    // 年份
    private String year;

    // 完整年份
    private String fullYear;

    // 封面图片url
    private String coverImage;

    // 最高车速
    private String maximumSpeed;

    // 是否进口
    private String isImport;

    // 续航里程
    private String lifeMile;

    // 排放量
    private String displacement;

    // 车型
    private String type;

    /**
     * 是否有过事故
     */
    @NotNull
    private Boolean ifAccident = false;

    /**
     * 是否有运输损坏
     */
    @NotNull
    private Boolean ifShippingDamage = false;

    // 车商名
    private String companyName;

    /**
     * 是否为库存货
     */
    @NotNull
    private Boolean ifStock = false;
}
