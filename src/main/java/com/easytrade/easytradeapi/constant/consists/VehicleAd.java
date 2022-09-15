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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import com.easytrade.easytradeapi.constant.enums.*;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.MongoId;
import lombok.Data;

@Data
public class VehicleAd {
    @MongoId
    private ObjectId id;

    //// 拥有者账号
    //@NotNull
    //private String ownerAccount;

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

    // 广告等级
    @NotNull
    private AdLevelEnum adLevel;

    // 出售价格
    @NotNull
    private long price;

    // 初始设定的价格
    @NotNull
    private long originalPrice;

    // 卖家完成时输入的真实价格
    private long realPrice;

    // 广告位的浏览数，默认为0
    private long views = 0;

    // 广告位的点击数，默认为0
    private long clicks = 0;

    // 收藏该广告位的用户账号，长度即收藏数
    private ArrayList<String> favoriteUsers;

    // 公里数
    @NotNull
    private long kilometers;

    /**
     * 用户选择是否显示联系方式
     */
    @NotNull
    private Boolean showContactNum;

    /**
     * 联系号码
     */
    @NotNull
    @Pattern(regexp = "^1[0-9]{10}$")
    private String contactNum;

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

    // 广告类型, EXAMPLECAR, NEWCARAD, SECONDHANDCARAD
    private VehicleAdTypeEnum vehicleAdType;

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

    /**
     * 是否为库存车
     */
    @NotNull
    private Boolean ifStock = false;
}
