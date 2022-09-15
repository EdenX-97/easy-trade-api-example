/*
 * @Description: 载具服务层的实现类
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2021-11-13 19:54:52
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-19 03:06:27
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/service/impl/VehicleServiceImpl.java
 */
package com.easytrade.easytradeapi.service.impl;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;

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
import com.easytrade.easytradeapi.config.CarPriceConfig;
import com.easytrade.easytradeapi.config.MotorPriceConfig;
import com.easytrade.easytradeapi.constant.consists.TempVehicleAd;
import com.easytrade.easytradeapi.constant.consists.VehicleAd;
import com.easytrade.easytradeapi.constant.enums.*;
import com.easytrade.easytradeapi.constant.exceptions.JWTException;
import com.easytrade.easytradeapi.constant.exceptions.SecondHandCarException;
import com.easytrade.easytradeapi.constant.exceptions.VehicleException;
import com.easytrade.easytradeapi.listener.model.*;
import com.easytrade.easytradeapi.model.*;
import com.easytrade.easytradeapi.repository.*;
import com.easytrade.easytradeapi.service.intf.VehicleAdService;
import com.easytrade.easytradeapi.utils.*;
import net.coobird.thumbnailator.Thumbnails;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;


@Service
@Slf4j
public class VehicleServiceImpl implements VehicleAdService {
    @Autowired
    ExampleCarRepository exampleCarRepository;

    @Autowired
    ExampleMotorRepository exampleMotorRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    NewCarAdRepository newCarAdRepository;

    @Autowired
    SecondHandCarAdRepository secondHandCarAdRepository;

    @Autowired
    NewMotorAdRepository newMotorAdRepository;

    @Autowired
    SecondHandMotorAdRepository secondHandMotorAdRepository;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    CarPriceConfig carPriceConfig;

    @Autowired
    MotorPriceConfig motorPriceConfig;

    @Autowired
    MongoTemplate mongoTemplate;

    // 图片最大大小
    @Value("${vehiclead.file.image.max-file-size}")
    private long imageMaxSize;

    // 图片最大数量
    @Value("${vehiclead.file.image.max-num}")
    private int imageMaxNum;

    // 视频最大大小
    @Value("${vehiclead.file.video.max-file-size}")
    private long videoMaxSize;

    // 视频最大数量
    @Value("${vehiclead.file.video.max-num}")
    private int videoMaxNum;

    // 上传路径
    @Value("${web.upload-path}")
    private String uploadPath;

    // 最大收藏数量
    @Value("${user.favorite.max}")
    private int favoriteMaxNum;

    // redis数据自定义过期时间，用于点击、浏览、收藏，默认为7天
    @Value("${spring.redis.custom.expire}")
    private long expire;

    // redis数据自定义过期时间，用于点击、浏览、收藏，默认为7天
    @Value("${page.pagesize}")
    private int pageSize;

    @Value("${vehiclead.realPrice.percent}")
    private long realPricePercent;

    @Override
    public VehicleAd getVehicleById(ObjectId id) {
        // 从不同的repository中获取对应类型的载具
        VehicleAd vehicleAd = null;
        if (secondHandCarAdRepository.existsById(id)) {
            vehicleAd = secondHandCarAdRepository.findOneById(id);
        } else if (newCarAdRepository.existsById(id)) {
            vehicleAd = newCarAdRepository.findOneById(id);
        } else if(newMotorAdRepository.existsById(id)){
            vehicleAd = newMotorAdRepository.findOneById(id);
        } else if(secondHandMotorAdRepository.existsById(id)){
            vehicleAd = secondHandMotorAdRepository.findOneById(id);
        }

        // id没有对应存在的载具，则抛出异常
        if (vehicleAd == null) {
            throw new VehicleException(ResultCodeEnum.NOT_FOUND, "Id do not exist");
        }

        return vehicleAd;
    }

    @Override
    public String getVehicleTitleById(ObjectId id) {
        // 根据id获取对应载具
        VehicleAd vehicleAd = getVehicleById(id);
        // 根据载具类型获取对应模版
        VehicleAdTypeEnum type = vehicleAd.getVehicleAdType();
        switch (type) {
            case NEWCARAD: case SECONDHANDCARAD:
                ExampleCar exampleCar = exampleCarRepository.findOneById(vehicleAd.getExampleVehicleId());
                return exampleCar.getYear() + " " + exampleCar.getSeries();
                //break;
            case NEWMOTORAD: case SECONDHANDMOTORAD:
                ExampleMotor exampleMotor = exampleMotorRepository.findOneById(vehicleAd.getExampleVehicleId());
                return exampleMotor.getSeries();
                //break;
            default:
                // 没有找到对应模版，抛出异常
                throw new VehicleException(ResultCodeEnum.NOT_FOUND, "Cannot find example vehicle");
        }
    }

    @Override
    public VehicleAdTypeEnum getVehicleTypeById(ObjectId id) {
        // 根据id获取对应载具
        VehicleAd vehicleAd = getVehicleById(id);

        // 返回对应类型
        return vehicleAd.getVehicleAdType();
    }

    @Override
    public void view(ObjectId id) {
        // 检查id对应载具是否存在
        getVehicleById(id);

        // 先判断record，存在时说明此ip在24小时内浏览过，不能再次增加浏览数
        // recordKey保存该载具广告对应某一id的交互时间，e.g.
        // records:views:newcarad:192.111.111:618fa328a53cb822b625e7e1，value为交互时间
        String recordKey = "records:views:" + IPUtil.getIpAddr() + ":" + id;
        if (redisUtil.hasKey(recordKey)) {
            return;
            //throw new VehicleException(ResultCodeEnum.FAILED, "This ip already view in 24 hours");
        }

        // record不存在时，说明24小时内没有浏览，进行+1操作并保存新的record
        // timesKey保存该载具广告的浏览数，e.g. times:views:newcarad:618fa328a53cb822b625e7e1，value为浏览数
        String timesKey = "times:views:" + id;
        // redis中如果没有views，就从mongodb获取数据，再存入redis，然后统一进行+1，再保存record
        if (!redisUtil.hasKey(timesKey)) {
            long nowViews = getViews(id);
            // 交互数在redis中默认为保存7天，每3天会固定将redis中数据更新到mongodb，然后删除该数据
            redisUtil.set(timesKey, String.valueOf(nowViews), expire);
        }
        redisUtil.incr(timesKey, 1);
        // record在redis中默认为保存1天，24小时后自动删除，然后用户可以进行新的record
        redisUtil.set(recordKey, new Date().toString());
    }

    @Override
    public void click(ObjectId id) {
        // 检查id对应载具是否存在
        getVehicleById(id);

        // 先判断record，存在时说明此ip在24小时内点击过，不能再次增加点击数
        // recordKey保存该载具广告对应某一id的交互时间，e.g.
        // records:clicks:newcarad:192.111.111:618fa328a53cb822b625e7e1，value为交互时间
        String recordKey = "records:clicks:" + IPUtil.getIpAddr() + ":" + id;
        if (redisUtil.hasKey(recordKey)) {
            throw new VehicleException(ResultCodeEnum.FAILED, "This ip already click in 24 hours");
        }

        // record不存在时，说明24小时内没有点击，进行+1操作并保存新的record
        // timesKey保存该载具广告的点击数，e.g. times:clicks:newcarad:618fa328a53cb822b625e7e1，value为点击数
        String timesKey =
                "times:clicks:" + id.toString();
        // redis中如果没有views，就从mongodb获取数据，再存入redis，然后统一进行+1，再保存record
        if (!redisUtil.hasKey(timesKey)) {
            long nowViews = getClicks(id);
            // 交互数在redis中默认为保存7天，每3天会固定将redis中数据更新到mongodb，然后删除该数据
            redisUtil.set(timesKey, String.valueOf(nowViews), expire);
        }
        redisUtil.incr(timesKey, 1);
        // record在redis中默认为保存1天，24小时后自动删除，然后用户可以进行新的record
        redisUtil.set(recordKey, new Date().toString());
    }

    @Override
    public void favorite(ObjectId id, String account) {
        // 检查id对应载具是否存在
        getVehicleById(id);

        // 检查account是否存在
        if (!userRepository.existsByPhone(account)) {
            throw new VehicleException(ResultCodeEnum.NOT_FOUND, "User do not exist");
        }

        // 获取车辆收藏者列表，如果record不存在，则从mongoDB中调取数据，保存至redis，然后只对redis中缓存进行操作
        // recordKey保存该载具广告对应某一id的交互时间，e.g.
        // records:favorites:newcarad:618fa328a53cb822b625e7e1，value为一个set，包含所有已收藏的用户账号
        String recordKey =
                "records:favorites:" + id.toString();
        if (!redisUtil.hasKey(recordKey)) {
            ArrayList<String> favoriteUsers = getFavorites(id);
            // 将favorite中的数据保存到redis
            for (String record : favoriteUsers) {
                // favorite的数据在redis中保存7天，每3天系统会自动将数据更新到mongodb
                redisUtil.sSetAndTime(recordKey, expire, record);
            }
        }

        // 从redis中获取收藏者列表
        ArrayList<String> favoriteUsers = new ArrayList<>(redisUtil.sGet(recordKey));

        // 判断收藏者列表中是否已有用户，不能重复收藏
        if (favoriteUsers.contains(account)) {
            throw new VehicleException(ResultCodeEnum.FAILED, "This user already favorite this vehicle");
        }

        // 获取用户收藏的载具列表，如果不存在，则从mongoDB中调取，保存至redis，然后只对redis中缓存进行操作
        String userKey = "records:favorites:users:" + account;
        if (!redisUtil.hasKey(userKey)) {
            User user = userRepository.findOneByPhone(account);
            ArrayList<ObjectId> userFavorites = user.getFavoriteList();
            // 将该用户收藏的载具列表保存到redis
            for (ObjectId favoriteId : userFavorites) {
                // favorite的数据在redis中保存7天，每3天系统会自动将数据更新到mongodb
                redisUtil.sSetAndTime(userKey, expire, favoriteId.toString());
            }
        }

        // 从redis中获取用户收藏列表
        ArrayList<String> userFavorites = new ArrayList<>(redisUtil.sGet(userKey));

        // 判断用户是否已收藏该载具
        if (userFavorites.contains(id.toString())) {
            throw new VehicleException(ResultCodeEnum.FAILED, "This user already favorite this vehicle");
        }

        // 判断redis中是否有该用户的待删除列表
        String removedKey = "removed:favorites:users:" + account;
        if (redisUtil.hasKey(removedKey)) {
            // 判断待删除列表中是否有该载具，有的话则从该列表中删除
            ArrayList<String> removedList = new ArrayList<>(redisUtil.sGet(removedKey));
            if (removedList.contains(id.toString())) {
                redisUtil.setRemove(removedKey, id.toString());
            }
        }

        // 获取该载具的收藏数计数，如果不存在，则直接更新该数据
        String timesKey = "times:favorites:" + id.toString();
        if (!redisUtil.hasKey(timesKey)) {
            redisUtil.set(timesKey, String.valueOf(favoriteUsers.size()), expire);
        }

        // 如果用户收藏数量超过限制，则抛出异常
        userFavorites = new ArrayList<>(redisUtil.sGet(userKey));
        if (userFavorites.size() + 1 > favoriteMaxNum) {
            throw new VehicleException(ResultCodeEnum.FAILED, "Cannot favorite more than 50 vehicles");
        }

        // 通过校验，开始进行添加操作，根据三个key对redis添加数据即可
        // 1. 对该载具的key，添加新的收藏用户
        redisUtil.sSetAndTime(recordKey, expire, account);
        // 2. 对该用户的key，添加新的收藏载具
        redisUtil.sSetAndTime(userKey, expire, id.toString());
        // 3. 对该载具的收藏数计数key，数量+1
        redisUtil.incr(timesKey, 1);
    }

