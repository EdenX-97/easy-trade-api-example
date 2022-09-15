/**
 * @author: Hongzhang Liu
 * @description 样本车辆控制器
 * @date 29/6/2022 7:15 pm
 */
package com.easytrade.easytradeapi.controller;

import com.easytrade.easytradeapi.constant.consists.Result;
import com.easytrade.easytradeapi.model.ExampleMotor;
import com.easytrade.easytradeapi.repository.ExampleMotorRepository;
import com.easytrade.easytradeapi.service.intf.ExampleMotorService;
import com.easytrade.easytradeapi.utils.ReturnResultUtil;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;

@RestController
@Validated
public class ExampleMotorController {

    @Autowired
    ExampleMotorService exampleMotorService;

    @Autowired
    ExampleMotorRepository exampleMotorRepository;

    /**
     * @description: 传excel文件(.xlsx)并将车辆模版保存到mongodb数据库
     * @param {MultipartFile} exampleCarDataFile 车辆模版所有数据的excel文件
     * @return {Result} 结果信息
     */
    @PostMapping("/example/motor/upload")
    @Transactional(rollbackFor = IOException.class)
    public Result uploadExampleMotorData(@NotNull MultipartFile exampleMotorDataFile)
            throws IOException {
        exampleMotorService.uploadExampleMotorData(exampleMotorDataFile);
        return ReturnResultUtil.success();
    }

    /**
     * 通过id获得对应的样本摩托车
     *
     * @param id 摩托车id
     */
    @GetMapping("/example/motor/getExampleMotorById")
    public Result getExampleMotorById(@RequestParam @NotNull ObjectId id) {
        return ReturnResultUtil.success(exampleMotorService.getExampleMotorById(id));
    }

    /**
     * 得到所有摩托车品牌
     *
     * @return {@link List}<{@link String}>
     */
    @GetMapping("/example/motor/getAllBrands")
    public Result getAllBrands() {
        return ReturnResultUtil.success(exampleMotorService.getAllBrands());
    }

    /**
     * 根据品牌得到所有车系
     *
     * @return {@link List}<{@link String}>
     */
    @GetMapping("/example/motor/getAllSeriesByBrand")
    public Result getAllSeriesByBrand(@RequestParam @NotNull String brand) {
        return ReturnResultUtil.success(exampleMotorService.getAllSeriesByBrand(brand));
    }

    /**
     * 根据车系获得所有模型名
     *
     * @param series 系列
     * @return {@link List}<{@link String}>
     */
    @GetMapping("/example/motor/getModelsBySeries")
    public Result getModelsBySeries(@RequestParam @NotNull String series) {
        return ReturnResultUtil.success(exampleMotorService.getModelsBySeries(series));
    }

    /**
     * 通过模型获取样本摩托
     *
     * @param model 模型
     * @return {@link ExampleMotor}
     */
    @GetMapping("/example/motor/getMotorByModel")
    public Result getMotorByModel(@RequestParam @NotNull String model) {
        return ReturnResultUtil.success(exampleMotorService.getMotorByModel(model));
    }
}
