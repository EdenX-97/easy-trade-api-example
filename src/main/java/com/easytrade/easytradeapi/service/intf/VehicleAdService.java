/*
 * @Description: 载具服务层的接口
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2021-11-13 19:44:54
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-18 00:48:18
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/service/intf/VehicleAdService.java
 */
package com.easytrade.easytradeapi.service.intf;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.easytrade.easytradeapi.constant.consists.TempVehicleAd;
import com.easytrade.easytradeapi.constant.consists.VehicleAd;
import com.easytrade.easytradeapi.constant.enums.*;
import org.bson.types.ObjectId;
import org.springframework.web.multipart.MultipartFile;

public interface VehicleAdService {
    /**
     * 通过id获取载具
     *
     * @param id 载具id
     * @return {@link VehicleAd} 返回的车辆广告
     */
    public VehicleAd getVehicleById(ObjectId id);

    /**
     * 通过id获取载具标题
     *
     * @param id 载具id
     * @return {@link String} 返回的载具标题
     */
    public String getVehicleTitleById(ObjectId id);

    /**
     * 通过id获取车辆类型
     *
     * @param id 载具id
     * @return {@link VehicleAdTypeEnum} 返回的载具类型
     */
    public VehicleAdTypeEnum getVehicleTypeById(ObjectId id);

    /**
     * @description: 浏览载具广告时，增加浏览数
     * @param {ObjectId} id 浏览载具广告的id
     * @param {VehicleAdTypeEnum} type 浏览载具广告的类型
     * @return {*}
     */
    public void view(ObjectId id);

    /**
     * @description: 点击载具广告时，增加点击数
     * @param {ObjectId} id 点击载具广告的id
     * @param {VehicleAdTypeEnum} type 点击载具广告的类型
     * @return {*}
     */
    public void click(ObjectId id);

    /**
     * @description: 点击载具广告收藏时，增加收藏数，并为用户添加收藏记录
     * @param {ObjectId} id 收藏载具广告的id
     * @param {VehicleAdTypeEnum} type 收藏载具广告的类型
     * @param {String} account 用户账号
     * @return {*}
     */
    public void favorite(ObjectId id, String account);

    /**
     * @description: 点击取消载具广告收藏时，减少收藏数，并删除用户收藏记录
     * @param {ObjectId} id 取消收藏载具广告的id
     * @param {VehicleAdTypeEnum} type 取消收藏载具广告的类型
     * @param {String} account 用户账号
     * @return {*}
     */
    public void cancelFavorite(ObjectId id, String account);

    /**
     * @description: 获取该载具广告位的浏览数
     * @param {ObjectId} id 载具广告的id
     * @param {VehicleAdTypeEnum} type 载具广告的类型
     * @return {long} 浏览数
     */
    public long getViews(ObjectId id);

    /**
     * @description: 获取该载具广告位的点击数
     * @param {ObjectId} id 载具广告的id
     * @param {VehicleAdTypeEnum} type 载具广告的类型
     * @return {long} 点击数
     */
    public long getClicks(ObjectId id);

    /**
     * @description: 获取该载具广告位的收藏者
     * @param {ObjectId} id 载具广告的id
     * @param {VehicleAdTypeEnum} type 载具广告的类型
     * @return {ArrayList<String>} 收藏数信息
     */
    public ArrayList<String> getFavorites(ObjectId id);

    /**
     * @description: 将redis中的缓存数据全都更新到mongodb，包含了浏览数、点击数、收藏信息
     * @param {*}
     * @return {*}
     */
    public void updateAdInRedisToMongodb();

    /**
     * @description: 上传载具图片
     * @param {ObjectId} id 载具广告的id
     * @param {VehicleAdTypeEnum} type 载具广告的类型
     * @param {FileTypeEnum} fileType 上传文件的类型
     * @param {MultipartFile[]} files 上传的文件，可包含多个文件
     * @return {*}
     */
    public void uploadFiles(ObjectId id, FileTypeEnum fileType, MultipartFile[] files, String token) throws IOException;

