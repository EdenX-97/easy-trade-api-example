/*
 * @Description: 地区服务层
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2022-01-02 20:54:31
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-03 22:25:56
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/service/intf/AreaService.java
 */
package com.easytrade.easytradeapi.service.intf;

import java.io.IOException;
import java.util.List;
import com.easytrade.easytradeapi.model.Area;
import org.springframework.web.multipart.MultipartFile;


public interface AreaService {
    /**
     * @description: 上传excel文件(.xlsx)并将地区信息保存到mongodb数据库
     * @param {MultipartFile} areaDataFile 地区信息所有数据的excel文件
     * @return {*}
     */
    public void uploadAreaData(MultipartFile areaDataFile) throws IOException;

    /**
     * @description: 获取所有省份
     * @param {*}
     * @return {List<String>} 省份数据
     */
    public List<String> getProvinces();

    /**
     * @description: 获取所有市
     * @param {*}
     * @return {List<String>} 市数据
     */
    public List<String> getCities();

    /**
     * @description: 获取所有县
     * @param {*}
     * @return {List<String>} 县数据
     */
    public List<String> getCounties();

    /**
     * @description: 获取所有镇
     * @param {*}
     * @return {List<String>} 镇数据
     */
    public List<String> getTowns();

    /**
     * @description: 根据省份来获取对应的市
     * @param {String} province 省名
     * @return {List<String>} 包含对应的市
     */
    public List<String> getCitiesByProvince(String province);

    /**
     * @description: 根据市来获取对应的县
     * @param {String} province 省
     * @param {String} city 市
     * @return {List<String>} 包含对应的县
     */
    public List<String> getCountiesByCity(String province, String city);

    /**
     * @description: 根据县来获取对应的镇
     * @param {String} province 省
     * @param {String} city 市
     * @param {String} county 县
     * @return {List<String>} 包含对应的镇
     */
    public List<String> getTownsByCounty(String province, String city, String county);

    /**
     * @description: 根据信息来获取对应的地区
     * @param {String} province 省
     * @param {String} city 市
     * @param {String} county 县
     * @param {String} town 镇
     * @return {Area} 对应的地区信息
     */
    public Area getArea(String province, String city, String county, String town);

    /**
     * @description: 添加对应的地区
     * @param {String} province 省
     * @param {String} city 市
     * @param {String} county 县
     * @param {String} town 镇
     */
    public void addArea(long code, String province, String city, String county, String town);

    /**
     * @description: 根据地区代码删除对应的地区
     * @param {long} code 地区代码
     */
    public void deleteAreaByCode(long code);

    /**
     * @description: 根据地区代码来修改对应地区的相关信息
     * @param {String} province 省
     * @param {String} city 市
     * @param {String} county 县
     * @param {String} town 镇
     */
    public void updateAreaByCode(long code, String province, String city, String county, String town);
}

