/**
 * @author: Hongzhang Liu
 * @description 微信支付服务实现类
 * @date 20/7/2022 1:46 pm
 */
package com.easytrade.easytradeapi.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.easytrade.easytradeapi.constant.consists.VehicleAd;
import com.easytrade.easytradeapi.constant.enums.*;
import com.easytrade.easytradeapi.constant.exceptions.PayException;
import com.easytrade.easytradeapi.constant.exceptions.VehicleException;
import com.easytrade.easytradeapi.listener.model.*;
import com.easytrade.easytradeapi.model.*;
import com.easytrade.easytradeapi.repository.*;
import com.easytrade.easytradeapi.service.intf.TradeRecordService;
import com.easytrade.easytradeapi.service.intf.UserService;
import com.easytrade.easytradeapi.service.intf.VehicleAdService;
import com.easytrade.easytradeapi.service.intf.WechatPaySerivce;
import com.easytrade.easytradeapi.utils.AesUtil;
import com.easytrade.easytradeapi.utils.AlipayUtil;
import com.easytrade.easytradeapi.utils.JWTUtil;
import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;
import com.wechat.pay.contrib.apache.httpclient.auth.AutoUpdateCertificatesVerifier;
import com.wechat.pay.contrib.apache.httpclient.auth.PrivateKeySigner;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Credentials;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Validator;
import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.util.Date;
import java.util.List;

@Service
public class WechatPayServiceImpl implements WechatPaySerivce{

    @Value("${wechat.appId}")
    private String appId;

    @Value("${wechat.privateKey}")
    private String privateKey;

    @Value("${wechat.mchId}")
    private String mchId;

    @Value("${wechat.apiV3Key}")
    private String apiV3Key;

    @Value("${wechat.mchSerialNo}")
    private String mchSerialNo;

    @Autowired
    VehicleAdService vehicleAdService;

    @Autowired
    TradeRecordService tradeRecordService;

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TradeRecordRepository tradeRecordRepository;

    @Autowired
    NewCarAdRepository newCarAdRepository;

    @Autowired
    SecondHandCarAdRepository secondHandCarAdRepository;

    @Autowired
    NewMotorAdRepository newMotorAdRepository;

    @Autowired
    SecondHandMotorAdRepository secondHandMotorAdRepository;