    @Override
    public void cancelFavorite(ObjectId id, String account) {
        // 检查id对应载具是否存在
        getVehicleById(id);

        // 检查账号是否存在
        if (!userRepository.existsByPhone(account)) {
            throw new VehicleException(ResultCodeEnum.NOT_FOUND, "User do not exist");
        }

        // 获取车辆收藏者列表，如果record不存在，则从mongoDB中调取数据，保存至redis，然后只对redis中缓存进行操作
        // recordKey保存该载具广告对应某一id的交互时间，e.g.
        // records:favorites:newcarad:618fa328a53cb822b625e7e1，value为一个set，包含所有已收藏的用户账号
        String recordKey =
                "records:favorites:" + id.toString();
        if (!redisUtil.hasKey(recordKey)) {
            ArrayList<String> favoriteUsers = getFavorites(id);
            // 将favorite中的数据保存到redis
            for (String record : favoriteUsers) {
                // favorite的数据在redis中保存7天，每3天系统会自动将数据更新到mongodb
                redisUtil.sSetAndTime(recordKey, expire, record);
            }
        }

        // 从redis中获取收藏者列表
        ArrayList<String> favoriteUsers = new ArrayList<>(redisUtil.sGet(recordKey));
        // 判断收藏者列表中是否已有用户，有才能删除
        if (!favoriteUsers.contains(account)) {
            throw new VehicleException(ResultCodeEnum.FAILED, "This user do not favorite this vehicle");
        }

        // 获取用户收藏的载具列表，如果不存在，则从mongoDB中调取，保存至redis，然后只对redis中缓存进行操作
        String userKey = "records:favorites:users:" + account;
        if (!redisUtil.hasKey(userKey)) {
            User user = userRepository.findOneByPhone(account);
            ArrayList<ObjectId> userFavorites = user.getFavoriteList();
            // 将该用户收藏的载具列表保存到redis
            for (ObjectId favoriteId : userFavorites) {
                // favorite的数据在redis中保存7天，每3天系统会自动将数据更新到mongodb
                redisUtil.sSetAndTime(userKey, expire, favoriteId.toString());
            }
        }

        // 从redis中获取用户收藏列表
        ArrayList<String> userFavorites = new ArrayList<>(redisUtil.sGet(userKey));

        // 判断用户是否已收藏该载具，有才能删除
        if (!userFavorites.contains(id.toString())) {
            throw new VehicleException(ResultCodeEnum.FAILED, "This user do not favorite this vehicle");
        }

        // 判断redis中是否有该用户的待删除列表
        String removedKey = "removed:favorites:users:" + account;
        if (redisUtil.hasKey(removedKey)) {
            // 判断待删除列表中是否有该载具
            ArrayList<String> removedList = new ArrayList<>(redisUtil.sGet(removedKey));
            if (removedList.contains(id.toString())) {
                throw new VehicleException(ResultCodeEnum.FAILED, "Already cancel favorite this vehicle");
            }
        }

        // 获取该载具的收藏数计数，如果不存在，则直接更新该数据
        String timesKey = "times:favorites:" + id.toString();
        if (!redisUtil.hasKey(timesKey)) {
            redisUtil.set(timesKey, String.valueOf(favoriteUsers.size()), expire);
        }

        // 通过校验，开始进行添加操作，根据三个key对redis添加数据即可
        // 1. 对该载具的key，删除收藏用户
        redisUtil.setRemove(recordKey, account);
        // 2. 对该用户的key，删除该收藏载具
        redisUtil.setRemove(userKey, id.toString());
        // 3. 对该载具的收藏数计数key，数量-1
        redisUtil.decr(timesKey, 1);
        // 4. 对用户的删除列表，添加该载具
        redisUtil.sSetAndTime(removedKey, expire, id.toString());
    }

    @Override
    public long getViews(ObjectId id) {
        // 从Mongodb中获取数据
        VehicleAd vehicleAd = getVehicleById(id);
        return vehicleAd.getViews();
    }

    @Override
    public long getClicks(ObjectId id) {
        // 从Mongodb中获取数据
        VehicleAd vehicleAd = getVehicleById(id);
        return vehicleAd.getClicks();
    }

    @Override
    public ArrayList<String> getFavorites(ObjectId id) {
        // 判断该载具是否存在
        VehicleAd vehicleAd = getVehicleById(id);

        ArrayList<String> favorites;
        // 先尝试从redis中获取数据
        String recordKey = "records:favorites:" + id.toString();
        if (redisUtil.hasKey(recordKey)) {
            favorites = new ArrayList<>(redisUtil.sGet(recordKey));
            if (favorites == null || favorites.isEmpty()) {
                favorites = new ArrayList<>();
            }
        } else {
            // 从Mongodb中获取数据
            favorites = vehicleAd.getFavoriteUsers();
            if (favorites == null) {
                favorites = new ArrayList<>();
            }
        }

        return favorites;
    }

    @Override
    public void updateAdInRedisToMongodb() {
        List<String> dataTypes = List.of("views", "clicks", "favorites");

        for (String dataType : dataTypes) {
            // 从redis中提取对应的keys
            Set<String> keys = redisUtil.scan("times:" + dataType + ":*");

            // 遍历每一个key，对应不同的载具类型和载具id在mongodb中找到对应的载具
            MongoRepository repository = null;
            VehicleAd vehicleAd = null;
            for (String key : keys) {
                // 将key分割，第三个参数为载具id
                String[] splitKey = key.split(":");
                String vehicleId = splitKey[2];

                // 根据载具类型和载具id在mongodb中找到对应的载具
                vehicleAd = getVehicleById(new ObjectId(vehicleId));
                repository = getRepository(vehicleAd.getVehicleAdType());

                // 对应不同的数据类型进行更新数据
                switch (dataType) {
                    case "views":
                        vehicleAd.setViews(Long.parseLong(redisUtil.get(key).toString()));
                        break;
                    case "clicks":
                        vehicleAd.setClicks(Long.parseLong(redisUtil.get(key).toString()));
                        break;
                    case "favorites":
                        // favorites在mongodb中保存为records所包含的数个用户账号，因此需要特殊处理
                        // 如果times的key对应的值为0，说明所有用户取消收藏导致该载具的收藏数为0，此时record中不包含favorites
                        if (redisUtil.get(key).toString().equals("0")) {
                            vehicleAd.setFavoriteUsers(new ArrayList<>());
                        }

                        // 直接将redis中的数据，替换掉vehicle中的数据
                        String recordsKey = key.replace("times", "records");
                        ArrayList<String> favoriteUsers = new ArrayList<>(redisUtil.sGet(recordsKey));
                        vehicleAd.setFavoriteUsers(favoriteUsers);

                        // 保存完载具数据后，开始保存用户数据
                        for (String userPhone : favoriteUsers) {
                            // 获取用户
                            User user = null;
                            if (userRepository.existsByPhone(userPhone)) {
                                user = userRepository.findOneByPhone(userPhone);
                            } else {
                                // 如果用户id不存在，则直接跳过
                                continue;
                            }

                            // 将该载具id保存到用户的favoriteList中
                            ArrayList<ObjectId> favoriteList = user.getFavoriteList();
                            ObjectId addedVehicleId = new ObjectId(vehicleId);
                            // 如果已包含，则不重复添加
                            if (!favoriteList.contains(addedVehicleId)) {
                                favoriteList.add(addedVehicleId);
                            }

                            // 对removedList中的数据，如果其中的某个id在favoriteList中存在，则删除
                            String removedKey = "removed:favorites:" + "users:" + userPhone;
                            ArrayList<String> removedList = new ArrayList<>(redisUtil.sGet(removedKey));
                            for (String removedId : removedList) {
                                ObjectId removedObjectId = new ObjectId(removedId);
                                if (favoriteList.contains(removedObjectId)) {
                                    favoriteList.remove(removedObjectId);
                                }
                            }

                            // 将处理完的数据保存到mongoDB
                            user.setFavoriteList(favoriteList);
                            userRepository.save(user);

                            // 删除该用户删除列表中的数据
                            redisUtil.del(removedKey);

                            // 删除该用户缓存的该类型载具收藏列表
                            String userFavoriteKey = removedKey.replace("removed", "records");
                            redisUtil.del(userFavoriteKey);
                        }

                        // 删除records中的数据
                        redisUtil.del(recordsKey);

                        break;
                }

                // 将更新后的vehicleAd保存至数据库
                repository.save(vehicleAd);
                // 更新完成后删除对应的key
                redisUtil.del(key);
            }
        }
        
        log.info("Complete update redis data to mongodb");
    }

