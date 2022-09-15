/*
 * @Description: 地区信息读取的监听器，根据EasyExcel实现
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2022-01-02 21:06:37
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-02 21:08:46
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/listener/AreaListener.java
 */
package com.easytrade.easytradeapi.listener;

import java.util.List;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ListUtils;
import com.easytrade.easytradeapi.model.Area;
import com.easytrade.easytradeapi.repository.AreaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;


@Slf4j
public class AreaListener implements ReadListener<Area> {
    @Autowired
    MongoTemplate mongoTemplate;
    // 每隔1000条存储到数据库
    private static final int BATCH_COUNT = 1000;
    private List<Area> cachedAreaDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);

    AreaRepository areaRepository;

    /**
     * @description: 构造函数，此listener不能被spring管理，所以需要传入持久层变量
     * @param {AreaRepository} areaRepository 传入的持久层
     * @return {*}
     */
    public AreaListener(AreaRepository areaRepository) {
        this.areaRepository = areaRepository;
    }

    /**
     * @description: 读取解析每一条数据
     * @param {Area} data 传入的地区类定义的变量
     * @param {AnalysisContext} content EasyExcel定义的变量
     * @return {*}
     */
    @Override
    public void invoke(Area data, AnalysisContext content) {
        cachedAreaDataList.add(data);
        // 当解析完一定数量的数据后，保存数据到数据库，并清空list
        if (cachedAreaDataList.size() >= BATCH_COUNT) {
            saveData();
            cachedAreaDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
        }
    }

    /**
     * @description: 所有数据解析完后调用
     * @param {AnalysisContext} context EasyExcel定义的变量
     * @return {*}
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 保存数据，确保最后的遗留数据存储到数据库
        saveData();
        log.info("All data analyse complete");
    }

    /**
     * @description: 存储数据到数据库
     * @param {*}
     * @return {*}
     */
    private void saveData() {
        log.info("{} data, start to save", cachedAreaDataList.size());
        areaRepository.saveAll(cachedAreaDataList);
        log.info("Save data complete");
    }
}
