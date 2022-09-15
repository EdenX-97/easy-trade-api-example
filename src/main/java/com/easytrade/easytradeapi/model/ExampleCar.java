/*
 * @Description: 汽车的模版类，用于储存所有汽车数据，被广告位引用来获取汽车参数详情
 * @Author: Mo Xu
 * @Date: 2021-12-16 18:38:57
 * @LastEditors: Mo Xu
 * @LastEditTime: 2022-05-09 17:50:24
 * @FilePath: /EasyTradeApi/EasyTradeApi/src/main/java/com/easytrade/easytradeapi/model/ExampleCar.java
 */
package com.easytrade.easytradeapi.model;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;
import lombok.Data;
import lombok.ToString;


@Document(collection = "exampleCars")
@Data
@ToString
public class ExampleCar {
    @MongoId
    private ObjectId id;

    // 品牌首字母
    private String initial;

    // 品牌
    private String brand;

    // 车系
    private String series;

    // 车型名称
    @Indexed(name = "model", unique = true)
    private String model;

    // 厂商
    private String company;

    // 年款
    private String year;

    // 上市时间
    private String timeToMarket;

    // 级别
    private String level;

    // 能源类型
    private String energy;

    // 最大功率 (kW)
    private String maximumPower;

    // 最大扭矩 (N·m)
    private String maximumTorque;

    // 发动机
    private String engine;

    // 电动机 (Ps)
    private String electricMotor;

    // 变速箱
    private String gearbox;

    // 车身结构
    private String structure;

    // 最高车速 (km/h)
    private String maximumSpeed;

    // 官方0-100km/h加速 (s)
    private String officialSpeedTest;

    // 工信部综合油耗 (L/100km)
    private String fuelConsumption;

    // 工信部续航里程 (km)
    private String mileage;

    // 整车质保
    private String vehicleWarranty;

    // 长度 (mm)
    private String length;

    // 宽度 (mm)
    private String width;

    // 高度 (mm)
    private String high;

    // 轴距 (mm)
    private String wheelbase;

    // 前轮距 (mm)
    private String frontTrack;

    // 后轮距 (mm)
    private String rearTrack;

    // 最小离地间隙 (mm)
    private String MinimumGroundClearance;

    // 车门数
    private String doors;

    // 座位数
    private String seats;

    // 油箱容积 (L)
    private String fuelTankCapacity;

    // 行李箱容积 (L)
    private String luggageColume;

    // 货箱尺寸 (mm)
    private String cargoBoxSize;

    // 最大载重质量 (kg)
    private String maximumLoadMass;

    // 整备质量 (kg)
    private String curbQuality;

    // 发动机型号
    private String engineModel;

    // 排量 (L)
    private String displacement;

    // 进气形式
    private String intake;

    // 气缸排列形式
    private String cylinderArrangement;

    // 气缸个数
    private String cylinders;

    // 每缸气门个数
    private String cylinderValves;

    // 压缩比
    private String compressionRatio;

    // 配气机构
    private String airSupply;

    // 缸径 (mm)
    private String bore;

    // 行程 (mm)
    private String stroke;

    // 最大马力 (Ps)
    private String maximumHorsepower;

    // 最大功率转速 (rpm)
    private String maximumPowerSpeed;

    // 最大扭矩转速 (rpm)
    private String maximumTorqueSpeed;

    // 发动机特有技术
    private String engineSpecificTechnology;

    // 燃油标号
    private String fuelLabel;

    // 供油方式
    private String fuelSupplyMethod;

    // 缸盖材料
    private String cylinderHeadMaterial;

    // 缸体材料
    private String cylinderBodyMaterial;

    // 环保标准
    private String environmentalStandards;

    // 电动机总功率 (kW)
    private String totalMotorPower;

    // 电动机总扭矩 (N·m)
    private String totalMotorTorque;

    // 前电机最大功率 (kW)
    private String frontMotorMaximumPower;

    // 前电机最大扭矩 (Ps)
    private String frontMotorMaximumTorque;

    // 后电机最大功率 (kW)
    private String rearMotorMaximumPower;

    // 后电机最大扭矩 (N·m)
    private String rearMotorMaximumTorque;

    // 系统综合功率 (kW)
    private String systemIntegratedPower;

    // 系统综合扭矩 (N·m)
    private String systemIntegratedTorque;

    // 工信部纯电续航里程 (km)
    private String pureElectricMileage;

    // 电池容量 (kWh)
    private String batteryCapacity;

    // 百公里耗电量 (kWh/100km)
    private String powerConsumption;

    // 快充时间 (h)
    private String fastChargeTime;

    // 慢充时间 (h)
    private String slowChargeTime;

    // 快充百分比 (%)
    private String fastChargePercentage;

    // 电池充电时间 (h)
    private String chargeTime;

    // 电机类型
    private String motorType;

    // 驱动电机数
    private String drivingMotors;

    // 电机布局
    private String motorLayout;

    // 电池类型
    private String batteryType;

    // 电池组质保
    private String batteryWarranty;

    // 电池能量 (kWh)
    private String batteryEnergy;

    // 电池预加热
    private String batteryPreheating;

    // 档位个数
    private String gears;

    // 变速箱类型
    private String gearboxType;

