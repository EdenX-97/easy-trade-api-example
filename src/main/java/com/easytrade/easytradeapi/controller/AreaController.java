/*
 * @Description: 地区信息控制器
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2022-01-02 20:49:34
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-05 17:45:11
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/controller/AreaController.java
 */
package com.easytrade.easytradeapi.controller;

import java.io.IOException;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import com.easytrade.easytradeapi.constant.consists.Result;
import com.easytrade.easytradeapi.service.intf.AreaService;
import com.easytrade.easytradeapi.utils.ReturnResultUtil;
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
public class AreaController {
    @Autowired
    AreaService areaService;

    /**
     * @description: 上传excel文件(.xlsx)并将地区信息保存到mongodb数据库
     * @param {MultipartFile} areaDataFile 地区信息所有数据的excel文件
     * @return {Result} 结果信息
     */
    @PostMapping("/area/upload")
    @Transactional(rollbackFor = IOException.class)
    public Result uploadAreaData(@NotNull MultipartFile areaDataFile) throws IOException {
        areaService.uploadAreaData(areaDataFile);
        return ReturnResultUtil.success();
    }

    /**
     * @description: 获取所有省份
     * @param {*}
     * @return {Result} 结果信息，包含省份数据
     */
    @GetMapping("/area/getProvinces")
    @Cacheable(cacheNames = "area:provinces")
    public Result getProvinces() {
        return ReturnResultUtil.success(areaService.getProvinces());
    }

    /**
     * @description: 获取所有市
     * @param {*}
     * @return {Result} 结果信息，包含市数据
     */
    @GetMapping("/area/getCities")
    @Cacheable(cacheNames = "area:cities")
    public Result getCities() {
        return ReturnResultUtil.success(areaService.getCities());
    }

    /**
     * @description: 获取所有县
     * @param {*}
     * @return {Result} 结果信息，包含所有县
     */
    @GetMapping("/area/getCounties")
    @Cacheable(cacheNames = "area:counties")
    public Result getCounties() {
        return ReturnResultUtil.success(areaService.getCounties());
    }

    /**
     * @description: 获取所有镇
     * @param {*}
     * @return {Result} 结果信息，包含所有镇
     */
    @GetMapping("/area/getTowns")
    @Cacheable(cacheNames = "area:towns")
    public Result getTowns() {
        return ReturnResultUtil.success(areaService.getTowns());
    }

    /**
     * @description: 根据省份来获取对应的市
     * @param {String} province 省名
     * @return {Result} 结果信息，包含对应的市
     */
    @GetMapping("/area/getCitiesByProvince")
    @Cacheable(cacheNames = "area:cities")
    public Result getCitiesByProvince(@RequestParam @NotNull @NotBlank String province) {
        return ReturnResultUtil.success(areaService.getCitiesByProvince(province));
    }

    /**
     * @description: 根据市来获取对应的县
     * @param {String} city 市名
     * @return {Result} 结果信息，包含对应的县
     */
    @GetMapping("/area/getCountiesByCity")
    @Cacheable(cacheNames = "area:counties")
    public Result getCountiesByCity(@RequestParam @NotNull @NotBlank String province,
            @RequestParam @NotNull @NotBlank String city) {
        return ReturnResultUtil.success(areaService.getCountiesByCity(province, city));
    }

    /**
     * @description: 根据县来获取对应的镇
     * @param {String} county 县名
     * @return {Result} 结果信息，包含对应的镇
     */
    @GetMapping("/area/getTownsByCounty")
    @Cacheable(cacheNames = "area:towns")
    public Result getTownsByCounty(@RequestParam @NotNull @NotBlank String province,
            @RequestParam @NotNull @NotBlank String city,
            @RequestParam @NotNull @NotBlank String county) {
        return ReturnResultUtil.success(areaService.getTownsByCounty(province, city, county));
    }

    /**
     * @description: 根据镇来获取对应的地区信息
     * @param {String} town 镇名
     * @return {Result} 结果信息，包含对应的地区信息
     */
    @GetMapping("/area/getArea")
    @Cacheable(cacheNames = "area:areas")
    public Result getArea(@RequestParam @NotNull @NotBlank String province,
            @RequestParam @NotNull @NotBlank String city,
            @RequestParam @NotNull @NotBlank String county,
            @RequestParam @NotNull @NotBlank String town) {
        return ReturnResultUtil.success(areaService.getArea(province, city, county, town));
    }

    /**
     * @description: 添加对应的地区
     * @param {String} province 省
     * @param {String} city 市
     * @param {String} county 县
     * @param {String} town 镇
     */
    @PostMapping("/area/addArea")
    public Result addArea(@RequestParam @NotNull long code,
                          @RequestParam @NotNull @NotBlank String province,
                          @RequestParam @NotNull @NotBlank String city,
                          @RequestParam @NotNull @NotBlank String county,
                          @RequestParam @NotNull @NotBlank String town) {
        areaService.addArea(code, province, city, county, town);
        return ReturnResultUtil.success("add area successfully");
    }

    /**
     * @description: 根据地区代码删除对应的地区
     * @param {long} code 地区代码
     */
    @PostMapping("/area/deleteAreaByCode")
    public Result deleteAreaByCode(@RequestParam @NotNull long code) {
        areaService.deleteAreaByCode(code);
        return ReturnResultUtil.success("delete area successfully");
    }

    /**
     * @description: 根据地区代码来修改对应地区的相关信息
     * @param {String} province 省
     * @param {String} city 市
     * @param {String} county 县
     * @param {String} town 镇
     */
    @PostMapping("/area/updateAreaByCode")
    public Result updateAreaByCode(@RequestParam @NotNull long code,
                                   @RequestParam @NotNull @NotBlank String province,
                                   @RequestParam @NotNull @NotBlank String city,
                                   @RequestParam @NotNull @NotBlank String county,
                                   @RequestParam @NotNull @NotBlank String town) {
        areaService.updateAreaByCode(code, province, city, county, town);
        return ReturnResultUtil.success("update area successfully");
    }
}
