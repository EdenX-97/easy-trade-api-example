/*
 * @Description: 载具广告位控制器
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2021-11-13 19:34:26
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-16 18:15:21
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/controller/VehicleAdController.java
 */
package com.easytrade.easytradeapi.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.easytrade.easytradeapi.constant.consists.Result;
import com.easytrade.easytradeapi.constant.consists.TempVehicleAd;
import com.easytrade.easytradeapi.constant.enums.AdLevelEnum;
import com.easytrade.easytradeapi.constant.enums.FileTypeEnum;
import com.easytrade.easytradeapi.constant.enums.VehicleAdTypeEnum;
import com.easytrade.easytradeapi.service.intf.VehicleAdService;
import com.easytrade.easytradeapi.utils.ReturnResultUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@Validated
public class VehicleAdController {
    @Autowired
    VehicleAdService vehicleService;

    // // 每周二、四、六凌晨3.00的定时
    private final static String updateAdInRedisToMongodbSchedule = "0 0 3 ? * TUE,THU,SAT";

    /**
     * 通过id获取载具
     *
     * @param id 载具id
     * @return {@link Result} 返回的结果
     */
    @GetMapping("/vehicle/getVehicleById")
    public Result getVehicleById(@RequestParam @NotNull ObjectId id) {
        return ReturnResultUtil.success(vehicleService.getVehicleById(id));
    }

    /**
     * 通过id获取车辆标题
     *
     * @param id 载具id
     * @return {@link Result} 返回的结果
     */
    @GetMapping("/vehicle/getVehicleTitleById")
    public Result getVehicleTitleById(@RequestParam @NotNull ObjectId id) {
        return ReturnResultUtil.success(vehicleService.getVehicleTitleById(id));
    }

    /**
     * 通过id获取车辆类型
     *
     * @param id 载具id
     * @return {@link Result} 返回的结果
     */
    @GetMapping("/vehicle/getVehicleTypeById")
    public Result getVehicleTypeById(@RequestParam @NotNull ObjectId id) {
        return ReturnResultUtil.success(vehicleService.getVehicleTypeById(id));
    }



    /**
     * @description: 浏览载具广告时，增加浏览数
     * @param {ObjectId} id 浏览载具广告的id
     * @param {VehicleAdTypeEnum} type 浏览载具广告的类型
     * @return {Result} 结果信息
     */
    @PostMapping("/vehicle/view")
    public Result view(@RequestParam @NotNull ObjectId id) {
        vehicleService.view(id);
        return ReturnResultUtil.success();
    }

    /**
     * @description: 点击载具广告时，增加点击数
     * @param {ObjectId} id 点击载具广告的id
     * @param {VehicleAdTypeEnum} type 点击载具广告的类型
     * @return {Result} 结果信息
     */
    @PostMapping("/vehicle/click")
    public Result click(@RequestParam @NotNull ObjectId id) {
        vehicleService.click(id);
        return ReturnResultUtil.success();
    }

    /**
     * @description: 点击载具广告收藏时，增加收藏数，并为用户添加收藏记录
     * @param {ObjectId} id 收藏载具广告的id
     * @param {VehicleAdTypeEnum} type 收藏载具广告的类型
     * @param {String} account 收藏载具广告的账号
     * @return {Result} 结果信息
     */
    @PostMapping("/vehicle/favorite")
    public Result favorite(@RequestParam @NotNull ObjectId id,
                           @RequestParam @NotNull String account) {
        vehicleService.favorite(id, account);
        return ReturnResultUtil.success();
    }

    /**
     * @description: 点击取消载具广告收藏时，减少收藏数，并删除用户收藏记录
     * @param {ObjectId} id 取消收藏载具广告的id
     * @param {VehicleAdTypeEnum} type 取消收藏载具广告的类型
     * @param {String} account 取消收藏载具广告的账号
     * @return {Result} 结果信息
     */
    @PostMapping("/vehicle/cancelFavorite")
    public Result cancelFavorite(@RequestParam @NotNull ObjectId id,
                                 @RequestParam @NotNull String account) {
        vehicleService.cancelFavorite(id, account);
        return ReturnResultUtil.success();
    }

    /**
     * @description: 获取该载具广告位的浏览数
     * @param {ObjectId} id 载具广告的id
     * @param {VehicleAdTypeEnum} type 载具广告的类型
     * @return {Result} 结果信息，包含浏览数信息
     */
    @GetMapping("/vehicle/getViews")
    @Cacheable(value = "times:views", key = "#id")
    public long getViews(@RequestParam ObjectId id) {
        return vehicleService.getViews(id);
    }

    /**
     * @description: 获取该载具广告位的点击数
     * @param {ObjectId} id 载具广告的id
     * @param {VehicleAdTypeEnum} type 载具广告的类型
     * @return {Result} 结果信息，包含点击数信息
     */
    @GetMapping("/vehicle/getClicks")
    @Cacheable(cacheNames = "times:clicks", key = "#id")
    public long getClicks(@RequestParam @NotNull ObjectId id) {
        return vehicleService.getClicks(id);
    }

