/*
 * @Description: 车辆模版读取的监听器，根据EasyExcel实现
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2021-12-25 21:08:35
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-02 21:06:44
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/listener/ExampleCarDataListener.java
 */
package com.easytrade.easytradeapi.listener;

import java.util.List;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ListUtils;
import com.easytrade.easytradeapi.model.ExampleCar;
import com.easytrade.easytradeapi.repository.ExampleCarRepository;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class ExampleCarDataListener implements ReadListener<ExampleCar> {
    // 每隔1000条存储到数据库
    private static final int BATCH_COUNT = 1000;
    private List<ExampleCar> cachedCarDataList =
            ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);

    ExampleCarRepository exampleCarRepository;

    /**
     * @description: 构造函数，此listener不能被spring管理，所以需要传入持久层变量
     * @param {ExampleCarRepository} exampleCarRepository 传入的持久层
     * @return {*}
     */
    public ExampleCarDataListener(ExampleCarRepository exampleCarRepository) {
        this.exampleCarRepository = exampleCarRepository;
    }

    /**
     * @description: 读取解析每一条数据
     * @param {ExampleCar} data 传入的车辆模版类定义的变量
     * @param {AnalysisContext} content EasyExcel定义的变量
     * @return {*}
     */
    @Override
    public void invoke(ExampleCar data, AnalysisContext content) {
        // 由于model作为唯一索引，而且车型名称有重复，所以将model+timeToMarket即车型名称+上市时间作为索引，替换原model参数
        String model = data.getModel();
        String timeToMarket = data.getTimeToMarket();
        String seats = data.getSeats();
        String brand = data.getBrand();
        if (timeToMarket.equals("-") || timeToMarket == null || timeToMarket.isBlank()) {
            timeToMarket = "";
        }
        data.setModel(model + " - " + timeToMarket + " * " + seats + " * " + brand);

        //// 跳过方法
        //if (!exampleCarRepository.existsExampleCarByModel(data.getModel())) {
        //    cachedCarDataList.add(data);
        //}

        cachedCarDataList.add(data);

        // 当解析完一定数量的数据后，保存数据到数据库，并清空list
        if (cachedCarDataList.size() >= BATCH_COUNT) {
            saveData();
            cachedCarDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
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
        log.info("{} data, start to save", cachedCarDataList.size());
        exampleCarRepository.saveAll(cachedCarDataList);
        log.info("Save data complete");
    }

}
