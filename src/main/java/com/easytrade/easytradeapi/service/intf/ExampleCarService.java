/*
 * @Description: 车辆模版服务层
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2021-12-26 02:13:09
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-03 22:26:51
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/service/intf/ExampleCarService.java
 */
package com.easytrade.easytradeapi.service.intf;

import java.io.IOException;
import java.util.List;
import com.easytrade.easytradeapi.model.ExampleCar;
import org.bson.types.ObjectId;
import org.springframework.web.multipart.MultipartFile;


public interface ExampleCarService {
    /**
     * @description: 上传excel文件(.xlsx)并将车辆模版保存到mongodb数据库
     * @param {MultipartFile} exampleCarDataFile 车辆模版所有数据的excel文件
     * @return {*}
     */
    public void uploadExampleCarData(MultipartFile exampleCarDataFile) throws IOException;

    /**
     * @description: 获取数据库中所有的品牌首字母
     * @param {*}
     * @return {List<String>} 所有首字母数据
     */
    public List<String> getInitials();

    /**
     * @description: 获取所有品牌
     * @param {*}
     * @return {List<String>} 所有品牌数据
     */
    public List<String> getBrands();

    /**
     * @description: 获取所有车系
     * @param {*}
     * @return {List<String>} 所有车系数据
     */
    public List<String> getSeries();

    /**
     * @description: 获取所有车型
     * @param {*}
     * @return {List<String>} 所有车型数据
     */
    public List<String> getModels();

    /**
     * @description: 根据首字母来获取对应的品牌
     * @param {String} initial 首字母，通常为A-Z，建议大写
     * @return {List<String>} 所对应的品牌
     */
    public List<String> getBrandsByInitial(String initial);

    /**
     * @description: 根据品牌来获取对应的车系
     * @param {String} brand 品牌
     * @return {List<String>} 所对应的车系
     */
    public List<String> getSeriesByBrand(String brand);

    /**
     * @description: 根据车系来获取对应的车型
     * @param {String} series 车系
     * @return {List<String>} 对应的车型
     */
    public List<String> getModelsBySeries(String series);

    /**
     * @description: 根据车型来获取对应的车辆模版信息
     * @param {String} model 车型
     * @return {ExampleCar} 所对应的车辆模版详情信息
     */
    public ExampleCar getCarByModel(String model);

    /**
     * 通过id找到一个样本车
     *
     * @param id id 样本车id
     * @return {@link ExampleCar}
     */
    public ExampleCar getExampleCarById(ObjectId id);
}
