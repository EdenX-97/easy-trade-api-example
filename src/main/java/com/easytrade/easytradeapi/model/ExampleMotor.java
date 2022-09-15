/**
 * @author: Hongzhang Liu
 * @description 摩托车模版类，存储所有摩托车数据
 * @date 29/6/2022 5:42 pm
 */
package com.easytrade.easytradeapi.model;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Document(collection = "exampleMotors")
@Data
@ToString
public class ExampleMotor{

    // 首字母
    private String initial;

    // 品牌
    private String brand;

    // 车系
    private String series;

    // 模型名称
    @Indexed(name = "model", unique = true)
    private String model;

    // 价格
    private String price;

    // 车型
    private String type;

    // 是否进口
    private String isImport;

    // 产地
    private String produceLocation;

    // 厂家
    private String manufacturer;

    // 引擎
    private String engine;

    // 最大马力（ps）
    private String maximumHorsepower;

    // 最大功率/转速（kW/rpm）
    private String maximumPowerRotationSpeed;

    // 最大扭矩/转速（Nm/rpm）
    private String maximumTorqueRotationSpeed;

    // 变速器
    private String gearbox;

    // ABS
    private String abs;

    // CBS
    private String cbs;

    // 长宽高（mm）
    private String lwh;

    // 座高（mm）
    private String seatHeight;

    // 整备质量（kg）
    private String curbWeight;

    // 油箱容量（L）
    private String fuelCapacity;

    // 最高车速（km/h）
    private String maximumSpeed;

    // 实测平均油耗（L/100km）
    private String averageFuelConsume;

    // 续航里程（km）
    private String lifeMile;

    // 整车质保
    private String warranty;

    // 可选颜色
    private String color;

    // 长
    private String length;

    // 宽
    private String width;

    // 高
    private String height;

    // 轴距
    private String wheelbase;

    // 最小离地间距
    private String minimumDistanceFromGround;

    // 车架型式
    private String frameType;

    // 后摇臂
    private String rearRockerArm;

    // 前悬挂系统
    private String frontSuspensionSystem;

    // 后悬挂系统
    private String rearSuspensionSystem;

    // 主油箱容量（L）
    private String majorFuelBoxCapacity;

    // 副油箱容量（L）
    private String viceFuelBoxCapacity;

    // 拖曳距离
    private String dragDistance;

    // 发动机型号
    private String engineModel;

    // 排量
    private String displacement;

    // 精确排量
    private String accurateDisplacement;

    // 进气形式
    private String intakeForm;

    // 发动机结构
    private String engineStructure;

    // 发动机气缸数
    private String cylindersNum;

    // 配气结构
    private String distributionStructure;

    // 缸径x行程（mm）
    private String diameterStroke;

    // 发动机冲程
    private String engineStroke;

    // 压缩比
    private String compressionRatio;

    // 冷却方式
    private String coldMethod;

    // 能源类型
    private String energy;

    // 燃油标号
    private String fuelLabel;

    // 供油方式
    private String offerFuelMethod;

    // 环保标准
    private String environmentalStandard;

    // 离合器
    private String clutch;

    // 传动方式
    private String transmissionWay;

    // 滑动离合器
    private String slidingClutch;

    // 前轮规格
    private String frontWheelSpecification;

    // 后轮规格
    private String rearWheelSpecification;

    // 轮胎形式
    private String tireForm;

    // 轮辋
    private String rim;

    // 前制动系统
    private String frontBrakingSystem;

    // 后制动系统
    private String rearBrakingSystem;

    // ABS关闭功能
    private String absCloseSystem;

    // 弯道ABS系统
    private String bendAbsSystem;

    // 胎压监测
    private String tirePressureDetection;

    // 牵引力控制系统
    private String tractionControlSystem;

    // 转向阻尼器
    private String steeringDamper;

    // 模式选择
    private String driveModelSelection;

    // 油门配置
    private String throttleConfig;

    // 快速换挡系统
    private String quickShiftSystem;

    // 电子悬挂
    private String electronicSuspension;

    // 定速巡航
    private String cruiseControl;

    // 液压离合器
    private String hydraulicClutch;

    // 发动机启停
    private String engineStartStop;

    // 脉冲点火系统
    private String pulseIgnitionSystem;

    // 仪表盘
    private String dashboard;

    // 手机连接功能
    private String mobileConnection;

    // 导航投屏功能
    private String navigationScreenCasting;

    // 档位显示
    private String gearDisplay;

    // USB充电口
    private String usb;

    // 无钥匙启动
    private String noKeyStart;

    // 电加热手把
    private String electricHeatingHandle;

    // 电加热座垫
    private String electricHeatingSeat;

    // 风挡
    private String windshield;

    // 前灯
    private String frontLight;

    // 后灯
    private String rearLight;

    // 转向灯
    private String rotationLight;

    // 辅助灯
    private String supportLight;

    // 危险警示灯，双闪
    private String dangerLight;

    // 自动大灯
    private String autoLight;

    // 选装包
    private String optionalPackage;

    // 上市时间
    private String timeToMarket;

    // 生产状态
    private String produceStatus;

    // 配置选项
    private String configOptions;

    // 官方平均油耗（L/100km）
    private String officialAverageFuelConsume;

    // 官方0～100km/h加速（s）
    private String officialAccelerate;

    // 干重（kg）
    private String dryWeight;

    // 前倾角度（度）
    private String leaningAngle;

    // 电机最大马力（Ps）
    private String electricMaximumHorsepower;

    // 电机最大功率/转速（kW/rpm）
    private String electricMaximumPowerRotationSpeed;

    // 电机最大扭矩/转速（Nm/rpm）
    private String electricMaximumTorqueRotationSpeed;

    // 电池规格
    private String batterySpecification;

    // 充电时间（h）
    private String chargeTime;

    // 快充时间（h）
    private String quickChargeTime;

    // 官方百公里电耗（kW*h/km）
    private String officialElectricConsume;

    // 实测百公里电耗（kW*h/km）
    private String testElectricConsume;

    // 电机型号
    private String electricEngineModel;

    // 控制器
    private String controller;

    // 电机能源类型
    private String electricEnergyType;

    // 电池类型
    private String batteryType;

    // 电芯类型
    private String batteryHeartType;

    // 电压
    private String voltage;

    // 电池容量（Ah）
    private String batteryCapacity;

    // 电池重量
    private String batteryWeight;

    // 标准充电电流（A）
    private String standardChargingCurrent;

    // 循环充电次数
    private String loopChargeTime;

    // 放电耐温范围（摄氏度）
    private String dischargeTemperatureRange;

    // 最大有效载荷
    private String maximumPayload;

    // 最小转弯半径（m）
    private String minimumTurningRadius;

    @MongoId
    @ExcelProperty("id")
    private ObjectId id;
}
