/**
 * @author: Hongzhang Liu
 * @description 敏感词检测实现类
 * @date 4/4/202210:35 pm
 */
package com.easytrade.easytradeapi.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.ClientException;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.green.model.v20180509.TextScanRequest;
import com.aliyuncs.http.FormatType;
import com.aliyuncs.http.HttpResponse;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.easytrade.easytradeapi.service.intf.SensitiveContentDetectionService;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.*;

@Service
public class SensitiveContentDetectionServiceImpl implements SensitiveContentDetectionService {

    @Override
    public String detectSensitiveContent(String str) throws UnsupportedEncodingException {
        // 传入的待检测文本
        String rs = str;
        // 使用账号信息以及endpoint相关信息
        IClientProfile profile = DefaultProfile
                .getProfile("cn-shenzhen", "LTAI5tLTBb82Tq5jxZtCAAKF", "oDvWBBkBYHfzPE7SdU7esSzPSUqjzK");
        DefaultProfile
                .addEndpoint("cn-shenzhen", "Green", "green.cn-shanghai.aliyuncs.com");
        IAcsClient client = new DefaultAcsClient(profile);
        TextScanRequest textScanRequest = new TextScanRequest();
        // 指定API返回格式
        textScanRequest.setAcceptFormat(FormatType.JSON);
        textScanRequest.setHttpContentType(FormatType.JSON);
        // 指定请求方法
        textScanRequest.setMethod(com.aliyuncs.http.MethodType.POST);
        textScanRequest.setEncoding("UTF-8");
        textScanRequest.setRegionId("cn-shenzhen");
        List<Map<String, Object>> tasks = new ArrayList<Map<String, Object>>();
        Map<String, Object> task1 = new LinkedHashMap<String, Object>();
        task1.put("dataId", UUID.randomUUID().toString());
        /**
         * 待检测的文本，长度不超过10000个字符。
         */
        task1.put("content", str);
        tasks.add(task1);
        JSONObject data = new JSONObject();

        /**
         * 检测场景，此处为文本垃圾检测接口，antispam。
         **/
        data.put("scenes", Arrays.asList("antispam"));
        data.put("tasks", tasks);
        // System.out.println(JSON.toJSONString(data, true));
        textScanRequest.setHttpContent(data.toJSONString().getBytes("UTF-8"), "UTF-8", FormatType.JSON);
        // 超时时间。
        textScanRequest.setConnectTimeout(3000);
        textScanRequest.setReadTimeout(6000);
        try {
            HttpResponse httpResponse = client.doAction(textScanRequest);
            if(httpResponse.isSuccess()){
                JSONObject scrResponse = JSON.parseObject(new String(httpResponse.getHttpContent(), "UTF-8"));
                // System.out.println(JSON.toJSONString(scrResponse, true));
                if (200 == scrResponse.getInteger("code")) {
                    JSONArray taskResults = scrResponse.getJSONArray("data");
                    for (Object taskResult : taskResults) {
                        if(200 == ((JSONObject)taskResult).getInteger("code")){
                            /*
                             JSONArray sceneResults = ((JSONObject)taskResult).getJSONArray("results");
                             for (Object sceneResult : sceneResults) {
                                 String scene = ((JSONObject)sceneResult).getString("scene");
                                 String suggestion = ((JSONObject)sceneResult).getString("suggestion");
                                 根据scene和suggetion做相关处理。
                                 suggestion为pass表示未命中垃圾。suggestion为block表示命中了垃圾，可以通过label字段查看命中的垃圾分类。
                                 System.out.println("args = [" + scene + "]");
                                 System.out.println("args = [" + suggestion + "]");
                             }
                             */
                            // 返回过滤后的文本结果
                            if(JSONObject.parseObject(scrResponse.getJSONArray("data").get(0).toString()).getString("filteredContent") != null){
                                rs = JSONObject.parseObject(scrResponse.getJSONArray("data").get(0).toString()).getString("filteredContent");
                            }
                        }else{
                            System.out.println("task process fail:" + ((JSONObject)taskResult).getInteger("code"));
                        }
                    }
                } else {
                    System.out.println("detect not success. code:" + scrResponse.getInteger("code"));
                }
            }else{
                System.out.println("response not success. status:" + httpResponse.getStatus());
            }
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rs;
    }
}
