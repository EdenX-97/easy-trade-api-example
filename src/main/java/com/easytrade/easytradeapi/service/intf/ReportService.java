/**
 * @author: Hongzhang Liu
 * @description 举报服务接口
 * @date 14/4/2022 4:44 pm
 */
package com.easytrade.easytradeapi.service.intf;

import com.easytrade.easytradeapi.model.ReportRecord;
import org.bson.types.ObjectId;

import java.util.List;

public interface ReportService {
    /**
     * 提交举报
     *
     * @param submitUserId 举报用户（必须是完成认证的用户或商家）
     * @param reportedUserId 被举报用户
     * @param des   文字描述（少于两百字）
     * @param pics  图片（最多两张）
     * @param chatStartPos 聊天记录起始位置
     * @param chatEndPos 聊天记录终止位置
     */
    public void submitReportRecord(ObjectId submitUserId, ObjectId reportedUserId, String des, List<String> pics, int chatStartPos, int chatEndPos);

    /**
     * 根据id取消举报
     *
     * @param id id 举报记录id
     */
    public void cancelReportByObjectId(ObjectId id);

    /**
     * 根据提交举报者id得到所有相关举报
     *
     * @param submitUserId 提交用户id
     * @return {@link List}<{@link ReportRecord}> 举报记录集合
     */
    public List<ReportRecord> getAllReportRecordBySubmitUserId(ObjectId submitUserId);

    /**
     * 根据被举报者id得到所有相关举报
     *
     * @param reportedUserId 报告用户id
     * @return {@link List}<{@link ReportRecord}> 举报记录集合
     */
    public List<ReportRecord> getAllReportRecordByReportedUserId(ObjectId reportedUserId);
}