    // 驱动方式
    private String driveMethod;

    // 中央差速器结构
    private String centralDifferentialStructure;

    // 前悬架类型
    private String frontSuspensionType;

    // 后悬架类型
    private String rearSuspensionType;

    // 助力类型
    private String boostType;

    // 车体结构
    private String bodyStructure;

    // 前制动器类型
    private String frontBrakeType;

    // 后制动器类型
    private String rearBrakeType;

    // 驻车制动器类型
    private String parkingBrakeType;

    // 四驱形式
    private String fourWheelDriveType;

    // 前轮胎规格
    private String frontTireSpecification;

    // 后轮胎规格
    private String raerTireSpecification;

    // 备胎规格
    private String spareTireSpecification;

    // 主/副驾驶座安全气囊
    private String mainPassengerSeatAirbag;

    // 前/后排侧气囊
    private String frontRearSideAirbag;

    // 前/后排头部气囊（气帘）
    private String frontRearHeadAirbag;

    // 副驾驶坐垫式气囊
    private String coPilotSeatAirbag;

    // 后排安全带式气囊
    private String RearSeatBeltAirbag;

    // 后排座椅防下滑气囊
    private String RearSeatAntiSlidingAirbag;

    // 膝部气囊
    private String kneeAirbag;

    // 后排中央安全气囊
    private String RearCentralAirbag;

    // 安全带未系提醒
    private String seatBeltNotFastenedReminder;

    // 胎压监测功能
    private String tirePressureMonitoring;

    // ISOFIX儿童座椅接口
    private String ISOFIXChildSeatInterface;

    // ABS防抱死
    private String ABSAntiLock;

    // 制动力分配(EBD/CBC等)
    private String brakingForceDistribution;

    // 刹车辅助(EBA/BAS/BA等)
    private String brakeAssist;

    // 牵引力控制(ASR/TCS/TRC等)
    private String tractionControl;

    // 车身稳定控制(ESC/ESP/DSC等)
    private String bodyStabilityControl;

    // 主动刹车/主动安全系统
    private String activeSafetySystem;

    // 疲劳驾驶提示
    private String tiredDrivingTips;

    // 前/后驻车雷达
    private String fronRearParkingRadar;

    // 倒车车侧预警系统
    private String reversingCarSideEarlyWarningSystem;

    // 驾驶辅助影像
    private String drivingAssistanceImage;

    // 巡航系统
    private String cruiseSystem;

    // 驾驶模式切换
    private String drivingModeSwitch;

    // 自动泊车入位
    private String autoParking;

    // 发动机启停技术
    private String engineStartStopTechnology;

    // 自动驻车
    private String autoHold;

    // 上坡辅助
    private String uphillAssist;

    // 陡坡缓降
    private String steepSlope;

    // 空气悬架
    private String airSuspension;

    // 电磁感应悬架
    private String electromagneticInductionSuspension;

    // 可变转向比
    private String variableSteeringRatio;

    // 中央差速器锁止功能
    private String centralDifferentialLock;

    // 整体主动转向系统
    private String overallActiveSteeringSystem;

    // 限滑差速器/差速锁
    private String differentialLock;

    // 运动外观套件
    private String sportAppearanceKit;

    // 轮圈材质
    private String wheelMaterial;

    // 无钥匙启动系统
    private String keylessStart;

    // 无钥匙进入功能
    private String keylessEntry;

    // 方向盘材质
    private String steeringWheelMaterial;

    // 多功能方向盘
    private String multifunctionSteeringWheel;

    // 方向盘换挡
    private String steeringWheelShift;

    // 全液晶仪表盘
    private String fullLCDDashboard;

    // 液晶仪表尺寸
    private String LCDDashboardSize;

    // HUD抬头数字显示
    private String HUDHeadupDigitalDisplay;

    // 座椅材质
    private String seatMaterial;

    // 运动风格座椅
    private String sportStyleSeats;

    // 前排座椅功能
    private String frontSeatFunction;

    // 后排座椅功能
    private String rearSeatFunction;

    // 手机互联/映射
    private String mobilePhoneInterconnectionMapping;

    // 扬声器品牌名称
    private String speakerBrand;

    // 扬声器数量
    private String speakersNumber;

    // 近光灯光源
    private String lowBeamLightSource;

    // 远光灯光源
    private String highBeamLightSource;

    // 灯光特色功能
    private String lightingFeatures;

    // 外后视镜功能
    private String exteriorMirrorFunction;

    // 内后视镜功能
    private String insideRearviewMirrorFunction;

    // 空调温度控制方式
    private String airConditioningTemperatureControlMethod;

    // 后排独立空调
    private String independentRearAirConditioner;

    // 后座出风口
    private String rearSeatAirOutlet;

    // 温度分区控制
    private String temperatureZoneControl;

    // 车载空气净化器
    private String carAirPurifier;

    // 车内PM2.5过滤装置
    private String inCarPM25FilterDevice;

    // 外观颜色
    private String exteriorColor;

    // 内饰颜色
    private String interiorColor;

    // 中控彩色屏幕
    private String centralControlScreen;

    // 中控屏幕尺寸
    private String centralControlScreenSize;

    // 卫星导航系统
    private String satelliteNavigationSystem;
}
