//package com.easytrade.easytradeapi.model;
//
//import lombok.Data;
//import lombok.ToString;
//import org.bson.types.ObjectId;
//import org.springframework.data.mongodb.core.index.Indexed;
//import org.springframework.data.mongodb.core.mapping.Document;
//import org.springframework.data.mongodb.core.mapping.MongoId;
//
//@Document(collection = "exampleTrucks")
//@Data
//@ToString
//public class ExampleTruck {
//    @MongoId
//    private ObjectId id;
//
//    // 品牌
//    private String brand;
//
//    // 车系
//    private String series;
//
//    // 车型名称
//    @Indexed(name = "model", unique = true)
//    private String model;
//
//    // 分类名
//    private String type;
//
//    // 驱动形式
//    private String driveForm;
//
//    // 轴距
//    private String wheelbase;
//
//    // 发动机
//    private String engine;
//
//    // 变速箱
//    private String gearbox;
//
//    // 长度
//    private String length;
//
//    // 宽度
//    private String width;
//
//    // 高度
//    private String height;
//
//    // 轮距
//    private String track;
//
//    // 整车重量
//    private String vehicleWeight;
//
//    // 总质量
//    private String totalMass;
//
//    // 牵引总质量
//    private String totalTractionMass;
//
//    // 最高车速
//    private String topSpeed;
//
//    // 产地
//    private String origin;
//
//    // 吨位级别
//    private String ;
//
//    // 发动机品牌
//    private String ;
//
//    // 汽缸数
//    private String ;
//
//    // 燃料种类
//    private String ;
//
//    // 汽缸排列形式
//    private String ;
//
//    // 排量
//    private String ;
//
//    // 排放标准
//    private String ;
//
//    // 最大马力
//    private String ;
//
//    // 最大输出功率
//    private String ;
//
//    // 驾驶室
//    private String ;
//
//    // 准乘人数
//    private String ;
//
//    // 座位排数
//    private String ;
//
//    // 变速箱品牌
//    private String ;
//
//    // 换挡方式
//    private String ;
//
//    // 前进挡位
//    private String ;
//
//    // 倒挡数
//    private String ;
//
//    // 后桥允许载荷
//    private String ;
//
//    // 前桥允许载荷
//    private String ;
//
//    // 弹簧片数
//    private String ;
//
//    // 鞍座
//    private String ;
//
//    // ABS防抱死
//    private String ;
//
//    // 空调调节形式
//    private String ;
//
//    // 电动车窗
//    private String ;
//
//    // 外接音源接口(AUX/USB/iPod等)
//    private String ;
//
//    // 收音机
//    private String ;
//
//    // 大灯高度可调
//    private String ;
//
//    // 整车制动类型
//    private String ;
//
//    // 驻车制动
//    private String ;
//
//    // 遥控钥匙
//    private String ;
//
//    // 箱长级别
//    private String ;
//
//    // 后桥速比
//    private String ;
//
//    // 接近角/离去角
//    private String ;
//
//    // 细分市场
//    private String ;
//
//    // 备注
//    private String ;
//
//    // 前悬/后悬
//    private String ;
//
//    // 发动机俗称
//    private String ;
//
//    // 扭矩
//    private String ;
//
//    // 最大扭矩转速
//    private String ;
//
//    // 发动机形式
//    private String ;
//
//    // 货箱形式
//    private String ;
//
//    // 货箱长度
//    private String ;
//
//    // 货箱宽度
//    private String ;
//
//    // 驾驶室举升
//    private String ;
//
//    // 驾驶室宽度
//    private String ;
//
//    // 轮胎规格
//    private String ;
//
//    // 轮胎数
//    private String ;
//
//    // 油箱材质
//    private String ;
//
//    // 油箱容量
//    private String ;
//
//    // 后桥描述
//    private String ;
//
//    // 导流罩
//    private String ;
//
//    // 独立暖风
//    private String ;
//
//    // 定速巡航
//    private String ;
//
//    // 其它专用说明
//    private String ;
//
//    // 货箱尺寸
//    private String ;
//
//    // 接近角
//    private String ;
//
//    // 离去角
//    private String ;
//
//    // 货箱高度
//    private String ;
//
//    // 百公里等速油耗
//    private String ;
//
//    // 额定载重
//    private String ;
//
//    // 额定转速
//    private String ;
//
//    // 助力形式
//    private String ;
//
//    // 换挡形式
//    private String ;
//
//    // 前制动器类型
//    private String ;
//
//    // 后制动器类型
//    private String ;
//
//    // 前轮规格
//    private String ;
//
//    // 后轮规格
//    private String ;
//
//    // 主驾安全气囊
//    private String ;
//
//    // 副驾安全气囊
//    private String ;
//
//    // 前排侧气囊
//    private String ;
//
//    // 后排侧气囊
//    private String ;
//
//    // 膝部气囊
//    private String ;
//
//    // 安全带未系提示
//    private String ;
//
//    // 防盗报警器
//    private String ;
//
//    // 车内中控锁
//    private String ;
//
//    // 胎压监测
//    private String ;
//
//    // 牵引力控制(ASR/TCS/TRC等)
//    private String ;
//
//    // 制动力分配(EBD/CBC等)
//    private String ;
//
//    // 车身稳定控制(ESP/DSC/VSC等)
//    private String ;
//
//    // 轮间差速锁
//    private String ;
//
//    // 电动天窗
//    private String ;
//
//    // 铝合金车轮
//    private String ;
//
//    // 方向盘调节
//    private String ;
//
//    // 多功能方向盘
//    private String ;
//
//    // 座椅材质
//    private String ;
//
//    // 座椅高低调节
//    private String ;
//
//    // 座椅加热
//    private String ;
//
//    // 后视镜加热
//    private String ;
//
//    // 后视镜电动调节
//    private String ;
//
//    // GPS/北斗行车记录仪
//    private String ;
//
//    // 中控台彩色大屏
//    private String ;
//
//    // 蓝牙/车载电话
//    private String ;
//
//    // 倒车影像/倒车雷达
//    private String ;
//
//    // 氙气大灯
//    private String ;
//
//    // 前雾灯
//    private String ;
//
//    // 日间行车灯
//    private String ;
//
//    // 最小转弯直径
//    private String ;
//
//    // 最小离地间隙
//    private String ;
//
//    // 油箱容积
//    private String ;
//
//    // 上市年份
//    private String ;
//
//    // 尾门形式
//    private String ;
//
//    // 前悬类型
//    private String ;
//
//    // 助力类型
//    private String ;
//
//    // 车体结构
//    private String ;
//
//    // 悬架类型(前/后)
//    private String ;
//
//    // 制动形式
//    private String ;
//
//    // 品牌车系
//    private String ;
//
//    // 厂标续航
//    private String ;
//
//    // 电机品牌
//    private String ;
//
//    // 电机型号
//    private String ;
//
//    // 电机型式
//    private String ;
//
//    // 额定功率
//    private String ;
//
//    // 峰值功率
//    private String ;
//
//    // 电机额定扭矩
//    private String ;
//
//    // 峰值扭矩
//    private String ;
//
//    // 电池品牌
//    private String ;
//
//    // 电池型号
//    private String ;
//
//    // 电池类型
//    private String ;
//
//    // 电池容量
//    private String ;
//
//    // 箱体容积
//    private String ;
//
//    // 整车产地
//    private String ;
//
//    // 上装品牌
//    private String ;
//
//    // 整车类型
//    private String ;
//
//    // 整车公告
//    private String ;
//
//    // 喷油系统
//    private String ;
//
//    // 底盘品牌
//    private String ;
//
//    // 底盘型号
//    private String ;
//
//    // 车架高度
//    private String ;
//
//    // 前桥描述
//    private String ;
//
//    // 前轮距
//    private String ;
//
//    // 后轮距
//    private String ;
//
//    // 离合器
//    private String ;
//
//    // 转向机
//    private String ;
//
//    // 车桥数量
//    private String ;
//
//    // 罐体外形(长×宽×高)
//    private String ;
//
//    // 其它
//    private String ;
//
//    // 设计载重
//    private String ;
//
//    // 车桥品牌
//    private String ;
//
//    // 车桥载荷
//    private String ;
//
//    // 车轮品牌
//    private String ;
//
//    // 车轮数量
//    private String ;
//
//    // 轮胎品牌
//    private String ;
//
//    // 轮胎数量
//    private String ;
//
//    // 悬挂
//    private String ;
//
//    // 车轮材质
//    private String ;
//
//    // 大梁材质
//    private String ;
//
//    // 纵梁高度
//    private String ;
//
//    // 上翼板
//    private String ;
//
//    // 下翼板
//    private String ;
//
//    // 腹板
//    private String ;
//
//    // 边梁
//    private String ;
//
//    // 箱板
//    private String ;
//
//    // 开数
//    private String ;
//
//    // 牵引销
//    private String ;
//
//    // 支腿
//    private String ;
//
//    // 工具箱
//    private String ;
//
//    // 备胎架
//    private String ;
//
//    // 制动器室
//    private String ;
//
//    // ABS
//    private String ;
//
//    // 订做周期
//    private String ;
//
//    // 保修时间
//    private String ;
//
//    // 保修方式
//    private String ;
//
//    // 底盘车系
//    private String ;
//
//    // 搅拌动容积
//    private String ;
//
//    // 罐体容积
//    private String ;
//
//    // 罐体材质
//    private String ;
//
//    // 罐体仓数
//    private String ;
//
//    // 罐体壁厚
//    private String ;
//
//    // 车厢最大举升时间
//    private String ;
//
//    // 最小转弯半径
//    private String ;
//
//    // 最大爬坡度
//    private String ;
//
//    // 最高行驶速度
//    private String ;
//
//    // 额定载重量
//    private String ;
//
//    // 技术路线
//    private String ;
//
//    // 下卧铺宽
//    private String ;
//
//    // 油箱/气罐材质
//    private String ;
//
//    // 油箱/气罐容量
//    private String ;
//
//    // 悬挂形式
//    private String ;
//
//    // 系别
//    private String ;
//
//    // 电源接口
//    private String ;
//
//    // 铝合金储气罐
//    private String ;
//
//    // 壳体材质
//    private String ;
//
//    // 驾驶室悬挂
//    private String ;
//
//    // 主驾座椅形式
//    private String ;
//
//    // 版本
//    private String ;
//
//    // 类型
//    private String ;
//
//    // 离合助力
//    private String ;
//
//    // 转向助力
//    private String ;
//
//    // 电动后视镜
//    private String ;
//
//    // 后视镜电加热
//    private String ;
//
//    // 电子中控锁
//    private String ;
//
//    // 前轮制动器
//    private String ;
//
//    // 后轮制动器
//    private String ;
//
//    // 前轮距/后轮距
//    private String ;
//
//    // 离去角/接近角
//    private String ;
//
//    // 最大扭矩
//    private String ;
//
//    // 挡位数
//    private String ;
//
//    // 底盘结构
//    private String ;
//
//    // 前桥悬挂形式
//    private String ;
//
//    // 后桥悬挂形式
//    private String ;
//
//    // 长度
//    private String ;
//
//    // 宽度
//    private String ;
//
//    // 高度
//    private String ;
//
//    // 座位数
//    private String ;
//
//    // 后悬类型
//    private String ;
//
//    // 车门数量
//    private String ;
//
//    // 侧门形式
//    private String ;
//
//    // 刹车辅助(EBA/BAS/BA等)
//    private String ;
//
//    // 车载电视
//    private String ;
//
//    // 方向盘材质
//    private String ;
//
//    // CD/DVD
//    private String ;
//
//    // 前/后轮距
//    private String ;
//
//    // 百公里油耗
//    private String ;
//
//    // 能量密度
//    private String ;
//
//    // 电池额定电压
//    private String ;
//
//    // 电池总电压
//    private String ;
//
//    // 充电时间
//    private String ;
//
//    // 货厢容积
//    private String ;
//
//    // 充电方式
//    private String ;
//
//    // 缸数
//    private String ;
//
//    // 车身尺寸
//    private String ;
//
//    // 搅拌筒几何容积
//    private String ;
//
//    // 地方排放标准
//    private String ;
//
//    // 其他
//    private String ;
//
//    // 电控系统品牌
//    private String ;
//
//    // 车厢最大深度
//    private String ;
//
//    // 车厢最大宽度
//    private String ;
//
//    // 车厢高度
//    private String ;
//
//    // 车厢容积
//    private String ;
//
//    // 车身结构
//    private String ;
//
//    // 一体式挡泥板
//    private String ;
//
//    // 电机最大功率
//    private String ;
//
//    // 轮胎类型
//    private String ;
//
//    // 前桥轴荷
//    private String ;
//
//    // 后桥轴荷
//    private String ;
//
//    // 专用功能说明
//    private String ;
//
//    // 坍落度
//    private String ;
//
//    // 进料速度
//    private String ;
//
//    // 出料速度
//    private String ;
//
//    // 出料残余率
//    private String ;
//
//    // 供水方式
//    private String ;
//
//    // 水箱容积
//    private String ;
//
//    // 液压系统
//    private String ;
//
//    // 制冷机组
//    private String ;
//
//    // 温度
//    private String ;
//
//    // 上卧铺宽
//    private String ;
//
//    // 独立空调
//    private String ;
//
//    // ASR驱动防滑
//    private String ;
//
//    // 天窗
//    private String ;
//
//    // 挂车稳定系统
//    private String ;
//
//    // LED尾灯
//    private String ;
//
//    // 侧裙板
//    private String ;
//
//    // 胎压监测系统
//    private String ;
//
//    // 搅拌筒转速
//    private String ;
//
//    // 填充率
//    private String ;
//
//    // 轴数
//    private String ;
//
//    // 发动机功率
//    private String ;
//
//    // 泵送排量
//    private String ;
//
//    // 整车尺寸长×宽×高
//    private String ;
//
//    // 泵送排量(低压/高压)
//    private String ;
//
//    // 泵送压力(低压/高压)
//    private String ;
//
//    // 泵送次数(低压/高压)
//    private String ;
//
//    // 输送缸内径×行程
//    private String ;
//
//    // 臂架垂直高度
//    private String ;
//
//    // 臂架臂节数
//    private String ;
//
//    // 支腿展开宽度前/后
//    private String ;
//
//    // 最大泵送高度
//    private String ;
//
//    // 臂架水平长度
//    private String ;
//
//    // 臂架垂直深度
//    private String ;
//
//    // 转台旋转角度
//    private String ;
//
//    // 油压
//    private String ;
//
//    // 输送管径
//    private String ;
//
//    // 第一臂节-长度
//    private String ;
//
//    // 第一臂节-转角
//    private String ;
//
//    // 第二臂节-长度
//    private String ;
//
//    // 第二臂节-转角
//    private String ;
//
//    // 第三臂节-长度
//    private String ;
//
//    // 第三臂节-转角
//    private String ;
//
//    // 第四臂节-长度
//    private String ;
//
//    // 第四臂节-转角
//    private String ;
//
//    // 第五臂节-长度
//    private String ;
//
//    // 第五臂节-转角
//    private String ;
//
//    // 第六臂节-长度
//    private String ;
//
//    // 第六臂节-转角
//    private String ;
//
//    // 最小展开长度
//    private String ;
//
//    // 整机尺寸长×宽×高
//    private String ;
//
//    // 最大行驶速度
//    private String ;
//
//    // 额定起重力矩
//    private String ;
//
//    // 额定起重量
//    private String ;
//
//    // 额定起重力矩全伸臂
//    private String ;
//
//    // 最小工作幅度
//    private String ;
//
//    // 最大起升高度-基本臂
//    private String ;
//
//    // 最大起升高
//    private String ;
//
//    // 最大起升高度-基本臂+副臂
//    private String ;
//
//    // 起重臂截面
//    private String ;
//
//    // 起重臂长度-基本臂
//    private String ;
//
//    // 起重臂长度-全伸臂
//    private String ;
//
//    // 起重臂长度-基本臂+副臂
//    private String ;
//
//    // 副起重臂长度
//    private String ;
//
//    // 副起重臂安装角度
//    private String ;
//
//    // 主卷扬最大起升速度
//    private String ;
//
//    // 起重臂全伸时间
//    private String ;
//
//    // 起重臂全起/落时间
//    private String ;
//
//    // 回转速度
//    private String ;
//
//    // 工作重量
//    private String ;
//
//    // 支腿跨距纵向×横向
//    private String ;
//
//    // 功率
//    private String ;
//
//    // 堆装容量
//    private String ;
//
//    // 满载质量
//    private String ;
//
//    // 发动机缸数
//    private String ;
//
//    // 缸径×行程
//    private String ;
//
//    // 使用范围
//    private String ;
//
//    // 最大容量
//    private String ;
//
//    // 车型最大下降时间
//    private String ;
//
//    // 系统压力
//    private String ;
//
//    // 最高时速
//    private String ;
//
//    // 前/后桥轴荷
//    private String ;
//
//    // 轮距(前/后)
//    private String ;
//
//    // 最大爬坡能力
//    private String ;
//
//    // 最大扭矩/转速
//    private String ;
//
//    // 举升形式
//    private String ;
//
//    // 最大倾卸角
//    private String ;
//
//    // 货箱举升时间
//    private String ;
//
//    // 罐体尺寸
//    private String ;
//
//    // 介质密度
//    private String ;
//
//    // 运输介质
//    private String ;
//
//    // 额定质量
//    private String ;
//
//    // 罐载介质
//    private String ;
//
//    // 燃油种类
//    private String ;
//
//    // 倒挡位数
//    private String ;
//
//    // 整车质量
//    private String ;
//
//    // 托举质量
//    private String ;
//
//    // 其他专用说明
//    private String ;
//
//    // 清扫面积
//    private String ;
//
//    // 下防护尺寸
//    private String ;
//
//    // 离地高度
//    private String ;
//
//    // 前/后桥描述
//    private String ;
//
//    // 其他专用参数
//    private String ;
//
//    // 密度
//    private String ;
//
//    // 清扫宽度
//    private String ;
//
//    // 清扫速度
//    private String ;
//
//    // 适用范围
//    private String ;
//
//    // 工况
//    private String ;
//
//    // 最大作业高度
//    private String ;
//
//    // 前伸/后伸
//    private String ;
//
//    // 最大作业半径
//    private String ;
//
//    // 回转角度
//    private String ;
//
//    // 洒水宽度
//    private String ;
//
//    // 总配重
//    private String ;
//
//    // 副卷扬最大起升速度
//    private String ;
//
//    // 最大功率转速
//    private String ;
//
//    // 平装容量
//    private String ;
//
//    // 整备质量
//    private String ;
//
//    // 卸载角度
//    private String ;
//
//    // 油箱信息
//    private String ;
//
//    // 最大卸载高度
//    private String ;
//
//    // 起重机额定功率
//    private String ;
//
//    // 车厢举升时间
//    private String ;
//
//    // 自卸斗最小离地间隙
//    private String ;
//
//    // 车厢下降时间
//    private String ;
//
//    // 主泵最大流量
//    private String ;
//
//    // 外型尺寸
//    private String ;
//
//    // 燃油箱容量
//    private String ;
//
//    // 液压油箱
//    private String ;
//
//    // 发动机机油
//    private String ;
//
//    // 冷却液
//    private String ;
//
//    // 排气制动
//    private String ;
//
//    // 下防护材质
//    private String ;
//
//    // 载液量
//    private String ;
//
//    // 后伸
//    private String ;
//
//    // 撒布机有效容积
//    private String ;
//
//    // 作业平均速度
//    private String ;
//
//    // 撒布宽度
//    private String ;
//
//    // 撒布密度
//    private String ;
//
//    // 主驾安全带类型
//    private String ;
//
//    // 喷洒介质
//    private String ;
//
//    // 底板
//    private String ;
//
//    // 装运介质
//    private String ;
//
//    // 后防断面尺寸
//    private String ;
//
//    // 罐体直线段长度
//    private String ;
//
//    // 后防护断面尺寸
//    private String ;
//
//    // 座椅面料
//    private String ;
//
//    // 制动器类型
//    private String ;
//
//    // 自动间隙调整臂
//    private String ;
//
//    // ABS/品牌/几通道
//    private String ;
//
//    // 货箱底板厚度
//    private String ;
//
//    // 举升装置
//    private String ;
//
//    // 货箱边板厚度
//    private String ;
//
//    // 装载车位
//    private String ;
//
//    // 集装箱锁具
//    private String ;
//
//    // 空压机
//    private String ;
//
//    // 紧绳器
//    private String ;
//
//    // 桩形式
//    private String ;
//
//    // 升降（空气）悬挂
//    private String ;
//
//    // 保修政策
//    private String ;
//
//    // 插桩形式
//    private String ;
//
//    // 尾门宽度
//    private String ;
//
//    // 车厢最小深度
//    private String ;
//
//    // 车厢最小宽度
//    private String ;
//
//    // 尾门高度
//    private String ;
//
//    // 地板高度
//    private String ;
//
//    // 发动机型号
//    private String ;
//
//    // 变速箱型号
//    private String ;
//
//    // 速比
//    private String ;
//
//    // 液力缓速器
//    private String ;
//
//    // 车队管理系统
//    private String ;
//
//    // 发动机制动
//    private String ;
//
//    // 自适应巡航
//    private String ;
//
//    // 正向碰撞预警系统
//    private String ;
//
//    // 变道支持
//    private String ;
//
//    // 紧急制动辅助系统
//    private String ;
//
//    // 厢体容积
//    private String ;
//
//    // 起重臂仰角范围
//    private String ;
//
//    // 产品型号
//    private String ;
//
//    // 产品机型
//    private String ;
//
//    // 工作质量
//    private String ;
//
//    // 前轮静线载荷
//    private String ;
//
//    // 整机尺寸
//    private String ;
//
//    // 振动频率
//    private String ;
//
//    // 激振力
//    private String ;
//
//    // 振动轮直径
//    private String ;
//
//    // 高速行驶速度
//    private String ;
//
//    // 前轮分配质量
//    private String ;
//
//    // 后轮分配质量
//    private String ;
//
//    // 名义振幅
//    private String ;
//
//    // 工作宽度/振动轮宽度
//    private String ;
//
//    // 振动轮轮圈厚度
//    private String ;
//
//    // 转向角度
//    private String ;
//
//    // 摇摆角度
//    private String ;
//
//    // 最小转弯外半径
//    private String ;
//
//    // 低速行驶速度
//    private String ;
//
//    // 理论爬坡能力
//    private String ;
//
//    // 轮胎型号
//    private String ;
//
//    // 产品动力
//    private String ;
//
//    // 铲斗形式
//    private String ;
//
//    // 产品吨位
//    private String ;
//
//    // 整机工作重量
//    private String ;
//
//    // 铲斗容量
//    private String ;
//
//    // 履带板宽度
//    private String ;
//
//    // 履带轨距
//    private String ;
//
//    // 后端回转半径
//    private String ;
//
//    // 行走液压回路
//    private String ;
//
//    // 回转液压回路
//    private String ;
//
//    // 发动机油更换量
//    private String ;
//
//    // 行走速度
//    private String ;
//
//    // 铲斗挖掘力
//    private String ;
//
//    // 斗杆挖掘力
//    private String ;
//
//    // 最大挖掘半径
//    private String ;
//
//    // 最大挖掘深度
//    private String ;
//
//    // 最大挖掘高度
//    private String ;
//
//    // 最大垂直挖掘深度
//    private String ;
//
//    // 动臂长度
//    private String ;
//
//    // 斗杆长度
//    private String ;
//
//    // 配重离地间隙
//    private String ;
//
//    // 工作液压油路
//    private String ;
//
//    // 最大牵引力
//    private String ;
//
//    // 履带轴距
//    private String ;
//
//    // 推土铲刀容量
//    private String ;
//
//    // 推土铲宽度×高度
//    private String ;
//
//    // 最大提升高度
//    private String ;
//
//    // 爬坡能力
//    private String ;
//
//    // 最大下降高度
//    private String ;
//
//    // 整机操作重量
//    private String ;
//
//    // 卸载高度
//    private String ;
//
//    // 工作装置动作时间三项和
//    private String ;
//
//    // 液压油箱容量
//    private String ;
//
//    // 卸载距离
//    private String ;
//
//    // 操纵方式
//    private String ;
//
//    // 铲斗宽度
//    private String ;
//
//    // 倾翻载荷
//    private String ;
//
//    // 液压泵类型
//    private String ;
//
//    // 材质
//    private String ;
//
//    // 备胎
//    private String ;
//
//    // 驾驶室后窗
//    private String ;
//
//    // 电源
//    private String ;
//
//    // EBS电子制动系统
//    private String ;
//
//}