    @Override
    public void uploadFiles(ObjectId id, FileTypeEnum fileType,
            MultipartFile[] files, String token) throws IOException {
        // 校验token的合法性
        if (!token.startsWith("Bearer ")) {
            throw new JWTException(ResultCodeEnum.INVALID_PARAM, "Token is incorrect");
        }

        // 检查输入的用户是否存在
        String phone = JWTUtil.getValue(token);
        if (!userRepository.existsByPhone(phone)) {
            throw new SecondHandCarException(ResultCodeEnum.NOT_FOUND, "User not exist");
        }
        User user = userRepository.findOneByPhone(phone);

        // 检查该id是否被此用户拥有
        if (!user.getNewCarAdIds().contains(id) && !user.getSecondHandCarAdIds().contains(id)
        && !user.getNewMotorAdIds().contains(id) && !user.getSecondHandMotorAdIds().contains(id)) {
            throw new SecondHandCarException(ResultCodeEnum.NOT_FOUND, "User not own this vehicle");
        }
        // 根据id获取载具和repository
        VehicleAd vehicleAd = getVehicleById(id);
        MongoRepository repository = getRepository(vehicleAd.getVehicleAdType());

        // 如果上传视频，需要判断是否不为标准广告
        if (fileType == FileTypeEnum.VIDEO) {
            if (vehicleAd.getAdLevel() == AdLevelEnum.STANDARD) {
                throw new VehicleException(ResultCodeEnum.FAILED, "Not elite ad cannot upload videos");
            }
        }
        // 检查是否处于已支付状态，在其他状态不允许上传文件
        if (!vehicleAd.getAdStatus().equals(AdStatusEnum.PAID)) {
            throw new VehicleException(ResultCodeEnum.FAILED, "Cannot upload files after posted");
        }
        // 检查是否已经上传过文件，上传完成后不允许修改
        if (!checkIfUpload(id, fileType)) {
            throw new VehicleException(ResultCodeEnum.FAILED,
                    "Already finish upload " + fileType.toString().toLowerCase());
        }
        // 对传入的文件进行校验
        validateFiles(files, fileType);
        // 通过校验，对文件进行压缩
        ArrayList<ByteArrayOutputStream> scaledFiles = new ArrayList<>();
        if (fileType == FileTypeEnum.IMAGE) {
            // 压缩图片
            for (MultipartFile file : files) {
                // 根据不同大小设置不同比例的压缩
                Double scale = null;
                long fileSize = file.getSize();
                if (fileSize > 4000000 && fileSize <= 5000000) {
                    scale = 0.05;
                } else if (fileSize > 2000000 && fileSize <= 4000000) {
                    scale = 0.1;
                } else if (fileSize > 1000000 && fileSize <= 2000000) {
                    scale = 0.5;
                } else if (fileSize <= 1000000) {
                    scale = 1.0;
                }
                //// 图片的压缩，统一转化为jpg格式
                //BufferedImage scaledImage = Thumbnails.of(ImageIO.read(file.getInputStream()))
                //        .scale(scale).outputQuality(1)
                //        .outputFormat("jpg")
                //        .asBufferedImage();
                // 图片的压缩
                BufferedImage scaledImage = Thumbnails.of(ImageIO.read(file.getInputStream()))
                        .scale(scale).outputQuality(1)
                        .asBufferedImage();
                // 将图片转化格式并存入列表
                ByteArrayOutputStream byteImage = new ByteArrayOutputStream();
                ImageIO.write(scaledImage, "jpg", byteImage);
                byteImage.flush();
                //scaledFiles.add(new MockMultipartFile(file.getName(), byteImage.toByteArray()));
                scaledFiles.add(byteImage);
            }
        }

        // 全部校验通过，开始上传文件，将所有文件保存路径存到list中
        ArrayList<String> paths = new ArrayList<>();
        int num = 1;
        for (ByteArrayOutputStream file : scaledFiles) {
            //String oldName = file.getOriginalFilename();
            //String fileName =
            //        UUID.randomUUID().toString() + oldName.substring(oldName.lastIndexOf("."));
            String fileName = UUID.randomUUID().toString() + ".jpg";

            VehicleAdTypeEnum vate = getVehicleById(id).getVehicleAdType();

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
            String bucketName = "";
            if (fileType == FileTypeEnum.IMAGE) {
                bucketName = "easybuysell-images";
            } else if (fileType == FileTypeEnum.VIDEO) {
                bucketName = "easybuysell-videos";
            }
            // 文件前缀
            System.out.println(vate);
            String keyPrefix = "";
            if(vate.equals(VehicleAdTypeEnum.NEWCARAD)){
                keyPrefix = "newCar/";
            }else if(vate.equals(VehicleAdTypeEnum.SECONDHANDCARAD)){
                keyPrefix = "secondHandCar/";
            }else if(vate.equals(VehicleAdTypeEnum.NEWMOTORAD)){
                keyPrefix = "newMotor/";
            }else if(vate.equals(VehicleAdTypeEnum.SECONDHANDMOTORAD)){
                keyPrefix = "secondHandMotor/";
            }
            //else {
            //    keyPrefix = "imgs/exampleCar/";
            //}

            // 从STS服务获取临时访问凭证后，通过临时访问密钥和安全令牌生成OSSClient。
            // 创建OSSClient实例。
            OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret, securityToken);

            String objectName = keyPrefix + id + "/" + num + "-" + fileName;
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName,
                    objectName,
                    new ByteArrayInputStream(file.toByteArray()));

            ossClient.putObject(putObjectRequest);

