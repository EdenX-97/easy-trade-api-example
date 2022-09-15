/**
 * @author: Hongzhang Liu
 * @description 样本摩托车接口的具体实现类
 * @date 29/6/2022 7:17 pm
 */
package com.easytrade.easytradeapi.service.impl;

import com.alibaba.excel.EasyExcel;
import com.easytrade.easytradeapi.constant.enums.ResultCodeEnum;
import com.easytrade.easytradeapi.constant.exceptions.ExampleCarException;
import com.easytrade.easytradeapi.constant.exceptions.ExampleMotorException;
import com.easytrade.easytradeapi.listener.ExampleMotorDataListener;
import com.easytrade.easytradeapi.model.ExampleMotor;
import com.easytrade.easytradeapi.repository.ExampleMotorRepository;
import com.easytrade.easytradeapi.service.intf.ExampleMotorService;
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

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Service
public class ExampleMotorServiceImpl implements ExampleMotorService {
    @Autowired
    ExampleMotorRepository exampleMotorRepository;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    RedisUtil redisUtil;

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void uploadExampleMotorData(MultipartFile exampleMotorDataFile) throws IOException {
        System.out.println("使用easyExcel前");
        // 调用easyExcel来完成数据的导入
        EasyExcel.read(exampleMotorDataFile.getInputStream(), ExampleMotor.class,
                new ExampleMotorDataListener(exampleMotorRepository)).sheet().doRead();
        System.out.println("使用easyExcel后");
        // 同时删除redis中所有关于汽车模版信息的缓存
        Set<String> deleteKeys = redisUtil.scan("example:motors:*");
        redisUtil.del(deleteKeys);
    }

    @Override
    public ExampleMotor getExampleMotorById(ObjectId id) {
        return exampleMotorRepository.findOneById(id);
    }

    @Override
    public List<String> getAllBrands() {
        return mongoTemplate.findDistinct(new Query(), "brand", "exampleMotors", String.class);
    }

    @Override
    public List<String> getAllSeriesByBrand(String brand) {
        // 使用distinct功能来获取所需的所有车系
        Query query = new Query(Criteria.where("brand").is(brand));
        List<String> series =
                mongoTemplate.findDistinct(query, "series", "exampleMotors", String.class);

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
                mongoTemplate.findDistinct(query, "model", "exampleMotors", String.class);

        // 如果获得空列表，则说明该品牌下没有对应车型
        if (models.isEmpty()) {
            throw new ExampleCarException(ResultCodeEnum.NOT_FOUND, "Series do not exist");
        }

        return models;
    }

    @Override
    public ExampleMotor getMotorByModel(String model) {
        model = model.replaceAll("\"", "");
        // 使用repository来获取所需的汽车模版
        ExampleMotor exampleMotor = exampleMotorRepository.findOneByModel(model);
        // 如果没有获得对应汽车模版，说明该车型没有对应的汽车模版
        if (exampleMotor == null) {
            throw new ExampleMotorException(ResultCodeEnum.NOT_FOUND, "Model do not exist");
        }
        return exampleMotor;
    }
}