    /**
     * @description: 获取该载具广告位的收藏数
     * @param {ObjectId} id 载具广告的id
     * @param {VehicleAdTypeEnum} type 载具广告的类型
     * @return {Result} 结果信息，包含收藏数信息
     */
    @GetMapping("/vehicle/getFavoritesNum")
    @Cacheable(cacheNames = "times:favorites", key = "#id")
    public long getFavoritesNum(@RequestParam @NotNull ObjectId id) {
        return vehicleService.getFavorites(id).size();
    }

    /**
     * @description: 获取该载具广告位的收藏者列表
     * @param {ObjectId} id 载具广告的id
     * @param {VehicleAdTypeEnum} type 载具广告的类型
     * @return {Result} 结果信息，包含收藏者列表信息
     */
    @GetMapping("/vehicle/getFavoriteUsers")
    public List<String> getFavoriteUsers(@RequestParam @NotNull ObjectId id) {
        return vehicleService.getFavorites(id);
    }

    /**
     * @description: 将redis中的缓存数据全都更新到mongodb，包含了浏览数、点击数、收藏信息，固定时间自动执行
     * @param {*}
     * @return {Result} 结果信息
     */
    @GetMapping("/vehicle/updateRedisToMongodb")
    @Scheduled(cron = updateAdInRedisToMongodbSchedule)
    @Transactional
    public Result updateAdInRedisToMongodb() {
        vehicleService.updateAdInRedisToMongodb();
        return ReturnResultUtil.success();
    }

    /**
     * @description: 上传载具图片或视频
     * @param {ObjectId} id 载具广告的id
     * @param {VehicleAdTypeEnum} type 载具广告的类型
     * @param {FileTypeEnum} fileType 上传文件的类型
     * @param {MultipartFile[]} files 上传的文件，可包含多个文件
     * 
     * @return {Result} 结果信息
     */
    @PostMapping("/vehicle/uploadFiles")
    @Transactional(rollbackFor = IOException.class)
    public Result uploadFiles(
            @RequestParam @NotNull ObjectId id,
            @RequestParam @NotNull FileTypeEnum fileType,
            @RequestParam("file") @NotNull MultipartFile[] files,
            @RequestHeader(name = "Authorization") @NotNull @NotBlank String token) throws IOException {
        vehicleService.uploadFiles(id, fileType, files, token);
        return ReturnResultUtil.success();
    }

    /**
     * @description: 检查是否能够上传文件
     * @param {ObjectId} id 载具广告的id
     * @param {VehicleAdTypeEnum} type 载具广告的类型
     * @param {FileTypeEnum} filesType 上传文件的类型
     * @return {Result} 结果信息
     */
    @PostMapping("/vehicle/checkIfUpload")
    public Result checkIfUpload(@RequestParam @NotNull ObjectId id,
            @RequestParam @NotNull FileTypeEnum fileType) {
        vehicleService.checkIfUpload(id, fileType);
        return ReturnResultUtil.success();
    }

    /**
     * 获取车辆价格等级
     *
     * @param price 价格
     * @return {@link Result}
     */
    @GetMapping("/vehicle/getCarPriceLevel")
    public Result getCarPriceLevel(@RequestParam @NotNull long price) {
        return ReturnResultUtil.success(vehicleService.getCarPriceLevel(price));
    }

    /**
     * 获取摩托价格等级
     *
     * @param price 价格
     * @return {@link Result}
     */
    @GetMapping("/vehicle/getMotorPriceLevel")
    public Result getMotorPriceLevel(@RequestParam @NotNull long price) {
        return ReturnResultUtil.success(vehicleService.getMotorPriceLevel(price));
    }

    /**
     * 获取广告价格
     *
     * @param adLevel 广告等级
     * @param price   价格
     * @param type    类型
     * @return {@link Result}
     */
    @GetMapping("/vehicle/getAdPrice")
    public Result getAdPrice(@RequestParam @NotNull AdLevelEnum adLevel,
                             @RequestParam @NotNull long price,
                             @RequestParam @NotNull VehicleAdTypeEnum type) {
        return ReturnResultUtil.success(vehicleService.getAdPrice(adLevel, price, type));
    }

    /**
     * 修改载具价格
     *
     * @param token 令牌
     * @param id    id
     * @param type  类型
     * @param price 价格
     * @return {@link Result}
     */
    @PostMapping("/vehicle/changePrice")
    public Result changePrice(@RequestHeader(name = "Authorization") @NotNull @NotBlank String token,
                              @RequestParam @NotNull ObjectId id,
                              @RequestParam @NotNull VehicleAdTypeEnum type,
                              @RequestParam @NotNull long price) {
        vehicleService.changePrice(token, id, type, price);
        return ReturnResultUtil.success();
    }