            paths.add(objectName);
            num += 1;
        }

        //// 文件上传完毕后，将所有文件路径保存至数据库中的对应载具广告
        //if (fileType == FileTypeEnum.IMAGE) {
        //    vehicleAd.setImages(paths);
        //} else if (fileType == FileTypeEnum.VIDEO) {
        //    vehicleAd.setVideos(paths);
        //}
        // 检查是否上传是否已经全部完成
        if (vehicleAd.getAdLevel() != AdLevelEnum.ELITE) {
            // 不是精英广告时，上传完图片即完成
            vehicleAd.setAdStatus(AdStatusEnum.POSTED);
        } else {
            // 是精英广告时，必须上传完视频才能完成
            if (fileType == FileTypeEnum.VIDEO) {
                vehicleAd.setAdStatus(AdStatusEnum.POSTED);
            }
        }
        repository.save(vehicleAd);
    }

    @Override
    public Boolean checkIfUpload(ObjectId id, FileTypeEnum filesType) {
        // 获取id对应的载具
        VehicleAd vehicleAd = getVehicleById(id);

        // 根据不同类型的文件检测
        switch (filesType) {
            case IMAGE:
                if (vehicleAd.getImages() != null) {
                    return false;
                }
                break;
            case VIDEO:
                if (vehicleAd.getVideos() != null) {
                    return false;
                }
                break;
        }

        return true;
    }

    @Override
    public CarPriceLevelEnum getCarPriceLevel(long price) {
        // 根据传入的价格判断价格等级
        if (price == 0) {
            throw new VehicleException(ResultCodeEnum.INVALID_PARAM, "Price cannot be 0");
        }
        CarPriceLevelEnum priceLevel = null;
        for (CarPriceLevelEnum priceEnum : CarPriceLevelEnum.values()) {
            if (price >= priceEnum.getMinPrice() && price <= priceEnum.getMaxPrice()) {
                priceLevel = priceEnum;
            }
        }
        if (priceLevel == null) {
            throw new VehicleException(ResultCodeEnum.INVALID_PARAM, "Price not correct");
        }
        return priceLevel;
    }

    @Override
    public MotorPriceLevelEnum getMotorPriceLevel(long price) {
        // 根据传入的价格判断价格等级
        if (price == 0) {
            throw new VehicleException(ResultCodeEnum.INVALID_PARAM, "Price cannot be 0");
        }
        MotorPriceLevelEnum priceLevel = null;
        for (MotorPriceLevelEnum priceEnum : MotorPriceLevelEnum.values()) {
            if (price >= priceEnum.getMinPrice() && price <= priceEnum.getMaxPrice()) {
                priceLevel = priceEnum;
            }
        }
        if (priceLevel == null) {
            throw new VehicleException(ResultCodeEnum.INVALID_PARAM, "Price not correct");
        }
        return priceLevel;
    }

    @Override
    public long getAdPrice(AdLevelEnum adLevel, long price, VehicleAdTypeEnum type) {
        long adPrice = 0;

        if (type == VehicleAdTypeEnum.NEWCARAD || type == VehicleAdTypeEnum.SECONDHANDCARAD) {
            // 获取广告价格等级
            CarPriceLevelEnum priceLevel = getCarPriceLevel(price);
            switch (adLevel) {
                case STANDARD:
                    adPrice = carPriceConfig.getStandard().get(priceLevel.toString().toLowerCase());
                    break;
                case ADVANCED:
                    adPrice = carPriceConfig.getAdvanced().get(priceLevel.toString().toLowerCase());
                    break;
                case ELITE:
                    adPrice = carPriceConfig.getElite().get(priceLevel.toString().toLowerCase());
                    break;
            }
        } else if (type == VehicleAdTypeEnum.NEWMOTORAD || type == VehicleAdTypeEnum.SECONDHANDMOTORAD) {
            // 获取广告价格等级
            MotorPriceLevelEnum priceLevel = getMotorPriceLevel(price);
            switch (adLevel) {
                case STANDARD:
                    adPrice = motorPriceConfig.getStandard().get(priceLevel.toString().toLowerCase());
                    break;
                case ADVANCED:
                    adPrice = motorPriceConfig.getAdvanced().get(priceLevel.toString().toLowerCase());
                    break;
                case ELITE:
                    adPrice = motorPriceConfig.getElite().get(priceLevel.toString().toLowerCase());
                    break;
            }
        }

        return adPrice;
    }

    @Override
    public void changePrice(String token, ObjectId id, VehicleAdTypeEnum type, long price) {
        // 校验token的合法性
        if (!token.startsWith("Bearer ")) {
            throw new JWTException(ResultCodeEnum.INVALID_PARAM, "Token is incorrect");
        }

        // 检查输入的用户是否存在并获取
        String phone = JWTUtil.getValue(token);
        if (!userRepository.existsByPhone(phone)) {
            throw new SecondHandCarException(ResultCodeEnum.NOT_FOUND, "User not exist");
        }
        User user = userRepository.findOneByPhone(phone);

        // 获取载具
        MongoRepository repository = getRepository(type);
        VehicleAd vehicleAd = (VehicleAd) repository.findById(id).get();

        // 检查是否超过最高价格
        long maxPrice = vehicleAd.getOriginalPrice() * (1 + realPricePercent / 100);
        if (price > maxPrice || price < 0) {
            throw new SecondHandCarException(ResultCodeEnum.FAILED, "Over max price");
        }

        vehicleAd.setPrice(price);
        repository.save(vehicleAd);
    }

    @Override
    public boolean getFavouriteState(ObjectId id, String phone) {
        // 检查id对应载具是否存在
        getVehicleById(id);

        // 判断手机号的有效性，即数据库中是否存在该账号
        if(!userRepository.existsByPhone(phone)){
            throw new VehicleException(ResultCodeEnum.NOT_FOUND, "this phone is not in the database");
        }
        // 通过载具类型和id获得缓存中对应的的key
        String carKey = "records:favorites:" + id;
        // 如果缓存中存在目标key，则取得value判断其内部是否包含参数中指定的phone
        if(redisUtil.hasKey(carKey)){
            System.out.println(4);
            ArrayList<String> list = new ArrayList<>(redisUtil.sGet(carKey));
            return list.contains(phone);
        }else {
            // 如果缓存中不包含key，且是新车广告
            if(newCarAdRepository.existsById(id)){
                NewCarAd curNewCar = newCarAdRepository.findOneById(id);
                ArrayList<String> list = curNewCar.getFavoriteUsers();
                if(list == null){
                    return false;
                }
                for(String curPhone : list){
                    if(curPhone.equals(phone)){
                        return true;
                    }
                }
            }
            // 如果缓存中不包含key，且是二手车广告
            if(secondHandCarAdRepository.existsById(id)){
                SecondHandCarAd curSecondHandCarAd = secondHandCarAdRepository.findOneById(id);
                ArrayList<String> list = curSecondHandCarAd.getFavoriteUsers();
                if(list == null){
                    return false;
                }
                for(String curPhone : list){
                    if(curPhone.equals(phone)){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public ArrayList<URL> getImagesFromOSSById(ObjectId id) {
        // 返回结果存放
        ArrayList<URL> imageUrls = new ArrayList<>();
        // 具体图片文件路径集合
        ArrayList<String> objectNames = new ArrayList<>();
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
        if(newCarAdRepository.existsById(id)){
            keyPrefix = "newCar/" + id;
        }else if(secondHandCarAdRepository.existsById(id)){
            keyPrefix = "secondHandCar/" + id;
        }else if(newMotorAdRepository.existsById(id)){
            keyPrefix = "newMotor/" + id;
        }else if(secondHandMotorAdRepository.existsById(id)){
            keyPrefix = "secondHandMotor/" + id;
        }

        // 从STS服务获取临时访问凭证后，通过临时访问密钥和安全令牌生成OSSClient。
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret, securityToken);

        try {
            //则列举包含指定前缀的文件。
            ObjectListing objectListing = ossClient.listObjects(bucketName, keyPrefix);
            List<OSSObjectSummary> sums = objectListing.getObjectSummaries();
            for (OSSObjectSummary s : sums) {
                // 如果和前缀名重合，跳过，说明是目录（keyPrefix），不进行存储
                if(s.getKey().equals(keyPrefix + "/")){
                    continue;
                }
                objectNames.add(s.getKey());
            }

            // 设置url过期时间，目前为3600s，即1h
            Date expiration = new Date(System.currentTimeMillis() + 3600 * 1000);
            // 生成以GET方法访问的签名URL，访客可以直接通过浏览器访问相关内容。
            for(String objectName : objectNames){
                // 使用generatePresignedUrl接口得到待签名的图片url
                URL url = ossClient.generatePresignedUrl(bucketName, objectName, expiration);
                imageUrls.add(url);
            }
            return imageUrls;
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
        return imageUrls;
    }

    @Override
    public URL getFirstImageFromOSSById(ObjectId id) {
        // 具体图片文件路径集合
        ArrayList<String> objectNames = new ArrayList<>();
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
        if(newCarAdRepository.existsById(id)){
            keyPrefix = "newCar/" + id;
        }else if(secondHandCarAdRepository.existsById(id)){
            keyPrefix = "secondHandCar/" + id;
        }else if(newMotorAdRepository.existsById(id)){
            keyPrefix = "newMotor/" + id;
        }else if(secondHandMotorAdRepository.existsById(id)){
            keyPrefix = "secondHandMotor/" + id;
        }

        // 从STS服务获取临时访问凭证后，通过临时访问密钥和安全令牌生成OSSClient。
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret, securityToken);

        try {
            //则列举包含指定前缀的文件。
            ObjectListing objectListing = ossClient.listObjects(bucketName, keyPrefix);
            List<OSSObjectSummary> sums = objectListing.getObjectSummaries();
            for (OSSObjectSummary s : sums) {
                // 如果和前缀名重合，跳过，说明是目录（keyPrefix），不进行存储
                if(s.getKey().equals(keyPrefix + "/")){
                    continue;
                }
                objectNames.add(s.getKey());
            }

            // 设置url过期时间，目前为3600s，即1h
            Date expiration = new Date(System.currentTimeMillis() + 3600 * 1000);
            // 生成以GET方法访问的签名URL，访客可以直接通过浏览器访问相关内容。

            return ossClient.generatePresignedUrl(bucketName, objectNames.get(0), expiration);
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
        return null;
    }

    @Override
    public List<Object> getFilterCars(ArrayList<String> conditions, int pageNumber) {
        // 返回的车辆结果集
        List<TempVehicleAd> rsCars = new ArrayList<>();
        // 返回的最终结果，包含页码总数，用于传给前端进行分页操作
        List<Object> rs = new ArrayList<>();
        // 得到所有的载具集合
        List<NewCarAd> newCars = newCarAdRepository.findAllByAdStatus(AdStatusEnum.POSTED);
        List<SecondHandCarAd> secondCars = secondHandCarAdRepository.findAllByAdStatus(AdStatusEnum.POSTED);
        List<VehicleAd> tempCars = new ArrayList<>();
        tempCars.addAll(newCars);
        tempCars.addAll(secondCars);
        List<TempVehicleAd> cars = new ArrayList<>();
        // 先拼接需求的排序和筛选属性
        for(VehicleAd car : tempCars){
            TempVehicleAd temp = new TempVehicleAd();
            temp.setId(car.getId());
            temp.setProvince(car.getProvince());
            temp.setCity(car.getCity());
            temp.setCounty(car.getCounty());
            temp.setTown(car.getTown());
            temp.setPrice(car.getPrice());
            temp.setExampleVehicleId(car.getExampleVehicleId());
            temp.setKilometers(car.getKilometers());
            temp.setVehicleAdType(car.getVehicleAdType());
            temp.setIfAccident(car.getIfAccident());
            temp.setIfShippingDamage(car.getIfShippingDamage());
            temp.setIfStock(car.getIfStock());

            ExampleCar cur = exampleCarRepository.findOneById(car.getExampleVehicleId());
            temp.setSeries(cur.getSeries());
            temp.setMaximumHorsepower(cur.getMaximumHorsepower());
            temp.setMaximumTorque(cur.getMaximumTorque());
            temp.setYear(cur.getYear().substring(0, 4));
            temp.setEnergy(cur.getEnergy());

            cars.add(temp);
        }
        // 排序不为空
        if(!Objects.equals(conditions.get(0), "")){
            cars = sortCars(cars, conditions.get(0));
        }
        // 如果价格不为空
        if(!Objects.equals(conditions.get(1), "") && !Objects.equals(conditions.get(2), "")){
            cars = priceCars(cars, Long.parseLong(conditions.get(1)), Long.parseLong(conditions.get(2)));
        }
        // 如果位置不为空
        if(!Objects.equals(conditions.get(3), "")){
            cars = locationCars(cars, conditions.get(3));
        }
        // 如果模型不为空
        if(!Objects.equals(conditions.get(4), "")){
            cars = brandCars(cars, conditions.get(4));
        }
        // 如果公里数不为空
        if(!Objects.equals(conditions.get(5), "") && !Objects.equals(conditions.get(6), "")){
            cars = mileCars(cars, Long.parseLong(conditions.get(5)), Long.parseLong(conditions.get(6)));
        }
        // 如果年份不为空
        if(!Objects.equals(conditions.get(7), "") && !Objects.equals(conditions.get(8), "")){
            cars = yearCars(cars, conditions.get(7), conditions.get(8));
        }
        // 如果能源不为空
        if(!Objects.equals(conditions.get(9), "")){
            cars = energyCars(cars, conditions.get(9));
        }
        // 如果新旧不为空
        if(!Objects.equals(conditions.get(10), "")){
            cars = oldnewCars(cars, conditions.get(10));
        }
        // 如果事故选项不为空
        if(!Objects.equals(conditions.get(11), "")){
            cars = accidentCars(cars, conditions.get(11));
        }
        // 如果运损选项不为空
        if(!Objects.equals(conditions.get(12), "")){
            cars = shipDamageCars(cars, conditions.get(12));
        }
        // 如果库存选项不为空
        if(!Objects.equals(conditions.get(13), "")){
            cars = stackCars(cars, conditions.get(13));
        }
        for(int i = (pageNumber - 1) * pageSize; i < (pageNumber - 1) * pageSize + pageSize; i++){
            if(i > cars.size() - 1){
                break;
            }
            rsCars.add(cars.get(i));
            // 增加浏览数
            view(cars.get(i).getId());
        }
        // 拼接当前页面车辆的其他需求属性
        for(TempVehicleAd car : rsCars){
            VehicleAd temp = getVehicleById(car.getId());
            car.setUserId(temp.getUserId());
            //car.setPriceLevel(temp.getPriceLevel());
            car.setAdLevel(temp.getAdLevel());
            car.setRealPrice(temp.getRealPrice());
            car.setViews(temp.getViews());
            car.setClicks(temp.getClicks());
            car.setFavoriteUsers(temp.getFavoriteUsers());
            car.setImages(temp.getImages());
            car.setVideos(temp.getVideos());
            car.setDescription(temp.getDescription());
            car.setCreatedDate(temp.getCreatedDate());
            car.setAdStatus(temp.getAdStatus());
            car.setCancelAdReason(temp.getCancelAdReason());

            ExampleCar cur = exampleCarRepository.findOneById(car.getExampleVehicleId());
            car.setModel(cur.getModel());
            car.setLevel(cur.getLevel());
            car.setEngine(cur.getEngine());
            car.setGearbox(cur.getGearbox());
            car.setBrand(cur.getBrand());
            car.setFullYear(cur.getYear());

            car.setCompanyName(userRepository.findOneById(car.getUserId()).getCompanyName());

//            System.out.println("当前加载的车辆为： " + car.getSeries());
            // 从OSS服务器中获取到封面地址
            car.setCoverImage(getFirstImageFromOSSById(car.getId()).toString());
        }
        rs.add(rsCars);
        rs.add(cars.size());
        rs.add(pageSize);
        return rs;
    }

    @Override
    public List<Object> getFilterMotors(ArrayList<String> conditions, int pageNumber) {
        // 返回的车辆结果集
        List<TempVehicleAd> rsMotors = new ArrayList<>();
        // 返回的最终结果，包含页码总数，用于传给前端进行分页操作
        List<Object> rs = new ArrayList<>();
        // 得到所有的载具集合
        List<NewMotorAd> newMotors = newMotorAdRepository.findAllByAdStatus(AdStatusEnum.POSTED);
        List<SecondHandMotorAd> secondHandMotors = secondHandMotorAdRepository.findAllByAdStatus(AdStatusEnum.POSTED);
        List<VehicleAd> tempMotors = new ArrayList<>();
        tempMotors.addAll(newMotors);
        tempMotors.addAll(secondHandMotors);
        List<TempVehicleAd> motors = new ArrayList<>();
        // 先拼接需求的排序和筛选属性
        for(VehicleAd motor : tempMotors){
            TempVehicleAd temp = new TempVehicleAd();
            temp.setId(motor.getId());
            temp.setProvince(motor.getProvince());
            temp.setCity(motor.getCity());
            temp.setCounty(motor.getCounty());
            temp.setTown(motor.getTown());
            temp.setPrice(motor.getPrice());
            temp.setExampleVehicleId(motor.getExampleVehicleId());
            temp.setKilometers(motor.getKilometers());
            temp.setVehicleAdType(motor.getVehicleAdType());
            ExampleMotor cur = exampleMotorRepository.findOneById(motor.getExampleVehicleId());
            temp.setSeries(cur.getSeries());
            temp.setMaximumHorsepower(cur.getMaximumHorsepower());
            temp.setMaximumSpeed(cur.getMaximumSpeed());
            temp.setIsImport(cur.getIsImport());
            temp.setEnergy(cur.getEnergy());
            temp.setLifeMile(cur.getLifeMile());
            temp.setDisplacement(cur.getDisplacement());
            temp.setType(cur.getType());
            temp.setIfAccident(motor.getIfAccident());
            temp.setIfShippingDamage(motor.getIfShippingDamage());
            temp.setIfStock(motor.getIfStock());

            motors.add(temp);
        }
        // 排序不为空
        if(!Objects.equals(conditions.get(0), "")){
            motors = sortCars(motors, conditions.get(0));
        }
        // 如果价格不为空
        if(!Objects.equals(conditions.get(1), "") && !Objects.equals(conditions.get(2), "")){
            motors = priceCars(motors, Long.parseLong(conditions.get(1)), Long.parseLong(conditions.get(2)));
        }
        // 如果位置不为空
        if(!Objects.equals(conditions.get(3), "")){
            motors = locationCars(motors, conditions.get(3));
        }
        // 如果模型不为空
        if(!Objects.equals(conditions.get(4), "")){
            motors = brandCars(motors, conditions.get(4));
        }
        // 如果公里数不为空
        if(!Objects.equals(conditions.get(5), "") && !Objects.equals(conditions.get(6), "")){
            motors = mileCars(motors, Long.parseLong(conditions.get(5)), Long.parseLong(conditions.get(6)));
        }
        // 如果能源不为空
        if(!Objects.equals(conditions.get(7), "")){
            motors = energyCars(motors, conditions.get(7));
        }
        // 如果新旧不为空
        if(!Objects.equals(conditions.get(8), "")){
            motors = oldnewMotors(motors, conditions.get(8));
        }
        // 如果车型不为空
        if(!Objects.equals(conditions.get(9), "")){
            motors = typeMotors(motors, conditions.get(9));
        }
        // 如果进口不为空
        if(!Objects.equals(conditions.get(10), "")){
            motors = isImportMotors(motors, conditions.get(10));
        }
        // 如果事故选项不为空
        if(!Objects.equals(conditions.get(11), "")){
            motors = accidentMotors(motors, conditions.get(11));
        }
        // 如果运损车选项不为空
        if(!Objects.equals(conditions.get(12), "")){
            motors = shipDamageMotors(motors, conditions.get(12));
        }
        // 如果库存车选项不为空
        if(!Objects.equals(conditions.get(13), "")){
            motors = stackMotors(motors, conditions.get(13));
        }

        for(int i = (pageNumber - 1) * pageSize; i < (pageNumber - 1) * pageSize + pageSize; i++){
            if(i > motors.size() - 1){
                break;
            }
            rsMotors.add(motors.get(i));
            // 增加浏览数
            view(motors.get(i).getId());
        }
        // 拼接当前页面车辆的其他需求属性
        for(TempVehicleAd motor : rsMotors){
            VehicleAd temp = getVehicleById(motor.getId());
            motor.setUserId(temp.getUserId());
            //motor.setPriceLevel(temp.getPriceLevel());
            motor.setAdLevel(temp.getAdLevel());
            motor.setRealPrice(temp.getRealPrice());
            motor.setViews(temp.getViews());
            motor.setClicks(temp.getClicks());
            motor.setFavoriteUsers(temp.getFavoriteUsers());
            motor.setImages(temp.getImages());
            motor.setVideos(temp.getVideos());
            motor.setDescription(temp.getDescription());
            motor.setCreatedDate(temp.getCreatedDate());
            motor.setAdStatus(temp.getAdStatus());
            motor.setCancelAdReason(temp.getCancelAdReason());

            ExampleMotor cur = exampleMotorRepository.findOneById(motor.getExampleVehicleId());
            motor.setModel(cur.getModel());
            motor.setEngine(cur.getEngine());
            motor.setGearbox(cur.getGearbox());
            motor.setBrand(cur.getBrand());

            motor.setCompanyName(userRepository.findOneById(motor.getUserId()).getCompanyName());

            // System.out.println("当前加载的车辆为： " + motor.getSeries());
            // 从OSS服务器中获取到封面地址
            motor.setCoverImage(getFirstImageFromOSSById(motor.getId()).toString());
        }
        rs.add(motors);
        rs.add(rsMotors);
        rs.add(motors.size());
        rs.add(pageSize);
        return rs;
    }

//    @Override
//    public String getVehicleType(ObjectId id) {
//        String type = "";
//        if(newCarAdRepository.existsById(id) || secondHandCarAdRepository.existsById(id)){
//            type = "car";
//        }else if(newMotorAdRepository.existsById(id) || secondHandMotorAdRepository.existsById(id)){
//            type = "motor";
//        }
//        return type;
//    }


    /**
     * 排序载具（通用）
     *
     * @param temp       临时车辆数据
     * @param sortMethod 排序方法
     * @return {@link List}<{@link TempVehicleAd}>
     */
    private List<TempVehicleAd> sortCars(List<TempVehicleAd> temp, String sortMethod){
        List<TempVehicleAd> rs = new ArrayList<>();
        if (Objects.equals(sortMethod, "priceLH")) {
            temp.sort(Comparator.comparing(TempVehicleAd::getPrice));
        }
        if (Objects.equals(sortMethod, "kilometersLH")) {
            temp.sort(Comparator.comparing(TempVehicleAd::getKilometers));
        }
        if (Objects.equals(sortMethod, "maximumHorsepowerLH")) {
            temp.sort(Comparator.comparing(TempVehicleAd::getMaximumHorsepower));
        }
        if (Objects.equals(sortMethod, "maximumTorqueLH")) {
            temp.sort(Comparator.comparing(TempVehicleAd::getMaximumTorque));
        }
        if (Objects.equals(sortMethod, "yearLH")) {
            temp.sort(Comparator.comparing(TempVehicleAd::getYear));
        }
        if (Objects.equals(sortMethod, "priceHL")) {
            temp.sort(Comparator.comparing(TempVehicleAd::getPrice).reversed());
        }
        if (Objects.equals(sortMethod, "kilometersHL")) {
            temp.sort(Comparator.comparing(TempVehicleAd::getKilometers).reversed());
        }
        if (Objects.equals(sortMethod, "maximumHorsepowerHL")) {
            temp.sort(Comparator.comparing(TempVehicleAd::getMaximumHorsepower).reversed());
        }
        if (Objects.equals(sortMethod, "maximumTorqueHL")) {
            temp.sort(Comparator.comparing(TempVehicleAd::getMaximumTorque).reversed());
        }
        if (Objects.equals(sortMethod, "yearHL")) {
            temp.sort(Comparator.comparing(TempVehicleAd::getYear).reversed());
        }
        if(Objects.equals(sortMethod, "maximumSpeedLH")){
            temp.sort(Comparator.comparing(TempVehicleAd::getMaximumSpeed));
        }
        if(Objects.equals(sortMethod, "maximumSpeedHL")){
            temp.sort(Comparator.comparing(TempVehicleAd::getMaximumSpeed).reversed());
        }
        if(Objects.equals(sortMethod, "lifeMileLH")){
            temp.sort(Comparator.comparing(TempVehicleAd::getLifeMile));
        }
        if(Objects.equals(sortMethod, "lifeMileHL")){
            temp.sort(Comparator.comparing(TempVehicleAd::getLifeMile).reversed());
        }
        if(Objects.equals(sortMethod, "displacementLH")){
            temp.sort(Comparator.comparing(TempVehicleAd::getDisplacement));
        }
        if(Objects.equals(sortMethod, "displacementHL")){
            temp.sort(Comparator.comparing(TempVehicleAd::getDisplacement).reversed());
        }
        rs = temp;
        return rs;
    }

    /**
     * 价格筛选（通用）
     *
     * @param temp  临时车辆数据
     * @param start 起始价格
     * @param end   终止价格
     * @return {@link List}<{@link TempVehicleAd}>
     */
    private List<TempVehicleAd> priceCars(List<TempVehicleAd> temp, long start, long end){
        List<TempVehicleAd> rs = new ArrayList<>();
        for(TempVehicleAd car : temp){
            if(car.getPrice() >= start && car.getPrice() <= end){
                rs.add(car);
            }
        }
        return rs;
    }

    /**
     * 位置筛选（通用）
     *
     * @param temp     临时车辆数据
     * @param location 位置
     * @return {@link List}<{@link TempVehicleAd}>
     */
    private List<TempVehicleAd> locationCars(List<TempVehicleAd> temp, String location){
        List<TempVehicleAd> rs = new ArrayList<>();
        for(TempVehicleAd car : temp){
            if((car.getProvince() + car.getCity() + car.getCounty() + car.getTown()).equals(location)){
                rs.add(car);
            }
        }
        return rs;
    }

    /**
     * 品牌车系筛选（通用）
     *
     * @param temp  临时车辆数据
     * @param series 品牌车系名
     * @return {@link List}<{@link TempVehicleAd}>
     */
    private List<TempVehicleAd> brandCars(List<TempVehicleAd> temp, String series){
        List<TempVehicleAd> rs = new ArrayList<>();
        for(TempVehicleAd car : temp){
            if(car.getSeries().equals(series)){
                rs.add(car);
            }
        }
        return rs;
    }

    /**
     * 公里数筛选（通用）
     *
     * @param temp  临时车辆数据
     * @param start 起始公里数
     * @param end   终止公里数
     * @return {@link List}<{@link TempVehicleAd}>
     */
    private List<TempVehicleAd> mileCars(List<TempVehicleAd> temp, long start, long end){
        List<TempVehicleAd> rs = new ArrayList<>();
        for(TempVehicleAd car : temp){
            if(car.getKilometers() >= start && car.getKilometers() <= end){
                rs.add(car);
            }
        }
        return rs;
    }

    /**
     * 年份筛选（通用）
     *
     * @param temp  临时车辆数据
     * @param start 起始年份
     * @param end   终止年份
     * @return {@link List}<{@link TempVehicleAd}>
     */
    private List<TempVehicleAd> yearCars(List<TempVehicleAd> temp, String start, String end){
        List<TempVehicleAd> rs = new ArrayList<>();
        for(TempVehicleAd car : temp){
            if(Integer.parseInt(car.getYear()) >= Integer.parseInt(start.substring(0, 4))
                    && Integer.parseInt(car.getYear()) <= Integer.parseInt(end.substring(0, 4))){
                rs.add(car);
            }
        }
        return rs;
    }

    /**
     * 能源筛选（通用）
     *
     * @param temp   临时汽车数据
     * @param energy 能源
     * @return {@link List}<{@link TempVehicleAd}>
     */
    private List<TempVehicleAd> energyCars(List<TempVehicleAd> temp, String energy){
        List<TempVehicleAd> rs = new ArrayList<>();
        for(TempVehicleAd car : temp){
            if(car.getEnergy().equals(energy)){
                rs.add(car);
            }
        }
        return rs;
    }

    /**
     * 新旧筛选（汽车）
     *
     * @param temp   临时汽车数据
     * @param oldNew 新旧
     * @return {@link List}<{@link TempVehicleAd}>
     */
    private List<TempVehicleAd> oldnewCars(List<TempVehicleAd> temp, String oldNew){
        List<TempVehicleAd> rs = new ArrayList<>();
        VehicleAdTypeEnum compare;
        if(oldNew.equals("新车")){
            compare = VehicleAdTypeEnum.NEWCARAD;
        }else if(oldNew.equals("二手车")){
            compare = VehicleAdTypeEnum.SECONDHANDCARAD;
        }else {
            compare = VehicleAdTypeEnum.EXAMPLECAR;
        }
        for(TempVehicleAd car : temp){
            if(car.getVehicleAdType().equals(compare)){
                rs.add(car);
            }
        }
        return rs;
    }

    /**
     * 事故车筛选（汽车）
     *
     * @param temp   临时汽车数据
     * @param accident 事故车
     * @return {@link List}<{@link TempVehicleAd}>
     */
    private List<TempVehicleAd> accidentCars(List<TempVehicleAd> temp, String accident){
        List<TempVehicleAd> rs = new ArrayList<>();
        boolean compare = false;
        if(accident.equals("事故车")){
            compare = true;
        }
        for(TempVehicleAd car : temp){
            if(car.getIfAccident() == compare){
                rs.add(car);
            }
        }
        return rs;
    }

    /**
     * 运损车筛选（汽车）
     *
     * @param temp   临时汽车数据
     * @param shipDamage 运损车
     * @return {@link List}<{@link TempVehicleAd}>
     */
    private List<TempVehicleAd> shipDamageCars(List<TempVehicleAd> temp, String shipDamage){
        List<TempVehicleAd> rs = new ArrayList<>();
        boolean compare = false;
        if(shipDamage.equals("运损车")){
            compare = true;
        }
        for(TempVehicleAd car : temp){
            if(car.getIfShippingDamage() == compare){
                rs.add(car);
            }
        }
        return rs;
    }

    /**
     * 筛选车型（摩托车）
     *
     * @param temp 临时数据
     * @param type 车型名称
     * @return {@link List}<{@link TempVehicleAd}>
     */
    private List<TempVehicleAd> typeMotors(List<TempVehicleAd> temp, String type){
        List<TempVehicleAd> rs = new ArrayList<>();
        for(TempVehicleAd car : temp){
            if(car.getType().equals(type)){
                rs.add(car);
            }
        }
        return rs;
    }


    /**
     * 筛选进出口类型（摩托车）
     *
     * @param temp     临时数据
     * @param isImport 进出口数据
     * @return {@link List}<{@link TempVehicleAd}>
     */
    private List<TempVehicleAd> isImportMotors(List<TempVehicleAd> temp, String isImport){
        List<TempVehicleAd> rs = new ArrayList<>();
        for(TempVehicleAd car : temp){
            if(car.getIsImport().equals(isImport)){
                rs.add(car);
            }
        }
        return rs;
    }

    /**
     * 新旧筛选（摩托车）
     *
     * @param temp   临时汽车数据
     * @param oldNew 新旧
     * @return {@link List}<{@link TempVehicleAd}>
     */
    private List<TempVehicleAd> oldnewMotors(List<TempVehicleAd> temp, String oldNew){
        List<TempVehicleAd> rs = new ArrayList<>();
        VehicleAdTypeEnum compare;
        if(oldNew.equals("新车")){
            compare = VehicleAdTypeEnum.NEWMOTORAD;
        }else if(oldNew.equals("二手车")){
            compare = VehicleAdTypeEnum.SECONDHANDMOTORAD;
        }else {
            compare = VehicleAdTypeEnum.EXAMPLEMOTOR;
        }
        for(TempVehicleAd car : temp){
            if(car.getVehicleAdType().equals(compare)){
                rs.add(car);
            }
        }
        return rs;
    }

    /**
     * 事故车筛选（摩托车）
     *
     * @param temp   临时摩托车数据
     * @param accident 事故车
     * @return {@link List}<{@link TempVehicleAd}>
     */
    private List<TempVehicleAd> accidentMotors(List<TempVehicleAd> temp, String accident){
        List<TempVehicleAd> rs = new ArrayList<>();
        boolean compare = false;
        if(accident.equals("事故车")){
            compare = true;
        }
        for(TempVehicleAd motor : temp){
            if(motor.getIfAccident() == compare){
                rs.add(motor);
            }
        }
        return rs;
    }

    /**
     * 运损车筛选（摩托车）
     *
     * @param temp   临时摩托车数据
     * @param shipDamage 运损车
     * @return {@link List}<{@link TempVehicleAd}>
     */
    private List<TempVehicleAd> shipDamageMotors(List<TempVehicleAd> temp, String shipDamage){
        List<TempVehicleAd> rs = new ArrayList<>();
        boolean compare = false;
        if(shipDamage.equals("运损车")){
            compare = true;
        }
        for(TempVehicleAd motor : temp){
            if(motor.getIfShippingDamage() == compare){
                rs.add(motor);
            }
        }
        return rs;
    }

    /**
     * @description: 内部方法，对传入的多个文件进行校验大小、格式、
     * @param {MultipartFile[]} files 需要校验的文件，可以为多个文件
     * @param {FileTypeEnum} fileType 文件的类型
     * @return {*}
     */
    private void validateFiles(MultipartFile[] files, FileTypeEnum fileType) {
        // 根据数据类型，设置对应的最大尺寸和最大数量和限制格式，详见配置文件application.properties
        int maxNum = 0;
        long maxSize = 0;
        List<String> limitTypes = null;
        switch (fileType) {
            case IMAGE:
                maxNum = imageMaxNum;
                maxSize = imageMaxSize;
                limitTypes = List.of(".png", ".jpg", ".jpeg");
                break;
            case VIDEO:
                maxNum = videoMaxNum;
                maxSize = videoMaxSize;
                limitTypes = List.of(".mp4", ".mkv", ".avi");
                break;
            default:
                throw new VehicleException(ResultCodeEnum.INVALID_PARAM,
                        "File type do not support");
        }

        // 对传入文件数量进行校验
        if (files.length > maxNum) {
            throw new VehicleException(ResultCodeEnum.FAILED, "Cannot input more than max number");
        }

        // 对每个传入文件大小和格式进行校验
        for (MultipartFile file : files) {
            // 校验文件大小
            if (file.getSize() > maxSize) {
                throw new VehicleException(ResultCodeEnum.FAILED, "File size over max size");
            }
            // 校验文件名及格式
            String oldName = file.getOriginalFilename();
            String suffix = oldName.substring(oldName.lastIndexOf("."));
            if (!oldName.contains(".") || !limitTypes.contains(suffix.toLowerCase())) {
                throw new VehicleException(ResultCodeEnum.INVALID_PARAM, "File name incorrect");
            }
        }
    }

    /**
     * @description: 内部方法，根据载具类型获取对应的Repository
     * @param {VehicleAdTypeEnum} type 载具类型
     * @return {MongoRepository} 返回一个Repository，用于获取具体的model信息
     */
    private MongoRepository getRepository(VehicleAdTypeEnum type) {
        switch (type) {
            case NEWCARAD:
                return newCarAdRepository;
            case SECONDHANDCARAD:
                return secondHandCarAdRepository;
            case NEWMOTORAD:
                return newMotorAdRepository;
            case SECONDHANDMOTORAD:
                return secondHandMotorAdRepository;
        }
        return null;
    }

    /**
     * 内部方法，向STS服务请求需求信息（临时keyId和keySecret）
     *
     * @return {@link ArrayList}<{@link String}>
     */
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

    @Override
    public List<Object> getSearchCarModel(String model, int pageNumber){
        // 返回的车辆结果集
        List<TempVehicleAd> rsCars = new ArrayList<>();
        // 返回的最终结果，包含页码总数，用于传给前端进行分页操作
        List<Object> rs = new ArrayList<>();

        List<NewCarAd> newCarAds =  newCarAdRepository.findAllByAdStatus(AdStatusEnum.POSTED);
        List<SecondHandCarAd> secondHandCarAds = secondHandCarAdRepository.findAllByAdStatus(AdStatusEnum.POSTED);
        List<VehicleAd> tempCars = new ArrayList<>();
        tempCars.addAll(newCarAds);
        tempCars.addAll(secondHandCarAds);
        List<TempVehicleAd> cars = new ArrayList<>();

        // 先拼接需求的排序和筛选属性
        for(VehicleAd car : tempCars){
            TempVehicleAd temp = new TempVehicleAd();
            ExampleCar cur = exampleCarRepository.findOneById(car.getExampleVehicleId());

            // 如果模型字符串不是子集字符串，直接跳过本次循环，说明数据不符合要求
            if(!cur.getModel().substring(0, cur.getModel().indexOf("-")).contains(model)){
                continue;
            }

            temp.setId(car.getId());
            temp.setProvince(car.getProvince());
            temp.setCity(car.getCity());
            temp.setCounty(car.getCounty());
            temp.setTown(car.getTown());
            temp.setPrice(car.getPrice());
            temp.setExampleVehicleId(car.getExampleVehicleId());
            temp.setKilometers(car.getKilometers());
            temp.setVehicleAdType(car.getVehicleAdType());
            temp.setIfAccident(car.getIfAccident());
            temp.setIfShippingDamage(car.getIfShippingDamage());

            temp.setSeries(cur.getSeries());
            temp.setMaximumHorsepower(cur.getMaximumHorsepower());
            temp.setMaximumTorque(cur.getMaximumTorque());
            temp.setYear(cur.getYear().substring(0, 4));
            temp.setEnergy(cur.getEnergy());

            cars.add(temp);
        }

        // 分页处理
        for(int i = (pageNumber - 1) * pageSize; i < (pageNumber - 1) * pageSize + pageSize; i++){
            if(i > cars.size() - 1){
                break;
            }
            rsCars.add(cars.get(i));
            // 增加浏览数
            view(cars.get(i).getId());
        }

        // 拼接当前页面车辆的其他需求属性
        for(TempVehicleAd car : rsCars){
            VehicleAd temp = getVehicleById(car.getId());
            car.setUserId(temp.getUserId());
            //car.setPriceLevel(temp.getPriceLevel());
            car.setAdLevel(temp.getAdLevel());
            car.setRealPrice(temp.getRealPrice());
            car.setViews(temp.getViews());
            car.setClicks(temp.getClicks());
            car.setFavoriteUsers(temp.getFavoriteUsers());
            car.setImages(temp.getImages());
            car.setVideos(temp.getVideos());
            car.setDescription(temp.getDescription());
            car.setCreatedDate(temp.getCreatedDate());
            car.setAdStatus(temp.getAdStatus());
            car.setCancelAdReason(temp.getCancelAdReason());

            ExampleCar cur = exampleCarRepository.findOneById(car.getExampleVehicleId());
            car.setModel(cur.getModel());
            car.setLevel(cur.getLevel());
            car.setEngine(cur.getEngine());
            car.setGearbox(cur.getGearbox());
            car.setBrand(cur.getBrand());
            car.setFullYear(cur.getYear());

            car.setCompanyName(userRepository.findOneById(car.getUserId()).getCompanyName());

            // 从OSS服务器中获取到封面地址
            car.setCoverImage(getFirstImageFromOSSById(car.getId()).toString());
        }
        rs.add(rsCars);
        rs.add(cars.size());
        rs.add(pageSize);
        return rs;
    }

    @Override
    public List<Object> getSearchMotorModel(String model, int pageNumber){
        // 返回的车辆结果集
        List<TempVehicleAd> rsMotors = new ArrayList<>();
        // 返回的最终结果，包含页码总数，用于传给前端进行分页操作
        List<Object> rs = new ArrayList<>();

        List<NewMotorAd> newMotorAds =  newMotorAdRepository.findAllByAdStatus(AdStatusEnum.POSTED);
        List<SecondHandMotorAd> secondHandMotorAds = secondHandMotorAdRepository.findAllByAdStatus(AdStatusEnum.POSTED);
        List<VehicleAd> tempMotors = new ArrayList<>();
        tempMotors.addAll(newMotorAds);
        tempMotors.addAll(secondHandMotorAds);
        List<TempVehicleAd> motors = new ArrayList<>();

        // 先拼接需求的排序和筛选属性
        for(VehicleAd motor : tempMotors){
            TempVehicleAd temp = new TempVehicleAd();
            ExampleMotor cur = exampleMotorRepository.findOneById(motor.getExampleVehicleId());
            // 如果模型字符串不是子集字符串，直接跳过本次循环，说明数据不符合要求
            if(!cur.getModel().substring(0, cur.getModel().indexOf("-")).contains(model)){
                continue;
            }

            temp.setId(motor.getId());
            temp.setProvince(motor.getProvince());
            temp.setCity(motor.getCity());
            temp.setCounty(motor.getCounty());
            temp.setTown(motor.getTown());
            temp.setPrice(motor.getPrice());
            temp.setExampleVehicleId(motor.getExampleVehicleId());
            temp.setKilometers(motor.getKilometers());
            temp.setVehicleAdType(motor.getVehicleAdType());
            temp.setSeries(cur.getSeries());
            temp.setMaximumHorsepower(cur.getMaximumHorsepower());
            temp.setMaximumSpeed(cur.getMaximumSpeed());
            temp.setIsImport(cur.getIsImport());
            temp.setEnergy(cur.getEnergy());
            temp.setLifeMile(cur.getLifeMile());
            temp.setDisplacement(cur.getDisplacement());
            temp.setType(cur.getType());
            temp.setIfAccident(motor.getIfAccident());
            temp.setIfShippingDamage(motor.getIfShippingDamage());
            motors.add(temp);
        }

        // 分页处理
        for(int i = (pageNumber - 1) * pageSize; i < (pageNumber - 1) * pageSize + pageSize; i++){
            if(i > motors.size() - 1){
                break;
            }
            rsMotors.add(motors.get(i));
            // 增加浏览数
            view(motors.get(i).getId());
        }

        // 拼接当前页面车辆的其他需求属性
        for(TempVehicleAd motor : rsMotors){
            VehicleAd temp = getVehicleById(motor.getId());
            motor.setUserId(temp.getUserId());
            //motor.setPriceLevel(temp.getPriceLevel());
            motor.setAdLevel(temp.getAdLevel());
            motor.setRealPrice(temp.getRealPrice());
            motor.setViews(temp.getViews());
            motor.setClicks(temp.getClicks());
            motor.setFavoriteUsers(temp.getFavoriteUsers());
            motor.setImages(temp.getImages());
            motor.setVideos(temp.getVideos());
            motor.setDescription(temp.getDescription());
            motor.setCreatedDate(temp.getCreatedDate());
            motor.setAdStatus(temp.getAdStatus());
            motor.setCancelAdReason(temp.getCancelAdReason());

            ExampleMotor cur = exampleMotorRepository.findOneById(motor.getExampleVehicleId());
            motor.setModel(cur.getModel());
            motor.setEngine(cur.getEngine());
            motor.setGearbox(cur.getGearbox());
            motor.setBrand(cur.getBrand());

            motor.setCompanyName(userRepository.findOneById(motor.getUserId()).getCompanyName());

            // 从OSS服务器中获取到封面地址
            motor.setCoverImage(getFirstImageFromOSSById(motor.getId()).toString());
        }
        rs.add(motors);
        rs.add(rsMotors);
        rs.add(motors.size());
        rs.add(pageSize);
        return rs;
    }

    @Override
    public List<Object> getSearchCarCompanyName(String companyName, int pageNumber){
        // 返回的车辆结果集
        List<TempVehicleAd> rsCars = new ArrayList<>();
        // 返回的最终结果，包含页码总数，用于传给前端进行分页操作
        List<Object> rs = new ArrayList<>();

        List<NewCarAd> newCarAds =  newCarAdRepository.findAllByAdStatus(AdStatusEnum.POSTED);
        List<SecondHandCarAd> secondHandCarAds = secondHandCarAdRepository.findAllByAdStatus(AdStatusEnum.POSTED);
        List<VehicleAd> tempCars = new ArrayList<>();
        tempCars.addAll(newCarAds);
        tempCars.addAll(secondHandCarAds);
        List<TempVehicleAd> cars = new ArrayList<>();

        // 先拼接需求的排序和筛选属性
        for(VehicleAd car : tempCars){
            TempVehicleAd temp = new TempVehicleAd();
            ExampleCar cur = exampleCarRepository.findOneById(car.getExampleVehicleId());

            // 如果公司名字符串为null，直接跳过本次循环，说明数据不符合要求
            if(userRepository.findOneById(car.getUserId()).getCompanyName() == null){
                continue;
            }

            // 如果公司名字符串不是子集字符串，直接跳过本次循环，说明数据不符合要求
            if(!userRepository.findOneById(car.getUserId()).getCompanyName().contains(companyName)){
                continue;
            }

            temp.setCompanyName(userRepository.findOneById(car.getUserId()).getCompanyName());

            temp.setId(car.getId());
            temp.setProvince(car.getProvince());
            temp.setCity(car.getCity());
            temp.setCounty(car.getCounty());
            temp.setTown(car.getTown());
            temp.setPrice(car.getPrice());
            temp.setExampleVehicleId(car.getExampleVehicleId());
            temp.setKilometers(car.getKilometers());
            temp.setVehicleAdType(car.getVehicleAdType());
            temp.setIfAccident(car.getIfAccident());
            temp.setIfShippingDamage(car.getIfShippingDamage());

            temp.setSeries(cur.getSeries());
            temp.setMaximumHorsepower(cur.getMaximumHorsepower());
            temp.setMaximumTorque(cur.getMaximumTorque());
            temp.setYear(cur.getYear().substring(0, 4));
            temp.setEnergy(cur.getEnergy());

            cars.add(temp);
        }

        // 分页处理
        for(int i = (pageNumber - 1) * pageSize; i < (pageNumber - 1) * pageSize + pageSize; i++){
            if(i > cars.size() - 1){
                break;
            }
            rsCars.add(cars.get(i));
            // 增加浏览数
            view(cars.get(i).getId());
        }

        // 拼接当前页面车辆的其他需求属性
        for(TempVehicleAd car : rsCars){
            VehicleAd temp = getVehicleById(car.getId());
            car.setUserId(temp.getUserId());
            //car.setPriceLevel(temp.getPriceLevel());
            car.setAdLevel(temp.getAdLevel());
            car.setRealPrice(temp.getRealPrice());
            car.setViews(temp.getViews());
            car.setClicks(temp.getClicks());
            car.setFavoriteUsers(temp.getFavoriteUsers());
            car.setImages(temp.getImages());
            car.setVideos(temp.getVideos());
            car.setDescription(temp.getDescription());
            car.setCreatedDate(temp.getCreatedDate());
            car.setAdStatus(temp.getAdStatus());
            car.setCancelAdReason(temp.getCancelAdReason());

            ExampleCar cur = exampleCarRepository.findOneById(car.getExampleVehicleId());
            car.setModel(cur.getModel());
            car.setLevel(cur.getLevel());
            car.setEngine(cur.getEngine());
            car.setGearbox(cur.getGearbox());
            car.setBrand(cur.getBrand());
            car.setFullYear(cur.getYear());

            // 从OSS服务器中获取到封面地址
            car.setCoverImage(getFirstImageFromOSSById(car.getId()).toString());
        }
        rs.add(rsCars);
        rs.add(cars.size());
        rs.add(pageSize);
        return rs;
    }

    @Override
    public List<Object> getSearchMotorCompanyName(String companyName, int pageNumber){
        // 返回的车辆结果集
        List<TempVehicleAd> rsMotors = new ArrayList<>();
        // 返回的最终结果，包含页码总数，用于传给前端进行分页操作
        List<Object> rs = new ArrayList<>();

        List<NewMotorAd> newMotorAds =  newMotorAdRepository.findAllByAdStatus(AdStatusEnum.POSTED);
        List<SecondHandMotorAd> secondHandMotorAds = secondHandMotorAdRepository.findAllByAdStatus(AdStatusEnum.POSTED);
        List<VehicleAd> tempMotors = new ArrayList<>();
        tempMotors.addAll(newMotorAds);
        tempMotors.addAll(secondHandMotorAds);
        List<TempVehicleAd> motors = new ArrayList<>();
        // 先拼接需求的排序和筛选属性
        for(VehicleAd motor : tempMotors){
            TempVehicleAd temp = new TempVehicleAd();
            ExampleMotor cur = exampleMotorRepository.findOneById(motor.getExampleVehicleId());

//            System.out.println("公司名称： " + userRepository.findOneById(motor.getUserId()).getCompanyName());
//            System.out.println("是否包含： " + userRepository.findOneById(motor.getUserId()).getCompanyName().contains(companyName));

            // 如果公司名字符串为null，直接跳过本次循环，说明数据不符合要求
            if(userRepository.findOneById(motor.getUserId()).getCompanyName() == null){
                continue;
            }

            // 如果公司名字符串不是子集字符串，直接跳过本次循环，说明数据不符合要求
            if(!userRepository.findOneById(motor.getUserId()).getCompanyName().contains(companyName)){
                continue;
            }

            temp.setCompanyName(userRepository.findOneById(motor.getUserId()).getCompanyName());

            temp.setId(motor.getId());
            temp.setProvince(motor.getProvince());
            temp.setCity(motor.getCity());
            temp.setCounty(motor.getCounty());
            temp.setTown(motor.getTown());
            temp.setPrice(motor.getPrice());
            temp.setExampleVehicleId(motor.getExampleVehicleId());
            temp.setKilometers(motor.getKilometers());
            temp.setVehicleAdType(motor.getVehicleAdType());
            temp.setSeries(cur.getSeries());
            temp.setMaximumHorsepower(cur.getMaximumHorsepower());
            temp.setMaximumSpeed(cur.getMaximumSpeed());
            temp.setIsImport(cur.getIsImport());
            temp.setEnergy(cur.getEnergy());
            temp.setLifeMile(cur.getLifeMile());
            temp.setDisplacement(cur.getDisplacement());
            temp.setType(cur.getType());
            temp.setIfAccident(motor.getIfAccident());
            temp.setIfShippingDamage(motor.getIfShippingDamage());
            motors.add(temp);
        }

        // 分页处理
        for(int i = (pageNumber - 1) * pageSize; i < (pageNumber - 1) * pageSize + pageSize; i++){
            if(i > motors.size() - 1){
                break;
            }
            rsMotors.add(motors.get(i));
            // 增加浏览数
            view(motors.get(i).getId());
        }

        // 拼接当前页面车辆的其他需求属性
        for(TempVehicleAd motor : rsMotors){
            VehicleAd temp = getVehicleById(motor.getId());
            motor.setUserId(temp.getUserId());
            //motor.setPriceLevel(temp.getPriceLevel());
            motor.setAdLevel(temp.getAdLevel());
            motor.setRealPrice(temp.getRealPrice());
            motor.setViews(temp.getViews());
            motor.setClicks(temp.getClicks());
            motor.setFavoriteUsers(temp.getFavoriteUsers());
            motor.setImages(temp.getImages());
            motor.setVideos(temp.getVideos());
            motor.setDescription(temp.getDescription());
            motor.setCreatedDate(temp.getCreatedDate());
            motor.setAdStatus(temp.getAdStatus());
            motor.setCancelAdReason(temp.getCancelAdReason());

            ExampleMotor cur = exampleMotorRepository.findOneById(motor.getExampleVehicleId());
            motor.setModel(cur.getModel());
            motor.setEngine(cur.getEngine());
            motor.setGearbox(cur.getGearbox());
            motor.setBrand(cur.getBrand());

            // 从OSS服务器中获取到封面地址
            motor.setCoverImage(getFirstImageFromOSSById(motor.getId()).toString());
        }
        rs.add(motors);
        rs.add(rsMotors);
        rs.add(motors.size());
        rs.add(pageSize);
        return rs;
    }

    /**
     * 库存车筛选（汽车）
     *
     * @param temp   临时汽车数据
     * @param stack 库存车
     * @return {@link List}<{@link TempVehicleAd}>
     */
    private List<TempVehicleAd> stackCars(List<TempVehicleAd> temp, String stack){
        List<TempVehicleAd> rs = new ArrayList<>();
        boolean compare = false;
        if(stack.equals("库存车")){
            compare = true;
        }
        for(TempVehicleAd car : temp){
            if(car.getIfStock() == compare){
                rs.add(car);
            }
        }
        return rs;
    }

    /**
     * 库存车筛选（摩托）
     *
     * @param temp   临时汽车数据
     * @param stack 库存车
     * @return {@link List}<{@link TempVehicleAd}>
     */
    private List<TempVehicleAd> stackMotors(List<TempVehicleAd> temp, String stack){
        List<TempVehicleAd> rs = new ArrayList<>();
        boolean compare = false;
        if(stack.equals("库存车")){
            compare = true;
        }
        for(TempVehicleAd motor : temp){
            if(motor.getIfStock() == compare){
                rs.add(motor);
            }
        }
        return rs;
    }
}
