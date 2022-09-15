/**
 * @author: Hongzhang Liu
 * @description 举报服务实现类
 * @date 14/4/2022 4:44 pm
 */
package com.easytrade.easytradeapi.service.impl;

import com.easytrade.easytradeapi.constant.consists.Chat;
import com.easytrade.easytradeapi.constant.enums.ResultCodeEnum;
import com.easytrade.easytradeapi.constant.enums.UserStatusEnum;
import com.easytrade.easytradeapi.constant.exceptions.ReportRecordException;
import com.easytrade.easytradeapi.model.ChatRecord;
import com.easytrade.easytradeapi.model.ReportRecord;
import com.easytrade.easytradeapi.model.User;
import com.easytrade.easytradeapi.repository.ChatRecordRepository;
import com.easytrade.easytradeapi.repository.ReportRecordRepoitory;
import com.easytrade.easytradeapi.repository.UserRepository;
import com.easytrade.easytradeapi.service.intf.ReportService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ChatRecordRepository chatRecordRepository;

    @Autowired
    ReportRecordRepoitory reportRecordRepoitory;

    @Override
    public void submitReportRecord(ObjectId submitUserId, ObjectId reportedUserId, String des, List<String> pics, int chatStartPos, int chatEndPos) {
        // 检测用户集合中是否存在举报者用户和被举报者id
        if(!userRepository.existsById(submitUserId)){
            throw new ReportRecordException(ResultCodeEnum.NOT_FOUND, "submitUserId not found");
        }
        if(!userRepository.existsById(reportedUserId)){
            throw new ReportRecordException(ResultCodeEnum.NOT_FOUND, "reportedUserId not found");
        }
        // 获取举报者实体对象
        User fromUser = userRepository.findOneById(submitUserId);
        // 判断权限，只有认证的个人和商户才可以提交举报
        if(!fromUser.getRole().equals(UserStatusEnum.USER.getStatus())
            && !fromUser.getRole().equals(UserStatusEnum.DEALER.getStatus())){
            throw new ReportRecordException(ResultCodeEnum.FAILED, "user status does not meet reporting requirements");
        }
        // 举报内容描述最大长度为200个字符
        if(des.toCharArray().length > 200){
            throw new ReportRecordException(ResultCodeEnum.INVALID_PARAM, "description can not greater than 200");
        }
        // 图片最多为两个
        if(pics.size() > 2){
            throw new ReportRecordException(ResultCodeEnum.INVALID_PARAM, "can upload up to two photos");
        }
        // 根据入参给实体赋值
        ReportRecord reportRecord = new ReportRecord();
        reportRecord.setSubmitUserId(submitUserId);
        reportRecord.setReportedUserId(reportedUserId);
        reportRecord.setDescription(des);
        ArrayList<String> imagesURL = new ArrayList<>();
        // 将路径传入到实体的pics属性当中
        for (String temp : pics) {
            File file = new File(temp);
            System.out.println(file);
            imagesURL.add(file.toString());
        }
        reportRecord.setPics(imagesURL);
        ChatRecord chatRecord = chatRecordRepository.findOneByUserOneIdAndUserTwoId(submitUserId, reportedUserId);
        System.out.println(chatRecord);
        // 如果两个用户之间不存在聊天记录，则返回一个空数组，否则根据起始位置和终止位置对聊天记录进行选择，下标最开始为0
        if (chatRecord == null) {
            reportRecord.setChats(new ArrayList<>());
            reportRecordRepoitory.save(reportRecord);
        } else {
            // 聊天记录起始位置和结束位置均要大于0且小于聊天记录数组长度，且起始位置值要恒小于等于终止位置
            if (chatStartPos < 0 || chatStartPos >= chatRecord.getChats().size()) {
                throw new ReportRecordException(ResultCodeEnum.INVALID_PARAM, "chatStartPos has to be greater than -1 and less than length of chats");
            }
            if (chatEndPos < 0 || chatEndPos >= chatRecord.getChats().size()) {
                throw new ReportRecordException(ResultCodeEnum.INVALID_PARAM, "chatEndPos has to be greater than -1 and less than length of chats");
            }
            if (chatStartPos > chatEndPos) {
                throw new ReportRecordException(ResultCodeEnum.INVALID_PARAM, "chatStartPos has to be less than chatEndPos");
            }
            // 从聊天记录数组里逐条获取chat对象，并返回给举报记录的chats属性
            ArrayList<Chat> temp = new ArrayList<>();
            for (int i = chatStartPos; i <= chatEndPos; i++) {
                temp.add(chatRecord.getChats().get(i));
            }
            reportRecord.setChats(temp);
            // 保存结果
            reportRecordRepoitory.save(reportRecord);
        }
    }

    @Override
    public void cancelReportByObjectId(ObjectId id) {
        // 判断输入的举报记录id合法性
        if(!reportRecordRepoitory.existsById(id)){
            throw new ReportRecordException(ResultCodeEnum.NOT_FOUND, "do not have this report record in the database");
        }
        reportRecordRepoitory.deleteById(id);
    }

    @Override
    public List<ReportRecord> getAllReportRecordBySubmitUserId(ObjectId submitUserId) {
        // 判断输入的提交用户id合法性
        if(!userRepository.existsById(submitUserId)){
            throw new ReportRecordException(ResultCodeEnum.NOT_FOUND, "this user id is not in the database");
        }
        if(!reportRecordRepoitory.existsBySubmitUserId(submitUserId)){
            throw new ReportRecordException(ResultCodeEnum.NOT_FOUND, "this user did not submit report before");
        }
        return reportRecordRepoitory.findAllBySubmitUserId(submitUserId);
    }

    @Override
    public List<ReportRecord> getAllReportRecordByReportedUserId(ObjectId reportedUserId) {
        // 判断输入的被举报用户id合法性
        if(!userRepository.existsById(reportedUserId)){
            throw new ReportRecordException(ResultCodeEnum.NOT_FOUND, "this user id is not in the database");
        }
        if(!reportRecordRepoitory.existsByReportedUserId(reportedUserId)){
            throw new ReportRecordException(ResultCodeEnum.NOT_FOUND, "this user has never been reported");
        }
        return reportRecordRepoitory.findAllByReportedUserId(reportedUserId);
    }
}
