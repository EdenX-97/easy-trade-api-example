/*
 * @Description:
 * 
 * @Author: Mo Xu
 * 
 * @Date: 2022-01-11 21:14:00
 * 
 * @LastEditors: Mo Xu
 * 
 * @LastEditTime: 2022-01-19 01:03:23
 * 
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/service/impl/ChatServiceImpl.java
 */
package com.easytrade.easytradeapi.service.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import com.easytrade.easytradeapi.constant.consists.Chat;
import com.easytrade.easytradeapi.constant.enums.ResultCodeEnum;
import com.easytrade.easytradeapi.constant.exceptions.ChatException;
import com.easytrade.easytradeapi.model.ChatRecord;
import com.easytrade.easytradeapi.model.User;
import com.easytrade.easytradeapi.repository.ChatRecordRepository;
import com.easytrade.easytradeapi.repository.NewCarAdRepository;
import com.easytrade.easytradeapi.repository.SecondHandCarAdRepository;
import com.easytrade.easytradeapi.repository.UserRepository;
import com.easytrade.easytradeapi.service.intf.ChatService;
import com.easytrade.easytradeapi.utils.JWTUtil;
import com.easytrade.easytradeapi.utils.RedisUtil;
import com.easytrade.easytradeapi.utils.SocketSessionUtil;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class ChatServiceImpl implements ChatService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    ChatRecordRepository chatRecordRepository;

    @Autowired
    NewCarAdRepository newCarAdRepository;

    @Autowired
    SecondHandCarAdRepository secondHandCarAdRepository;

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    RedisUtil redisUtil;

    // redis数据自定义过期时间，用于聊天记录，默认为7天
    @Value("${spring.redis.custom.expire}")
    private long expire;

    // 配置文件中读取system用户的id
    @Value("${chat.system.contactId}")
    private ObjectId systemContactId;

    @Override
    public void chat(Chat chat) {
        // 检查发送者和接受者是否存在
        ObjectId from = chat.getFrom();
        ObjectId to = chat.getTo();
        if (!checkUserExist(from.toString())) {
            throw new ChatException(ResultCodeEnum.NOT_FOUND, "From user do not exist");
        }
        if (!checkUserExist(to.toString())) {
            throw new ChatException(ResultCodeEnum.NOT_FOUND, "To user do not exist");
        }

        // 检查接受者是否在线
        if (checkUserOnline(to.toString())) {
            // 在线时直接发送消息
            simpMessagingTemplate.convertAndSendToUser(to.toString(), "/chat/contact", chat);
        }

        // 将该条消息保存至redis
        String chatKey = "chat:record:" + from + ":" + to;
        String chatToFromKey = "chat:record:" + to + ":" + from;
        // 默认为from to key，如果to from存在则设置
        if (redisUtil.hasKey(chatToFromKey)) {
            chatKey = chatToFromKey;
        }
        redisUtil.lSet(chatKey, chat.toString(), expire);
    }

    @Override
    public void systemNotification(Chat chat) {
        // 检查接受者是否存在
        chat.setFrom(systemContactId);
        ObjectId to = chat.getTo();
        if (!checkUserExist(to.toString())) {
            throw new ChatException(ResultCodeEnum.NOT_FOUND, "To user do not exist");
        }

        // 检查接受者是否在线
        if (checkUserOnline(to.toString())) {
            // 在线时直接发送消息
            simpMessagingTemplate.convertAndSendToUser(to.toString(), "/chat/contact", chat);
        }

        // 将该条消息保存至redis
        String chatKey = "chat:record:" + systemContactId + ":" + to;
        redisUtil.lSet(chatKey, chat.toString(), expire);
    }

    // @Override
    // public ChatRecord getSystemNotification(ObjectId userId) {
    // // 检查用户是否存在
    // if (!checkUserExist(userId.toString())) {
    // throw new ChatException(ResultCodeEnum.NOT_FOUND, "User do not exist");
    // }

    // // 从mongodb中获取历史聊天记录
    // ChatRecord chatRecord = getChatRecordFromMongodb(new ObjectId(systemContactId), userId);
    // ArrayList<Chat> chatsInMongodb = chatRecord.getChats();
    // if (chatsInMongodb == null) {
    // chatsInMongodb = new ArrayList<Chat>();
    // }

    // // 从redis中获取缓存的聊天记录，合并后返回
    // ArrayList<Chat> chatsInRedis = getChatsFromRedis(new ObjectId(systemContactId), userId);
    // chatsInMongodb.addAll(chatsInRedis);
    // chatRecord.setChats(chatsInMongodb);

    // return chatRecord;
    // }

    @Override
    public ChatRecord getChatRecordByTwoUsers(ObjectId contactUserId, String token)
            throws ParseException {
        // 检查联系用户是否存在
        if (!checkUserExist(contactUserId.toString())) {
            throw new ChatException(ResultCodeEnum.NOT_FOUND, "Contact user do not exist");
        }

        // 检查登录的用户是否存在
        String account = JWTUtil.getValue(token);
        ObjectId userId = userRepository.findOneByPhone(account).getId();
        if (userId == null) {
            throw new ChatException(ResultCodeEnum.FAILED,
                    "Login user not exist");
        }

        // 从mongodb中获取历史聊天记录
        ChatRecord chatRecord = getChatRecordFromMongodb(contactUserId, userId);
        ArrayList<Chat> chatsInMongodb = chatRecord.getChats();
        if (chatsInMongodb == null) {
            chatsInMongodb = new ArrayList<Chat>();
        }

        // 从redis中获取缓存的聊天记录，合并后返回
        ArrayList<Chat> chatsInRedis = getChatsFromRedis(contactUserId, userId);
        chatsInMongodb.addAll(chatsInRedis);
        chatRecord.setChats(chatsInMongodb);

        return chatRecord;
    }

    @Override
    public List<String> getContacts(String token) {
        // 检查登录的用户是否存在
        String account = JWTUtil.getValue(token);
        ObjectId loginUserId = userRepository.findOneByPhone(account).getId();
        if (loginUserId == null) {
            throw new ChatException(ResultCodeEnum.FAILED,
                    "Login user not exist");
        }

        // 如果redis中有数据，则从redis中获取并返回
        List<String> contactsStr = new ArrayList<>();
        String key = "chat:contacts:" + loginUserId.toString();
        if (redisUtil.hasKey(key)) {
            contactsStr = redisUtil.lGet(key, 0, -1);
        } else {
            // redis中没有数据，则从mongodb中获取
            // 获取联系人
            User user = userRepository.findOneById(loginUserId);
            ArrayList<ObjectId> contacts = user.getContacts();
            for (ObjectId contact : contacts) {
                String contactStr = contact.toString();
                contactsStr.add(contactStr);
                // 同时保存到redis进行缓存
                redisUtil.lSet(key, contactStr, expire);
            }
        }

        return contactsStr;
    }

    @Override
    public void addContact(ObjectId contactId, String token) {
        // 从JWT中获取用户id
        String account = JWTUtil.getValue(token);
        ObjectId userId = userRepository.findOneByPhone(account).getId();

        // 两个id不能相同
        if (userId.equals(contactId)) {
            throw new ChatException(ResultCodeEnum.FAILED,
                    "Cannot add user himself to his contacts");
        }

        // 检查两个用户是否存在
        if (!checkUserExist(userId.toString())) {
            throw new ChatException(ResultCodeEnum.NOT_FOUND, "User do not exist");
        }
        if (!checkUserExist(contactId.toString())) {
            throw new ChatException(ResultCodeEnum.NOT_FOUND, "User do not exist");
        }

        // 需要对两个用户都进行操作
        addContactToOther(userId, contactId);
        addContactToOther(contactId, userId);
    }

    private void addContactToOther(ObjectId from, ObjectId to) {
        // 首先检索redis中是否有该用户的联系人数据
        ArrayList<ObjectId> contacts = new ArrayList<ObjectId>();;
        String contactsKey = "chat:contacts:" + from.toString();
        if (redisUtil.hasKey(contactsKey)) {
            // 如果有，则直接使用redis中的数据
            List<String> strContacts = redisUtil.lGet(contactsKey, 0, -1);
            // 如果用户已有该联系人，不能重复添加
            if (strContacts.contains(to.toString())) {
                return;
                //throw new ChatException(ResultCodeEnum.FAILED, "This contact exists");
            }

            // 添加联系人并保存到redis
            redisUtil.lSet(contactsKey, to.toString(), expire);
        } else {
            // 如果redis中没有数据，则从mongodb中取，并保存到mongodb
            User user = userRepository.findOneById(from);
            contacts = user.getContacts();
            // 如果用户已有该联系人，不能重复添加
            if (contacts.contains(to)) {
                return;
                //throw new ChatException(ResultCodeEnum.FAILED, "This contact exists");
            }
            // 添加联系人并且保存至redis
            contacts.add(to);
            for (ObjectId contact : contacts) {
                redisUtil.lSet(contactsKey, contact.toString(), expire);
            }
        }
    }

    @Override
    public void updateChatInRedisToMongodb() {
        // 先更新聊天记录
        // 从redis中提取对应的keys
        Set<String> keys = redisUtil.scan("chat:record:*");
        if (keys != null && !keys.isEmpty()) {
            // 遍历每一个key，找出不同用户间的聊天记录
            for (String key : keys) {
                // 将key分割，第三和第四个参数为两个用户的id
                String[] splitKey = key.split(":");
                String userOneId = splitKey[2];
                String userTwoId = splitKey[3];
                // 根据两个用户id来获取两者间的聊天记录
                ArrayList<Chat> chatsInRedis =
                        getChatsFromRedis(new ObjectId(userOneId), new ObjectId(userTwoId));

                // 将所有聊天记录相合，并保存至mongodb
                ChatRecord chatRecord =
                        getChatRecordFromMongodb(new ObjectId(userOneId), new ObjectId(userTwoId));
                ArrayList<Chat> chatsInMongodb = chatRecord.getChats();
                if (chatsInMongodb == null) {
                    chatsInMongodb = new ArrayList<Chat>();
                }
                chatsInMongodb.addAll(chatsInRedis);
                chatRecord.setChats(chatsInMongodb);
                chatRecordRepository.save(chatRecord);

                // 最后删除redis中的缓存
                redisUtil.del(key);
            }
        }

        // 更新联系人
        keys = redisUtil.scan("chat:contacts:*");
        if (keys != null && !keys.isEmpty()) {
            for (String key : keys) {
                // 将key分割，第三个参数为用户的id
                String[] splitKey = key.split(":");
                String userId = splitKey[2];

                // 从redis取出联系人数据
                List<String> contactsStr = redisUtil.lGet(key, 0, -1);
                ArrayList<ObjectId> contacts = new ArrayList<ObjectId>();
                for (String contactStr : contactsStr) {
                    contacts.add(new ObjectId(contactStr));
                }

                // 将所有数据保存至mongodb
                User user = userRepository.findOneById(new ObjectId(userId));
                user.setContacts(contacts);
                userRepository.save(user);

                // 最后删除redis中的缓存
                redisUtil.del(key);
            }
        }

        log.info("Complete update chat in redis to mongodb - ");
    }

    @Override
    public Boolean checkUserExist(String userId) {
        if (!userRepository.existsById(new ObjectId(userId))) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public Boolean checkUserOnline(String id) {
        if (id.contains("-")) {
            String userId = SocketSessionUtil.getUserId(id);
            if (userId == null || userId.isEmpty()) {
                return false;
            } else {
                return true;
            }
        } else {
            String userSessionId = SocketSessionUtil.getSessionId(id);
            if (userSessionId == null || userSessionId.isEmpty()) {
                return false;
            } else {
                return true;
            }
        }
    }

    @Override
    public List<ChatRecord> getAllRecordsByToken(String token) throws ParseException {
        // 获取全部联系人
        List<String> contacts = getContacts(token);

        // 去除联系人中的第一个系统通知
        contacts.remove(contacts.get(0));

        // 根据联系人获取所有聊天记录
        List<ChatRecord> chatRecords = new ArrayList<>();
        for (String contact : contacts) {
            ChatRecord chatRecord = getChatRecordByTwoUsers(new ObjectId(contact), token);
            chatRecords.add(chatRecord);
        }

        return chatRecords;
        //if(!userRepository.existsById(userId)){
        //    throw new ChatException(ResultCodeEnum.NOT_FOUND, "user is not in the database");
        //}
        //List<ChatRecord> records = chatRecordRepository.findAll();
        //List<ChatRecord> rs = new ArrayList<>();
        //for(ChatRecord record : records){
        //    // 如果其中一个用户不为系统
        //    if(!record.getUserOneId().toString().equals("61d0714746018703bb778c63")
        //            && !record.getUserTwoId().toString().equals("61d0714746018703bb778c63")){
        //        // 若发送和接收双方有一个涉及到传入的用户id，则说明该聊天记录与此用户相关
        //        if(record.getUserOneId().equals(userId) || record.getUserTwoId().equals(userId)){
        //            rs.add(record);
        //        }
        //    }
        //}
        //return rs;
    }

    @Override
    public ChatRecord getAllSystemChatRecordsByToken(String token) throws ParseException {
        // 获取全部联系人
        List<String> contacts = getContacts(token);

        // 第一个联系人为系统通知
        String systemContact = contacts.get(0);
        ChatRecord chatRecord = getChatRecordByTwoUsers(new ObjectId(systemContact), token);

        return chatRecord;

        //if(!userRepository.existsById(userId)){
        //    throw new ChatException(ResultCodeEnum.NOT_FOUND, "user is not in the database");
        //}
        //List<ChatRecord> records = chatRecordRepository.findAll();
        //List<ChatRecord> rs = new ArrayList<>();
        //for(ChatRecord record : records){
        //    // 如果其中一个用户为系统
        //    if(record.getUserOneId().toString().equals("61d0714746018703bb778c63")
        //            || record.getUserTwoId().toString().equals("61d0714746018703bb778c63")){
        //        // 若发送和接收双方有一个涉及到传入的用户id，则说明该系统通知记录与此用户相关
        //        if(record.getUserOneId().equals(userId) || record.getUserTwoId().equals(userId)){
        //            rs.add(record);
        //        }
        //    }
        //}
        //return rs;
    }

    //@Override
    //public void createRecord(ObjectId userOneId, ObjectId userTwoId) {
    //    if(!userRepository.existsById(userOneId)){
    //        throw new ChatException(ResultCodeEnum.NOT_FOUND, "user one is not in the database");
    //    }
    //    if(!userRepository.existsById(userTwoId)){
    //        throw new ChatException(ResultCodeEnum.NOT_FOUND, "user two is not in the database");
    //    }
    //    ArrayList<ChatRecord> records = (ArrayList<ChatRecord>) chatRecordRepository.findAll();
    //    if(records.isEmpty()){
    //        ChatRecord rs = new ChatRecord();
    //        rs.setUserOneId(userOneId);
    //        rs.setUserTwoId(userTwoId);
    //        rs.setChats(new ArrayList<>());
    //        chatRecordRepository.save(rs);
    //    }else {
    //        for(ChatRecord record : records){
    //            if(record.getUserOneId().equals(userOneId)
    //                    && record.getUserTwoId().equals(userTwoId)){
    //                throw new ChatException(ResultCodeEnum.FAILED, "this record already exists");
    //            }
    //            if(record.getUserOneId().equals(userTwoId)
    //                    && record.getUserTwoId().equals(userOneId)){
    //                throw new ChatException(ResultCodeEnum.FAILED, "this record already exists");
    //            }
    //        }
    //        ChatRecord rs = new ChatRecord();
    //        rs.setUserOneId(userOneId);
    //        rs.setUserTwoId(userTwoId);
    //        rs.setChats(new ArrayList<>());
    //        chatRecordRepository.save(rs);
    //    }
    //}

    /**
     * @description: 内部方法，从mongodb中获取历史聊天记录
     * @param {ObjectId} userOneId 用户其一id
     * @param {ObjectId} userTwoId 用户其二id
     * @return {ChatRecord} mongodb中储存的聊天记录
     */
    private ChatRecord getChatRecordFromMongodb(ObjectId userOneId, ObjectId userTwoId) {
        // 检查mongodb中是否存在数据，注意两者的id可能会互换
        ChatRecord chatRecord = null;
        if (chatRecordRepository.existsByUserOneIdAndUserTwoId(userOneId, userTwoId)) {
            chatRecord = chatRecordRepository.findOneByUserOneIdAndUserTwoId(userOneId, userTwoId);
        } else if (chatRecordRepository.existsByUserOneIdAndUserTwoId(userTwoId, userOneId)) {
            chatRecord = chatRecordRepository.findOneByUserOneIdAndUserTwoId(userTwoId, userOneId);
        } else {
            // 如果两个用户间没有聊天记录，则新建一个并返回
            chatRecord = new ChatRecord();
            chatRecord.setUserOneId(userOneId);
            chatRecord.setUserTwoId(userTwoId);
        }
        return chatRecord;
    }

    /**
     * @description: 内部方法，从redis中获取聊天记录（缓存的聊天记录还未保存至mongodb时）
     * @param {ObjectId} userOneId 用户其一id
     * @param {ObjectId} userTwoId 用户其二id
     * @return {ArrayList<Chat>} redis中返回的所有聊天记录列表
     */
    private ArrayList<Chat> getChatsFromRedis(ObjectId userOneId, ObjectId userTwoId) {
        // 默认key为userOneId:userTwoId，如果userTwoId:userOneId存在则设置
        String chatKey = "chat:record:" + userOneId.toString() + ":" + userTwoId.toString();
        String chatTwoOneKey = "chat:record:" + userTwoId.toString() + ":" + userOneId.toString();
        if (redisUtil.hasKey(chatTwoOneKey)) {
            chatKey = chatTwoOneKey;
        }

        // 只有当该key存在时，说明redis中存在缓存的聊天记录，将这些聊天记录返回
        ArrayList<Chat> savedChats = new ArrayList<>();
        if (redisUtil.hasKey(chatKey)) {
            List<String> chats = redisUtil.lGet(chatKey, 0, -1);
            // 遍历每一个chat来保存
            for (String chat : chats) {
                Chat savedChat = new Chat();
                // 每个String的chat格式为Chat(from=**, to=**, content=**, date=**)，首先去除首尾的Chat()
                chat = chat.substring(5, chat.length() - 1);
                // 将chat分割为["from=**", "to=**", ...]
                String[] splitChat = chat.split(", ");
                // 遍历取出分割后的每一个值，并给savedChat赋值
                for (String variable : splitChat) {
                    // 将"from=**"的属性分割，list中的前一个为属性名，后一个值为具体的值
                    String[] splitVariable = variable.split("=");
                    String name = splitVariable[0];
                    String value = splitVariable[1];
                    switch (name) {
                        case "from":
                            savedChat.setFrom(new ObjectId(value));
                            break;
                        case "to":
                            savedChat.setTo(new ObjectId(value));
                            break;
                        case "content":
                            savedChat.setContent(value);
                            break;
                        case "date":
                            savedChat.setDate(value);
                            break;
                    }
                }
                savedChats.add(savedChat);
            }
        }

        // 如果redis中不存在记录，则直接返回一个空的chats

        return savedChats;
    }
}