    /**
     * 根据载具id，用户账号（手机号）以及载具类型判断车辆的收藏状态
     *
     * @param id    载具id
     * @param phone 用户账号（手机号
     * @return boolean
     */
    @GetMapping("/vehicle/getFavouriteState")
    public Result getFavouriteState(@RequestParam @NotNull ObjectId id,
                                    @RequestParam @NotNull String phone){
        return ReturnResultUtil.success(vehicleService.getFavouriteState(id, phone));
    }

    /**
     * 根据载具id在阿里云OSS上获得所有相关图片的url
     *
     * @param id 载具id
     * @return {@link ArrayList}<{@link URL}>
     */
    @GetMapping("/vehicle/getImagesFromOSSById")
    public Result getImagesFromOSSById(@RequestParam @NotNull ObjectId id) {
        return ReturnResultUtil.success(vehicleService.getImagesFromOSSById(id));
    }

    /**
     * 根据载具id在阿里云OSS上获得第一张相关图片的url（获取封面）
     *
     * @param id 载具id
     * @return {@link URL}
     */
    @GetMapping("/vehicle/getFirstImageFromOSSById")
    public Result getFirstImageFromOSSById(@RequestParam @NotNull ObjectId id) {
        return ReturnResultUtil.success(vehicleService.getFirstImageFromOSSById(id));
    }

    /**
     * 得到筛选后的汽车
     *
     * @param conditions 根据条件数组获得汽车结果集
     * @param pageNumber 页码
     * @return {@link List}<{@link TempVehicleAd}>
     */
    @PostMapping("/vehicle/getFilterCars")
    @JsonFormat
    public Result getFilterCars(@RequestBody ArrayList<String> conditions,
                                @RequestParam @NotNull int pageNumber){
//        System.out.println("当前筛选条件数组为： " + conditions);
//        System.out.println("当前页码为： " + pageNumber);
        return ReturnResultUtil.success(vehicleService.getFilterCars(conditions, pageNumber));
    }

    /**
     * 得到筛选后的汽车
     *
     * @param conditions 根据条件数组获得汽车结果集
     * @param pageNumber 页码
     * @return {@link List}<{@link TempVehicleAd}>
     */
    @PostMapping("/vehicle/getFilterMotors")
    @JsonFormat
    public Result getFilterMotors(@RequestBody ArrayList<String> conditions,
                                @RequestParam @NotNull int pageNumber){
//        System.out.println("当前筛选条件数组为： " + conditions);
//        System.out.println("当前页码为： " + pageNumber);
        return ReturnResultUtil.success(vehicleService.getFilterMotors(conditions, pageNumber));
    }

    /**
     * 得到模糊搜索后匹配的所有汽车载具模型
     *
     * @param model 模型名称
     * @param pageNumber 页码
     * @return {@link List}<{@link Object}>
     */
    @GetMapping("/vehicle/getSearchCarModel")
    @JsonFormat
    public Result getSearchCarModel(@RequestParam String model,
                                    @RequestParam @NotNull int pageNumber){
        return ReturnResultUtil.success(vehicleService.getSearchCarModel(model, pageNumber));
    }

    /**
     * 得到模糊搜索后匹配的所有摩托车载具模型
     *
     * @param model 模型名称
     * @param pageNumber 页码
     * @return {@link List}<{@link Object}>
     */
    @GetMapping("/vehicle/getSearchMotorModel")
    @JsonFormat
    public Result getSearchMotorModel(@RequestParam String model,
                                    @RequestParam @NotNull int pageNumber){
        return ReturnResultUtil.success(vehicleService.getSearchMotorModel(model, pageNumber));
    }

    /**
     * 得到模糊搜索后匹配车商名称的所有汽车载具模型
     *
     * @param companyName 公司名称
     * @param pageNumber  页码
     * @return {@link List}<{@link Object}>
     */
    @GetMapping("/vehicle/getSearchCarCompanyName")
    @JsonFormat
    public Result getSearchCarCompanyName(@RequestParam String companyName,
                                          @RequestParam @NotNull int pageNumber){
        return ReturnResultUtil.success(vehicleService.getSearchCarCompanyName(companyName, pageNumber));
    }

    /**
     * 得到模糊搜索后匹配车商名称的所有摩托车载具模型
     *
     * @param companyName 公司名称
     * @param pageNumber  页码
     * @return {@link List}<{@link Object}>
     */
    @GetMapping("/vehicle/getSearchMotorCompanyName")
    @JsonFormat
    public Result getSearchMotorCompanyName(@RequestParam String companyName,
                                          @RequestParam @NotNull int pageNumber){
        return ReturnResultUtil.success(vehicleService.getSearchMotorCompanyName(companyName, pageNumber));
    }

}