    @Override
    public String createOrder(ObjectId id, String token) throws Exception {

        // 免费广告标识位
        boolean isFree = false;

        // 检查并获取广告
        VehicleAd carAd = checkAndGetAd(id, token);

        // 获取支付用户信息
        User user = userService.getUserByToken(token, false);

        // 判断该广告是否处于创建阶段
        if (!carAd.getAdStatus().equals(AdStatusEnum.CREATED)) {
            throw new PayException(ResultCodeEnum.FAILED, "Cannot pay for ad not in created");
        }

        // 生成订单号和广告等级
        String outTradeNo = AlipayUtil.generateTradeNo();
        AdLevelEnum adLevel = carAd.getAdLevel();
        // 初始化价格
        BigDecimal price = BigDecimal.valueOf(0);

        // 判断该广告是否已经有支付方式为微信支付的订单
        List<TradeRecord> tradeRecords = tradeRecordRepository.findAllByAdIdAndType(id, TradeTypeEnum.WECHATPAY);
        System.out.println(tradeRecords);
        // 如果存在订单
        if (tradeRecords != null && !tradeRecords.isEmpty() ) {
            // 找到最新的订单
            TradeRecord tradeRecord = tradeRecordRepository.findAllByAdId(id).get(tradeRecordRepository.findAllByAdId(id).size() - 1);
            System.out.println(tradeRecord);
            System.out.println(tradeRecord.getStatus());
            // 当订单状态为正在支付则关闭订单，重新新建。
            if(tradeRecord.getStatus() == TradeStatusEnum.PAYING){
                // 根据订单号关闭订单（防止重复支付），并在数据库中设置订单状态为关闭
                outTradeNo = tradeRecord.getTradeNo();
                closeOrder(outTradeNo);
                tradeRecord.setStatus(TradeStatusEnum.CLOSED);
                tradeRecordRepository.save(tradeRecord);
                // 生成新的交易单号，继续建立新的订单
                outTradeNo = AlipayUtil.generateTradeNo();
                // 当订单状态为免费时(已完成)，无法继续支付
            }else if(tradeRecord.getStatus() == TradeStatusEnum.FREE){
                throw new PayException(ResultCodeEnum.FAILED, "The order is free, do not need to pay");
                // 当订单状态为已付费(已完成)，抛出异常，无法继续支付
            }else if(tradeRecord.getStatus() == TradeStatusEnum.PAID){
                throw new PayException(ResultCodeEnum.FAILED, "The order is paid already");
                // 订单状态为关闭时，视作没有订单进行处理， 即新建一个订单
            } else if(tradeRecord.getStatus() == TradeStatusEnum.CLOSED){
                System.out.println("this order already closed, continue to create a new order");
            }
        }

        // 创建临时记录对象并赋予基本属性
        TradeRecord temp = new TradeRecord();
        Date date=new Date();
        temp.setCreateDate(date);
        temp.setAdId(id);
        temp.setType(TradeTypeEnum.WECHATPAY);
        temp.setOwnerAccount(user.getId().toString());
        temp.setTradeNo(outTradeNo);

        // 检查是否可以使用免费广告
        long freeAdNums = user.getFreeAdNums();
        adLevel = carAd.getAdLevel();
        if(adLevel == AdLevelEnum.STANDARD){
            if (freeAdNums > 0) {
                isFree = true;
            }
        }

        // 可以使用免费广告
        if(isFree){
            temp.setPrice(price);
            temp.setStatus(TradeStatusEnum.FREE);
            return "";
            // 无法使用免费广告
        }else {
            // 根据广告类型，汽车价格，载具类型设置不同价格
//            System.out.println("adLevel: " + adLevel);
//            System.out.println("car price: " + carAd.getPrice());
//            System.out.println("VehicleAdType: " + carAd.getVehicleAdType());
            price = BigDecimal.valueOf(vehicleAdService.getAdPrice(adLevel, carAd.getPrice(), carAd.getVehicleAdType()));
//            System.out.println("广告具体价格为: " + price);
            temp.setPrice(price);
            temp.setStatus(TradeStatusEnum.PAYING);
        }

        tradeRecordRepository.save(temp);

        CloseableHttpClient httpClient = setup();

        HttpPost httpPost = new HttpPost("https://api.mch.weixin.qq.com/v3/pay/transactions/native");
        // 请求body参数
        String reqdata = "{"
//                + "\"time_expire\":\"2018-06-08T10:34:56+08:00\","
                + "\"amount\": {"
                + "\"total\": " + Integer.parseInt(String.valueOf(price)) * 100 + ","
                + "\"currency\":\"CNY\""
                + "},"
                + "\"mchid\":\"" + mchId + "\","
                + "\"description\":\"简单买卖 " + adLevel + "广告" + "\","
                + "\"notify_url\":\"https://www.jiandanmaimai.com/api/wechat/pay/notify.do/\","
                + "\"out_trade_no\":\"" + outTradeNo + "\","
                + "\"goods_tag\":\"载具广告\","
                + "\"appid\":\"" + appId + "\","
                + "\"attach\":\"广告购买成功后可以进行发布和完成交易等操作\","
                + "\"scene_info\": {"
                + "\"store_info\": {"
                + "\"address\":\"深圳市福田区福田街道口岸社区福田南路38号广银大厦1118J3\","
                + "\"area_code\":\"518033\","
                + "\"name\":\"深圳信盾电子商务有限责任公司\","
                + "\"id\":\"0001\""
                + "},"
                + "\"payer_client_ip\":\"14.23.150.211\""
                + "}"
                + "}";
        StringEntity entity = new StringEntity(reqdata,"utf-8");
        entity.setContentType("application/json");
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");

        //完成签名并执行请求
        CloseableHttpResponse response = httpClient.execute(httpPost);

        try {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) { //处理成功
                System.out.println("success,return body = " + EntityUtils.toString(response.getEntity()));
                return JSONObject.parseObject(EntityUtils.toString(response.getEntity())).get("code_url").toString()    ;
            } else if (statusCode == 204) { //处理成功，无返回Body
                System.out.println("success");
                return "";
            } else {
//                if(JSONObject.parseObject(EntityUtils.toString(response.getEntity())).get("message").toString().contains("201")){
//                    closeOrder(outTradeNo);
//                    System.out.println("closing the previous order");
//                }
                System.out.println("failed,resp code = " + statusCode+ ",return body = " + EntityUtils.toString(response.getEntity()));
                throw new IOException("request failed");
            }
        } finally {
            response.close();
        }
    }

    @Override
    public String payNotify(String notifyData) throws GeneralSecurityException, IOException {
        // 使用APIv3来初始化解密工具
        AesUtil aesUtil = new AesUtil(apiV3Key.getBytes(StandardCharsets.UTF_8));

        // 获取到解密的对象资源
        JSONObject resource = (JSONObject) JSONObject.parseObject(notifyData).get("resource");

        // 分别获取到解密需要的参数
        String associated_data = resource.get("associated_data").toString();
        String nonce = resource.get("nonce").toString();
        String ciphertext = resource.get("ciphertext").toString();

        System.out.println(aesUtil.decryptToString(associated_data.getBytes(StandardCharsets.UTF_8), nonce.getBytes(StandardCharsets.UTF_8), ciphertext));

        // 得到解密后的JSON数据
        JSONObject afterDecrypt = JSONObject.parseObject(aesUtil.decryptToString(associated_data.getBytes(StandardCharsets.UTF_8), nonce.getBytes(StandardCharsets.UTF_8), ciphertext));

        // 获取订单状态和订单号
        String tradeState = afterDecrypt.get("trade_state").toString();
        String outTradeNo = afterDecrypt.get("out_trade_no").toString();

        System.out.println(tradeState);
        System.out.println(outTradeNo);

        // 如果订单状态未成功
        if(tradeState.equals("SUCCESS")){
            String mchid = afterDecrypt.get("mchid").toString();
            String appid = afterDecrypt.get("appid").toString();
            String tradeType = afterDecrypt.get("trade_type").toString();
            System.out.println(mchId);
            System.out.println(appid);
            System.out.println(tradeType);
            // 继续进行检查，mchid，appid以及trade_type，防止数据伪造
            // 如果确认是合法数据
            if(mchid.equals("1628148828")
                    && appid.equals("wx05c052abbc3110fe")
                    && tradeType.equals("NATIVE")){
                System.out.println("数据合法");
                TradeRecord tradeRecord = tradeRecordRepository.findOneByTradeNoAndStatus(outTradeNo, TradeStatusEnum.PAYING);
                // 如果可以获取到为PAYING状态的订单号，则将其状态改为PAID
                if(tradeRecord != null){
                    tradeRecord.setStatus(TradeStatusEnum.PAID);
                    tradeRecordRepository.save(tradeRecord);
                    // 根据订单号获取到目标载具，将其状态变更为PAID
                    ObjectId curId = tradeRecord.getAdId();
                    if(newCarAdRepository.existsById(curId)){
                        NewCarAd newCarAd = newCarAdRepository.findOneById(curId);
                        newCarAd.setAdStatus(AdStatusEnum.PAID);
                        newCarAdRepository.save(newCarAd);
                    }else if(secondHandCarAdRepository.existsById(curId)){
                        SecondHandCarAd secondHandCarAd = secondHandCarAdRepository.findOneById(curId);
                        secondHandCarAd.setAdStatus(AdStatusEnum.PAID);
                        secondHandCarAdRepository.save(secondHandCarAd);
                    }else if(newMotorAdRepository.existsById(curId)){
                        NewMotorAd newMotorAd = newMotorAdRepository.findOneById(curId);
                        newMotorAd.setAdStatus(AdStatusEnum.PAID);
                        newMotorAdRepository.save(newMotorAd);
                    }else if(secondHandMotorAdRepository.existsById(curId)){
                        SecondHandMotorAd secondHandMotorAd = secondHandMotorAdRepository.findOneById(curId);
                        secondHandMotorAd.setAdStatus(AdStatusEnum.PAID);
                        secondHandMotorAdRepository.save(secondHandMotorAd);
                    }else {
                        throw new VehicleException(ResultCodeEnum.NOT_FOUND, "can not find this vehicle in databse");
                    }
                    System.out.println("operation success, already modify the trade record");
                    return "SUCCESS";
                }else {
                    throw new PayException(ResultCodeEnum.NOT_FOUND, "can not find target trade record");
                }
            }else {
                throw new PayException(ResultCodeEnum.FAILED, "illegal data warning");
            }
        }else {
            throw new PayException(ResultCodeEnum.FAILED, "trade failure, please try again");
        }
    }


    /**
     * 内部私有方法，新建一个微信支付客户端
     *
     * @return {@link CloseableHttpClient}
     * @throws IOException ioexception
     */
    private CloseableHttpClient setup() throws IOException {
        // 加载商户私钥（privateKey：私钥字符串）
        PrivateKey merchantPrivateKey = PemUtil
                .loadPrivateKey(new ByteArrayInputStream(privateKey.getBytes(StandardCharsets.UTF_8)));
        // 加载平台证书（mchId：商户号,mchSerialNo：商户证书序列号,apiV3Key：V3密钥）
        AutoUpdateCertificatesVerifier verifier = new AutoUpdateCertificatesVerifier(
                new WechatPay2Credentials(mchId, new PrivateKeySigner(mchSerialNo, merchantPrivateKey)),apiV3Key.getBytes(StandardCharsets.UTF_8));
        // 初始化httpClient
        return WechatPayHttpClientBuilder.create()
                .withMerchant(mchId, mchSerialNo, merchantPrivateKey)
                .withValidator(new WechatPay2Validator(verifier)).build();
    }

    /**
     * @description: 内部方法，检查并获取广告
     * @param {ObjectId} adId 广告的id
     * @param {VehicleAdTypeEnum} type 广告的类型
     * @param {String} token 登录后header中带有的token，用于验证该用户合法性
     * @param {MongoRepository} repository 广告的持久层
     * @return {VehicleAd} 返回的广告
     */
    private VehicleAd checkAndGetAd(ObjectId adId, String token) {
        // 检查输入的用户是否存在
        String phone = JWTUtil.getValue(token);
        if (!userRepository.existsByPhone(phone)) {
            throw new PayException(ResultCodeEnum.NOT_FOUND, "User not exist");
        }

        // 检查id对应载具是否存在并获取
        VehicleAd vehicleAd = vehicleAdService.getVehicleById(adId);

        // 判断用户是否拥有该广告
        if (!vehicleAd.getUserId().equals(userRepository.findOneByPhone(phone).getId())) {
            throw new PayException(ResultCodeEnum.FAILED, "User is not this ad owner");
        }

        return vehicleAd;
    }

    public void closeOrder(String tradeNo) throws Exception {
        CloseableHttpClient httpClient = setup();
        //请求URL
        HttpPost httpPost = new HttpPost("https://api.mch.weixin.qq.com/v3/pay/transactions/out-trade-no/" + tradeNo + "/close");
        //请求body参数
        String reqdata ="{\"mchid\": \""+mchId+"\"}";

        StringEntity entity = new StringEntity(reqdata,"utf-8");
        entity.setContentType("application/json");
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");

        //完成签名并执行请求
        CloseableHttpResponse response = httpClient.execute(httpPost);
        try {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                System.out.println("success,return body = " + EntityUtils.toString(response.getEntity()));
            } else if (statusCode == 204) {
                System.out.println("success");
            } else {
                System.out.println("failed,resp code = " + statusCode+ ",return body = " + EntityUtils.toString(response.getEntity()));
                throw new IOException("request failed");
            }
        } finally {
            response.close();
        }
    }
}
