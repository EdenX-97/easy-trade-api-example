/*
 * @Description: 车辆模版服务层的实现
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2021-12-26 02:17:15
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-10 03:38:27
 * 
 * @FilePath:
 * /EasyBuyCar/src/main/java/com/jiandanmaiche/api/service/impl/ExampleCarServiceImpl.java
 */
package com.easytrade.easytradeapi.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import com.alibaba.excel.EasyExcel;
import com.easytrade.easytradeapi.constant.enums.ResultCodeEnum;
import com.easytrade.easytradeapi.constant.exceptions.ExampleCarException;
import com.easytrade.easytradeapi.listener.ExampleCarDataListener;
import com.easytrade.easytradeapi.model.ExampleCar;
import com.easytrade.easytradeapi.repository.ExampleCarRepository;
import com.easytrade.easytradeapi.service.intf.ExampleCarService;
import com.easytrade.easytradeapi.utils.RedisUtil;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


@Service
public class ExampleCarServiceImpl implements ExampleCarService {
    @Autowired
    ExampleCarRepository exampleCarRepository;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    RedisUtil redisUtil;

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void uploadExampleCarData(MultipartFile exampleCarDataFile) throws IOException {
        // 调用easyExcel来完成数据的导入
        EasyExcel.read(exampleCarDataFile.getInputStream(), ExampleCar.class,
                new ExampleCarDataListener(exampleCarRepository)).sheet().doRead();

        // 同时删除redis中所有关于汽车模版信息的缓存
        Set<String> deleteKeys = redisUtil.scan("example:car:*");
        redisUtil.del(deleteKeys);
    }

    @Override
    public List<String> getInitials() {
        // 使用distinct功能来获取所需的所有首字母
        return mongoTemplate.findDistinct(new Query(), "initial", "exampleCars", String.class);
    }

    @Override
    public List<String> getBrands() {
        // 使用distinct功能来获取所需的所有品牌
        return mongoTemplate.findDistinct(new Query(), "brand", "exampleCars", String.class);
    }

    @Override
    public List<String> getSeries() {
        // 使用distinct功能来获取所需的所有车系
        return mongoTemplate.findDistinct(new Query(), "series", "exampleCars", String.class);
    }

    @Override
    public List<String> getModels() {
        // 使用distinct功能来获取所需的所有车型
        return mongoTemplate.findDistinct(new Query(), "model", "exampleCars", String.class);
    }

    @Override
    public List<String> getBrandsByInitial(String initial) {
        // 首字母都设置为大写
        initial = initial.toUpperCase();

        // 使用distinct功能来获取所需的所有品牌
        Query query = new Query(Criteria.where("initial").is(initial));
        List<String> brands =
                mongoTemplate.findDistinct(query, "brand", "exampleCars", String.class);

        // 如果获得空列表，则说明该字母下没有对应品牌
        if (brands.isEmpty()) {
            throw new ExampleCarException(ResultCodeEnum.NOT_FOUND, "Initial do not exist");
        }

        return brands;
    }

    @Override
    public List<String> getSeriesByBrand(String brand) {
        // 使用distinct功能来获取所需的所有车系
        Query query = new Query(Criteria.where("brand").is(brand));
        List<String> series =
                mongoTemplate.findDistinct(query, "series", "exampleCars", String.class);

        // 如果获得空列表，则说明该品牌下没有对应车系
        if (series.isEmpty()) {
            throw new ExampleCarException(ResultCodeEnum.NOT_FOUND, "Brand do not exist");
        }

        return series;
    }

    @Override
    public List<String> getModelsBySeries(String series) {
        // 使用distinct功能来获取所需的所有车型
        Query query = new Query(Criteria.where("series").is(series));
        List<String> models =
                mongoTemplate.findDistinct(query, "model", "exampleCars", String.class);

        // 如果获得空列表，则说明该品牌下没有对应车型
        if (models.isEmpty()) {
            throw new ExampleCarException(ResultCodeEnum.NOT_FOUND, "Series do not exist");
        }

        return models;
    }

    @Override
    public ExampleCar getCarByModel(String model) {
        // 使用repository来获取所需的汽车模版
        ExampleCar exampleCar = exampleCarRepository.findOneByModel(model);

        // 如果没有获得对应汽车模版，说明该车型没有对应的汽车模版
        if (exampleCar == null) {
            throw new ExampleCarException(ResultCodeEnum.NOT_FOUND, "Model do not exist");
        }

        return exampleCar;
    }

    @Override
    public ExampleCar getExampleCarById(ObjectId id) {
        return exampleCarRepository.findOneById(id);
    }
}

