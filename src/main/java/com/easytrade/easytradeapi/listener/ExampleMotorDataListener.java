/*
 * @Description: 车辆模版读取的监听器，根据EasyExcel实现
 * 
 * @Author: Hongzhang Liu
 * 
 * @Date: 2022-6-29 19:25:30
 */
package com.easytrade.easytradeapi.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ListUtils;
import com.easytrade.easytradeapi.model.ExampleMotor;
import com.easytrade.easytradeapi.repository.ExampleMotorRepository;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class ExampleMotorDataListener implements ReadListener<ExampleMotor> {

    // 每隔100条存储数据，然后清理list ，方便内存回收
    private static final int BATCH_COUNT = 500;

    private List<ExampleMotor> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);

    private ExampleMotorRepository exampleMotorRepository;

    /**
     * 构造方法。每次创建Listener的时候需要把spring管理的类传进来
     *
     * @param exampleMotorRepository
     */
    public ExampleMotorDataListener(ExampleMotorRepository exampleMotorRepository) {
        this.exampleMotorRepository = exampleMotorRepository;
    }

    /**
     * 每一条数据解析都会来调用
     *
     * @param data    one row value. Is is same as {@link AnalysisContext#readRowHolder()}
     * @param context
     */
    @Override
    public void invoke(ExampleMotor data, AnalysisContext context) {
        String model = data.getModel();
        // 拼接车系, 车型，进气形式，最大马力和油箱容量，避免重复key
        String temp = data.getSeries() + "*" + data.getType() + "*" + data.getIntakeForm() + "*" + data.getMaximumHorsepower() + data.getFuelCapacity();
        if (temp.equals("-") || temp == null || temp.isBlank()) {
            temp = "";
        }
        data.setModel(model + " - " + temp);
        cachedDataList.add(data);
        // 达到BATCH_COUNT了，需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
        if (cachedDataList.size() >= BATCH_COUNT) {
            saveData();
            // 存储完成清理 list
            cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
        }
    }

    /**
     * 所有数据解析完成了 都会来调用此函数方法
     *
     * @param context
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 这里也要保存数据，确保最后遗留的数据也存储到数据库
        saveData();
        log.info("所有数据解析完成！");
    }

    private void saveData() {
        log.info("{}条数据，开始存储数据库！", cachedDataList.size());
        exampleMotorRepository.saveAll(cachedDataList);
        log.info("存储数据库成功！");
    }
}
