/*
 * @Description: 汽车模版控制器
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2021-12-26 02:28:33
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-05 17:44:54
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/controller/ExampleCarController.java
 */
package com.easytrade.easytradeapi.controller;

import java.io.IOException;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import com.easytrade.easytradeapi.constant.consists.Result;
import com.easytrade.easytradeapi.model.ExampleCar;
import com.easytrade.easytradeapi.service.intf.ExampleCarService;
import com.easytrade.easytradeapi.utils.ReturnResultUtil;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
@Validated
public class ExampleCarController {
    @Autowired
    ExampleCarService exampleCarService;

    /**
     * @description: 传excel文件(.xlsx)并将车辆模版保存到mongodb数据库
     * @param {MultipartFile} exampleCarDataFile 车辆模版所有数据的excel文件
     * @return {Result} 结果信息
     */
    @PostMapping("/example/car/upload")
    @Transactional(rollbackFor = IOException.class)
    public Result uploadExampleCarData(@NotNull MultipartFile exampleCarDataFile)
            throws IOException {
        exampleCarService.uploadExampleCarData(exampleCarDataFile);
        return ReturnResultUtil.success();
    }

    /**
     * @description: 获取数据库中所有的品牌首字母
     * @param {*}
     * @return {Result} 结果信息，包含所有首字母数据
     */
    @GetMapping("/example/car/getInitials")
    @Cacheable(cacheNames = "example:car:initials")
    public Result getInitials() {
        return ReturnResultUtil.success(exampleCarService.getInitials());
    }

    /**
     * @description: 获取所有品牌
     * @param {*}
     * @return {Result} 结果信息，包含所有品牌数据
     */
    @GetMapping("/example/car/getBrands")
    @Cacheable(cacheNames = "example:car:brands")
    public Result getBrands() {
        return ReturnResultUtil.success(exampleCarService.getBrands());
    }

    /**
     * @description: 获取所有车系
     * @param {*}
     * @return {Result} 结果信息，包含所有车系数据
     */
    @GetMapping("/example/car/getSeries")
    @Cacheable(cacheNames = "example:car:series")
    public Result getSeries() {
        return ReturnResultUtil.success(exampleCarService.getSeries());
    }

    /**
     * @description: 获取所有车型
     * @param {*}
     * @return {Result} 结果信息，包含所有车型数据
     */
    @GetMapping("/example/car/getModels")
    @Cacheable(cacheNames = "example:car:models")
    public Result getModels() {
        return ReturnResultUtil.success(exampleCarService.getModels());
    }

    /**
     * @description: 根据首字母来获取对应的品牌
     * @param {String} initial 首字母，通常为A-Z，建议大写
     * @return {Result} 结果信息，包含所对应的品牌
     */
    @GetMapping("/example/car/getBrandsByInitial")
    @Cacheable(cacheNames = "example:car:brands")
    public Result getBrandsByInitial(@RequestParam @NotNull @NotBlank String initial) {
        return ReturnResultUtil.success(exampleCarService.getBrandsByInitial(initial));
    }

    /**
     * @description: 根据品牌来获取对应的车系
     * @param {String} brand 品牌
     * @return {Result} 结果信息，包含所对应的车系
     */
    @GetMapping("/example/car/getSeriesByBrand")
    @Cacheable(cacheNames = "example:car:series")
    public Result getSeriesByBrand(@RequestParam @NotNull @NotBlank String brand) {
        return ReturnResultUtil.success(exampleCarService.getSeriesByBrand(brand));
    }

    /**
     * @description: 根据车系来获取对应的车型
     * @param {String} series 车系
     * @return {Result} 结果信息，包含所对应的车型
     */
    @GetMapping("/example/car/getModelsBySeries")
    @Cacheable(cacheNames = "example:car:models")
    public Result getModelsBySeries(@RequestParam @NotNull @NotBlank String series) {
        return ReturnResultUtil.success(exampleCarService.getModelsBySeries(series));
    }

    /**
     * @description: 根据车型来获取对应的模版车数据
     * @param {String} model 车型
     * @return {Result} 结果信息，包含所对应的车辆模版详情信息
     */
    @GetMapping("/example/car/getCarByModel")
    @Cacheable(cacheNames = "example:car:cars")
    public Result getCarByModel(@RequestParam @NotNull @NotBlank String model) {
        return ReturnResultUtil.success(exampleCarService.getCarByModel(model));
    }

    /**
     * 通过id找到一个样本车
     *
     * @param id id 样本车id
     * @return {@link ExampleCar}
     */
    @GetMapping("/example/car/getExampleCarById")
    public Result getExampleCarById(@RequestParam @NotNull ObjectId id) {
        return ReturnResultUtil.success(exampleCarService.getExampleCarById(id));
    }
}

