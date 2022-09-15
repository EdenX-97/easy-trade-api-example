/**
 * @author: Hongzhang Liu
 * @description 将objectid序列化后供前端使用
 * @date 7/5/2022 6:26 pm
 */
package com.easytrade.easytradeapi.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
public class ObjectIdToStringConfig implements InitializingBean {

    @Resource
    private ObjectMapper objectMapper;

    @Override
    public void afterPropertiesSet() throws Exception {
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(ObjectId.class, ToStringSerializer.instance);
        objectMapper.registerModule(simpleModule);
    }
}
