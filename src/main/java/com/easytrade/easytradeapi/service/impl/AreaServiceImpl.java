/*
 * @Description: 地区服务层的实现
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2022-01-02 20:54:57
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-03 22:26:21
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/service/impl/AreaServiceImpl.java
 */
package com.easytrade.easytradeapi.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import com.alibaba.excel.EasyExcel;
import com.easytrade.easytradeapi.constant.enums.ResultCodeEnum;
import com.easytrade.easytradeapi.constant.exceptions.AreaException;
import com.easytrade.easytradeapi.listener.AreaListener;
import com.easytrade.easytradeapi.model.Area;
import com.easytrade.easytradeapi.repository.AreaRepository;
import com.easytrade.easytradeapi.service.intf.AreaService;
import com.easytrade.easytradeapi.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


@Service
public class AreaServiceImpl implements AreaService {
    @Autowired
    AreaRepository areaRepository;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    RedisUtil redisUtil;

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void uploadAreaData(MultipartFile areaDataFile) throws IOException {
        try{
            // 调用easyExcel来完成数据的导入
            EasyExcel.read(areaDataFile.getInputStream(), Area.class, new AreaListener(areaRepository))
                    .sheet().doRead();
        }catch (Exception e){
            e.printStackTrace();
        }

        // 同时删除redis中所有关于地区信息的缓存
        Set<String> deleteKeys = redisUtil.scan("area:*");
        redisUtil.del(deleteKeys);
    }

    @Override
    public List<String> getProvinces() {
        // 使用distinct功能来获取所需的所有省份
        return mongoTemplate.findDistinct(new Query(), "province", "areas", String.class);
    }

    @Override
    public List<String> getCities() {
        // 使用distinct功能来获取所需的所有市
        return mongoTemplate.findDistinct(new Query(), "city", "areas", String.class);
    }

    @Override
    public List<String> getCounties() {
        // 使用distinct功能来获取所需的所有县
        return mongoTemplate.findDistinct(new Query(), "county", "areas", String.class);
    }

    @Override
    public List<String> getTowns() {
        // 使用distinct功能来获取所需的所有镇
        return mongoTemplate.findDistinct(new Query(), "town", "areas", String.class);
    }

    @Override
    public List<String> getCitiesByProvince(String province) {
        // 使用distinct功能来获取所需的所有市
        Query query = new Query(Criteria.where("province").is(province));
        List<String> cities = mongoTemplate.findDistinct(query, "city", "areas", String.class);

        // 如果获得空列表，则说明该省份不存在
        if (cities.isEmpty()) {
            throw new AreaException(ResultCodeEnum.NOT_FOUND, "Province do not exist");
        }

        return cities;
    }

    @Override
    public List<String> getCountiesByCity(String province, String city) {
        // 使用distinct功能来获取所需的所有县
        Query query = new Query();
        query.addCriteria(Criteria.where("province").is(province));
        query.addCriteria(Criteria.where("city").is(city));
        List<String> counties = mongoTemplate.findDistinct(query, "county", "areas", String.class);

        // 如果获得空列表，则说明该市下没有对应县
        if (counties.isEmpty()) {
            throw new AreaException(ResultCodeEnum.NOT_FOUND, "City do not exist");
        }

        return counties;
    }

    @Override
    public List<String> getTownsByCounty(String province, String city, String county) {
        // 使用distinct功能来获取所需的所有镇
        Query query = new Query();
        query.addCriteria(Criteria.where("province").is(province));
        query.addCriteria(Criteria.where("city").is(city));
        query.addCriteria(Criteria.where("county").is(county));
        List<String> towns = mongoTemplate.findDistinct(query, "town", "areas", String.class);

        // 如果获得空列表，则说明该县下没有对应镇
        if (towns.isEmpty()) {
            throw new AreaException(ResultCodeEnum.NOT_FOUND, "County do not exist");
        }

        return towns;
    }

    @Override
    public Area getArea(String province, String city, String county, String town) {
        // 使用repository来获取所需的地区
        Area area = areaRepository.findOneByProvinceAndCityAndCountyAndTown(province, city, county, town);

        // 如果没有获得对应地区，说明该镇名没有对应的地区
        if (area == null) {
            throw new AreaException(ResultCodeEnum.NOT_FOUND, "Town do not exist");
        }

        return area;
    }

    @Override
    public void addArea(long code, String province, String city, String county, String town) {
        // 如果数据库中已经存在了相同的地区代码，抛出异常
        if(areaRepository.existsByCode(code)){
            throw new AreaException(ResultCodeEnum.FAILED, "alreay have this area in database");
        }
        if(code < 0){
            throw new AreaException(ResultCodeEnum.INVALID_PARAM, "code can not be negtive");
        }
        Area area = new Area();
        area.setCode(code);
        area.setProvince(province);
        area.setCity(city);
        area.setCounty(county);
        area.setTown(town);
        areaRepository.save(area);
    }

    @Override
    public void deleteAreaByCode(long code) {
        // 如果数据库中不存在输入的地区代码，抛出异常
        if(!areaRepository.existsByCode(code)){
            throw new AreaException(ResultCodeEnum.NOT_FOUND, "do not have this area in database");
        }
        areaRepository.deleteByCode(code);
    }

    @Override
    public void updateAreaByCode(long code, String province, String city, String county, String town) {
        // 如果数据库中不存在输入的地区代码，抛出异常
        if(!areaRepository.existsByCode(code)){
            throw new AreaException(ResultCodeEnum.NOT_FOUND, "do not have this area in database");
        }
        Area area = areaRepository.findOneByCode(code);
        area.setProvince(province);
        area.setCity(city);
        area.setCounty(county);
        area.setTown(town);
        areaRepository.save(area);
    }
}

