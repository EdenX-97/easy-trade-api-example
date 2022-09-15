/**
 * @author: Hongzhang Liu
 * @description 实名认证实现接口
 * @date 4/4/20221:08 am
 */
package com.easytrade.easytradeapi.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.easytrade.easytradeapi.constant.enums.ResultCodeEnum;
import com.easytrade.easytradeapi.constant.enums.UserStatusEnum;
import com.easytrade.easytradeapi.constant.exceptions.UserException;
import com.easytrade.easytradeapi.model.User;
import com.easytrade.easytradeapi.repository.UserRepository;
import com.easytrade.easytradeapi.service.intf.AuthService;
import com.easytrade.easytradeapi.service.intf.UserService;
import com.easytrade.easytradeapi.utils.HttpUtil;
import com.easytrade.easytradeapi.utils.JWTUtil;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Override
    public Boolean realNameAuthByIDandName(String token, String firstname, String lastname, String idcard) {
        // 校验并根据token获取用户手机号
        String phone = JWTUtil.getValue(token);
        if (phone == null) {
            throw new UserException(ResultCodeEnum.INVALID_PARAM, "Token invalid");
        }

        // 主机地址
        String host = "https://id2meta.market.alicloudapi.com";
        // 具体的访问路径
        String path = "/id2meta";
        // 请求方式
        String method = "GET";
        // 申请的appcode
        String appcode = "77a861efc1fc4642b356aecf1802b4be";
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中放入认证信息
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> querys = new HashMap<String, String>();
        // 拼接姓名
        String name = lastname + firstname;
        // 将身份证号和真实姓名存入到map中发送给验证服务器
        querys.put("identifyNum", idcard);
        querys.put("userName", name);

        try {
            HttpResponse response = HttpUtil.doGet(host, path, method, headers, querys);
            // 获取response的body
            String body = EntityUtils.toString(response.getEntity());
            // 转化body为json对象
            JSONObject data = JSONObject.parseObject(body);
            //System.out.println(data);

            // 判断是否实名认证成功，只有返回值为200时为认证成功
            boolean isRight = Objects.equals(data.get("code").toString(), "200");

            // 如果真实姓名与身份证匹配，则自动绑定账号的姓名
            if (isRight) {
                User user = userService.getUserByToken(token, true);
                user.setFirstname(firstname);
                user.setLastname(lastname);
                user.setIdcard(idcard);
                // 实名认真后更改用户状态（已实名认证）
                user.setRole(UserStatusEnum.USER.getStatus());

//                // 解析身份证性别，倒数第二奇数为男，偶数为女
//                if(Integer.parseInt(String.valueOf(idcard.charAt(idcard.length() - 2))) % 2 == 0){
//                    user.setGender("女");
//                }else {
//                    user.setGender("男");
//                }
                userRepository.save(user);
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void dealerAuth(String token, String creditCode, String companyName) {
        //Config config = new Config()
        //        // 您的AccessKey ID
        //        .setAccessKeyId("LTAI5tLTBb82Tq5jxZtCAAKF")
        //        // 您的AccessKey Secret
        //        .setAccessKeySecret("oDvWBBkBYHfzPE7SdU7esSzPSUqjzK");
        //// 访问的域名
        //config.endpoint = "ocr-api.cn-hangzhou.aliyuncs.com";
        //com.aliyun.ocr_api20210707.Client client =  new com.aliyun.ocr_api20210707.Client(config);
        //java.io.InputStream bodySyream = com.aliyun.darabonba.stream.Client.readFromFilePath(path);
        //RecognizeBusinessLicenseRequest recognizeBusinessLicenseRequest = new RecognizeBusinessLicenseRequest()
        //        .setBody(bodySyream);
        //RuntimeOptions runtime = new RuntimeOptions();
        //// 复制代码运行请自行打印 API 的返回值
        //RecognizeBusinessLicenseResponse resp = client.recognizeBusinessLicenseWithOptions(recognizeBusinessLicenseRequest, runtime);
        //JSONObject data = JSONObject.parseObject(com.aliyun.teautil.Common.toJSONString(TeaModel.buildMap(resp))).getJSONObject("body");
        //System.out.println(data);
        //return false;

        // 校验并根据token获取用户手机号
        String phone = JWTUtil.getValue(token);
        if (phone == null) {
            throw new UserException(ResultCodeEnum.INVALID_PARAM, "Token invalid");
        }

        // 获取用户并检查用户类型是否正确
        User dealer = userRepository.findOneByPhone(phone);
        if (!dealer.getRole().equals(UserStatusEnum.USER.getStatus())) {
            throw new UserException(ResultCodeEnum.FAILED, "Role is not user");
        }

        // 正则校验统一信用码
        String creditCodeRegex = "^[0-9A-HJ-NPQRTUWXY]{2}\\d{6}[0-9A-HJ-NPQRTUWXY]{10}$";
        if (!Pattern.matches(creditCodeRegex, creditCode)) {
            throw new UserException(ResultCodeEnum.FAILED, "Credit code format incorrect");
        }

        // 检查统一信用码是否存在
        if (userRepository.existsByCreditCode(creditCode)) {
            throw new UserException(ResultCodeEnum.FAILED, "Credit code exist");
        }

        // 通过校验，开始保存
        dealer.setCreditCode(creditCode);
        dealer.setCompanyName(companyName);
        dealer.setRole(UserStatusEnum.DEALER.getStatus());
        userRepository.save(dealer);
    }

//    @Override
//    public Boolean dealerAuth(String token, String creditCode) {
//        // 校验并根据token获取用户手机号
//        String phone = JWTUtil.getValue(token);
//        if (phone == null) {
//            throw new UserException(ResultCodeEnum.INVALID_PARAM, "Token invalid");
//        }
//
//        //申请的appcode
//        String appcode = "75ad0321dc9e48a29671b337d6b9d108";
//        //请求地址
//        String strUrl="http://cardnotwo.market.alicloudapi.com/company";
//        //请求参数
//        String param = "com=" + creditCode;
//        // 新建参数并初始化
//        URL url = null;
//        HttpURLConnection httpURLConnection = null;
//        try {
//            url = new URL(strUrl + "?" + param);
//            httpURLConnection = (HttpURLConnection) url.openConnection();
//            httpURLConnection.setRequestProperty("Accept-Charset", "utf-8");
//            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//            httpURLConnection.setRequestProperty("Authorization", "APPCODE " + appcode);
//            httpURLConnection.setDoOutput(true);
//            httpURLConnection.setDoInput(true);
//            // 设置请求方式
//            httpURLConnection.setRequestMethod("POST");
//            // 不使用缓存
//            httpURLConnection.setUseCaches(false);
//            // 建立连接
//            httpURLConnection.connect();
//            // 使用BufferedReader类读取数据
//            BufferedReader reader = new BufferedReader(
//                    new InputStreamReader(httpURLConnection.getInputStream(), "utf-8"));
//            StringBuffer buffer = new StringBuffer();
//            String line = "";
//            while ((line = reader.readLine()) != null) {
//                buffer.append(line);
//            }
//            reader.close();
//            // 将buffer中的数据转换为字符串形式
//            String result = buffer.toString();;
//            // 输出结果
//            System.out.println(result);
//            // 将字符串转换为JSON格式
//            JSONObject object = JSONObject.parseObject(result);
//            // 得到状态码
//            String errorCode = object.getString("error_code");
//            // 如果状态码为50002，说明未找到该企业，返回false
//            if(errorCode.equals("50002")){
//                System.out.println("can not find company according to this credit code");
//                return false;
//            }
//            // 获取json中的法人信息
//            String faRen = object.getJSONObject("result").getString("faRen");
//            // 获取json中的营业状态信息
//            String businessStatus = object.getJSONObject("result").getString("businessStatus");
//            // 根据phone得到用户信息
//            User user = userRepository.findOneByPhone(phone);
//            // 将用户姓和名进行拼接
//            String userName = user.getLastname() + user.getFirstname();
//            // 如果用户姓名和法人姓名一致，并且企业状态处于存续，在营，开业或在册其中一种，则说明认证成功，否则视为失败
//            if(userName.equals(faRen) && businessStatus.equals("存续") || businessStatus.equals("在营")
//                    || businessStatus.equals("开业") || businessStatus.equals("在册")){
//                System.out.println("dealer authorization successfully");
//                return true;
//            }
//            return false;
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (httpURLConnection != null) {
//                httpURLConnection.disconnect();
//            }
//        }
//        return false;
//    }
}
