package com.easytrade.easytradeapi.service.impl;

import com.easytrade.easytradeapi.controller.AreaController;
import com.easytrade.easytradeapi.service.intf.AreaService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * 区域服务层测试
 *
 * @author Mo Xu
 * @date 2022/04/18
 */
@RunWith(SpringRunner.class)
public class AreaServiceTest {
    // 创建Mock，并将其注入服务层
    @Mock
    private MongoTemplate mongoTemplate;
    @InjectMocks
    private AreaServiceImpl areaServiceImpl;

    /**
     * 测试获取省份信息
     */
    @Test
    public void testGetProvinces() {
        // 设置Mock返回数据
        List<String> mockReturn = List.of("上海市");
        Mockito.doReturn(mockReturn)
                .when(mongoTemplate)
                .findDistinct(new Query(), "province", "areas", String.class);

        // 调用service并获取返回数据
        Object returnData = areaServiceImpl.getProvinces();

        // 检测数据是否一致
        Assert.assertEquals(mockReturn, returnData);
    }
}
