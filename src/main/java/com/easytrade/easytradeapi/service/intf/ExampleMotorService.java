/**
 * @author: Hongzhang Liu
 * @description 样本摩托车相关的需求接口
 * @date 29/6/2022 7:17 pm
 */
package com.easytrade.easytradeapi.service.intf;

import com.easytrade.easytradeapi.model.ExampleMotor;
import org.bson.types.ObjectId;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ExampleMotorService {
    /**
     * @description: 上传excel文件(.xlsx)并将车辆模版保存到mongodb数据库
     * @param {MultipartFile} exampleMotorDataFile 摩托车模版所有数据的excel文件
     * @return {*}
     */
    public void uploadExampleMotorData(MultipartFile exampleMotorDataFile) throws IOException;

    /**
     * 通过id获得对应的样本摩托车
     *
     * @param id 摩托车id
     */
    ExampleMotor getExampleMotorById(ObjectId id);

    /**
     * 得到所有摩托车品牌
     *
     * @return {@link List}<{@link String}>
     */
    List<String> getAllBrands();

    /**
     * 根据品牌得到所有车系
     *
     * @return {@link List}<{@link String}>
     */
    List<String> getAllSeriesByBrand(String brand);

    /**
     * 根据车系获得所有模型名
     *
     * @param series 系列
     * @return {@link List}<{@link String}>
     */
    List<String> getModelsBySeries(String series);

    /**
     * 通过模型获取样本摩托
     *
     * @param model 模型
     * @return {@link ExampleMotor}
     */
    ExampleMotor getMotorByModel(String model);
}
