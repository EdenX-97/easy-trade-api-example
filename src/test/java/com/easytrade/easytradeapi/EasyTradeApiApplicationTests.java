package com.easytrade.easytradeapi;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.auth.sts.AssumeRoleRequest;
import com.aliyuncs.auth.sts.AssumeRoleResponse;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.easytrade.easytradeapi.constant.enums.VehicleAdTypeEnum;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootTest
class EasyTradeApiApplicationTests {

    @Test
    void testupload() {
        ObjectId id = new ObjectId("628e09b7195e8f7a7a27f2fe");
        // 载具类型，用于判断目录位置
        VehicleAdTypeEnum vate = VehicleAdTypeEnum.SECONDHANDCARAD;

        // 调用私有方法获得临时的账号，密码和token，索引分别对应为0，1，2
        ArrayList<String> rs = getStsInfo();
        // Endpoint地区为深圳。
        String endpoint = "https://oss-cn-shenzhen.aliyuncs.com";
        // 使用RAM用户进行临时访问
        String accessKeyId = rs.get(0);
        String accessKeySecret = rs.get(1);
        // 从STS服务获取的安全令牌（SecurityToken）。
        String securityToken = rs.get(2);
        // Bucket名称
        String bucketName = "easybuysell-images";
        // 文件前缀
        String keyPrefix = "";
        if(vate.equals(VehicleAdTypeEnum.NEWCARAD)){
            keyPrefix = "imgs/newCar/" + id;
        }else if(vate.equals(VehicleAdTypeEnum.SECONDHANDCARAD)){
            keyPrefix = "imgs/secondHandCar/" + id;
        }else {
            keyPrefix = "imgs/exampleCar/" + id;
        }

        // 从STS服务获取临时访问凭证后，通过临时访问密钥和安全令牌生成OSSClient。
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret, securityToken);

        String objectName = keyPrefix + "/1-imm.jpg";
        byte[] f = new byte[1000];
        PutObjectRequest putObjectRequest = new PutObjectRequest(
                bucketName,
                objectName,
                new ByteArrayInputStream(f));

        ossClient.putObject(putObjectRequest);

        try {
            //则列举包含指定前缀的文件。
            ObjectListing objectListing = ossClient.listObjects(bucketName, keyPrefix);
            List<OSSObjectSummary> sums = objectListing.getObjectSummaries();
            System.out.println(sums);
            //for (OSSObjectSummary s : sums) {
            //    // 如果和前缀名重合，跳过，说明是目录（keyPrefix），不进行存储
            //    if(s.getKey().equals(keyPrefix + "/")){
            //        continue;
            //    }
            //    objectNames.add(s.getKey());
            //}
            //
            //// 设置url过期时间，目前为3600s，即1h
            //Date expiration = new Date(System.currentTimeMillis() + 3600 * 1000);
            //// 生成以GET方法访问的签名URL，访客可以直接通过浏览器访问相关内容。
            //for(String objectName : objectNames){
            //    // 使用generatePresignedUrl接口得到待签名的图片url
            //    URL url = ossClient.generatePresignedUrl(bucketName, objectName, expiration);
            //    imageUrls.add(url);
            //}
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    private ArrayList<String> getStsInfo() {
        // 结果存放集合
        ArrayList<String> rs = new ArrayList<>();
        // STS接入地址，服务器位置为杭州。
        String endpoint = "sts.cn-shenzhen.aliyuncs.com";
        // RAM用户的keyId和keySecret。
        String AccessKeyId = "LTAI5tAH7YbQiNjgyGLzwGnq";
        String accessKeySecret = "9pJPqV8pdDc8qIsucEipjR3BuebuU5";
        // 用户下角色的ARN。
        String roleArn = "acs:ram::1233635843020318:role/ramoss";
        // 自定义角色会话名称，用来区分不同的令牌，随便写，用于区分session。
        String roleSessionName = "RamOss";
        try {
            // RAM的地域ID。
            String regionId = "cn-shenzhen";
            // 添加endpoint。适用于Java SDK 3.12.0以下版本。
            DefaultProfile.addEndpoint("",regionId, "Sts", endpoint);
            // 构造default profile。
            IClientProfile profile = DefaultProfile.getProfile(regionId, AccessKeyId, accessKeySecret);
            // 构造client。
            DefaultAcsClient client = new DefaultAcsClient(profile);
            final AssumeRoleRequest request = new AssumeRoleRequest();
            // 组合请求，setMethod方法仅适用于Java SDK 3.12.0以下版本。
            request.setMethod(MethodType.POST);
            request.setRoleArn(roleArn);
            request.setRoleSessionName(roleSessionName);
            request.setDurationSeconds(3600L); // 设置临时访问凭证的有效时间为3600秒。
            final AssumeRoleResponse response = client.getAcsResponse(request);
            //System.out.println("Expiration: " + response.getCredentials().getExpiration());
            //System.out.println("Access Key Id: " + response.getCredentials().getAccessKeyId());
            //System.out.println("Access Key Secret: " + response.getCredentials().getAccessKeySecret());
            //System.out.println("Security Token: " + response.getCredentials().getSecurityToken());
            //System.out.println("RequestId: " + response.getRequestId());
            rs.add(response.getCredentials().getAccessKeyId());
            rs.add(response.getCredentials().getAccessKeySecret());
            rs.add(response.getCredentials().getSecurityToken());
            return rs;
        } catch (ClientException e) {
            System.out.println("Failed：");
            System.out.println("Error code: " + e.getErrorCode());
            System.out.println("Error message: " + e.getErrorMessage());
            System.out.println("RequestId: " + e.getRequestId());
        } catch (com.aliyuncs.exceptions.ClientException e) {
            e.printStackTrace();
        }
        return rs;
    }

}