    /**
     * @description: 检查是否能够上传文件
     * @param {ObjectId} id 载具广告的id
     * @param {VehicleAdTypeEnum} type 载具广告的类型
     * @param {FileTypeEnum} filesType 上传文件的类型
     * @return {Boolean} true为该文件类型已上传完毕，false为未上传
     */
    public Boolean checkIfUpload(ObjectId id, FileTypeEnum fileType);

    /**
     * 获取车辆价格等级
     *
     * @param price 价格
     * @return {@link CarPriceLevelEnum}
     */
    public CarPriceLevelEnum getCarPriceLevel(long price);

    /**
     * 获取摩托价格等级
     *
     * @param price 价格
     * @return {@link MotorPriceLevelEnum}
     */
    public MotorPriceLevelEnum getMotorPriceLevel(long price);

    /**
     * @description: 获取对应广告的价格
     * @param {AdLevelEnum} adLevel 广告等级
     * @param {PriceLevelEnum} priceLevel 价格区间
     * @return {long} 对应的广告价格
     */    
    public long getAdPrice(AdLevelEnum adLevel, long price, VehicleAdTypeEnum type);

    /**
     * 修改载具价格
     *
     * @param token 令牌
     * @param id    id
     * @param price 价格
     * @param type  类型
     */
    public void changePrice(String token, ObjectId id, VehicleAdTypeEnum type, long price);

    /**
     * 根据载具id，用户账号（手机号）以及载具类型判断车辆的收藏状态
     *
     * @param id    载具id
     * @param phone 用户账号（手机号
     * @return boolean
     */
    public boolean getFavouriteState(ObjectId id, String phone);


    /**
     * 根据载具id在阿里云OSS上获得所有相关图片的url
     *
     * @param id 载具id
     * @return {@link ArrayList}<{@link URL}>
     */
    public ArrayList<URL> getImagesFromOSSById(ObjectId id);

    /**
     * 根据载具id在阿里云OSS上获得第一张相关图片的url（获取封面）
     *
     * @param id 载具id
     * @return {@link URL}
     */
    public URL getFirstImageFromOSSById(ObjectId id);


    /**
     * 得到筛选后的汽车
     *
     * @param conditions 根据条件数组获得汽车结果集
     * @param pageNumber 页码
     * @return {@link List}<{@link TempVehicleAd}>
     */
    public List<Object> getFilterCars(ArrayList<String> conditions, int pageNumber);

    /**
     * 得到筛选后的摩托
     *
     * @param conditions 根据条件数组获得摩托结果集
     * @param pageNumber 页码
     * @return {@link List}<{@link TempVehicleAd}>
     */
    public List<Object> getFilterMotors(ArrayList<String> conditions, int pageNumber);


    /**
     * 得到模糊搜索后匹配的所有汽车载具模型
     *
     * @param model 模型名称
     * @param pageNumber 页码
     * @return {@link List}<{@link Object}>
     */
    public List<Object> getSearchCarModel(String model, int pageNumber);

    /**
     * 得到模糊搜索后匹配的所有摩托载具模型
     *
     * @param model 模型名称
     * @param pageNumber 页码
     * @return {@link List}<{@link Object}>
     */
    public List<Object> getSearchMotorModel(String model, int pageNumber);

    /**
     * 得到模糊搜索后匹配车商名称的所有汽车载具模型
     *
     * @param companyName 公司名称
     * @param pageNumber  页码
     * @return {@link List}<{@link Object}>
     */
    public List<Object> getSearchCarCompanyName(String companyName, int pageNumber);

    /**
     * 得到模糊搜索后匹配车商名称的所有摩托载具模型
     *
     * @param companyName 公司名称
     * @param pageNumber  页码
     * @return {@link List}<{@link Object}>
     */
    public List<Object> getSearchMotorCompanyName(String companyName, int pageNumber);
}
