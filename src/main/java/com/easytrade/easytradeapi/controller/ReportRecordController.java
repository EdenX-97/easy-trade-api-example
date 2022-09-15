/**
 * @author: Hongzhang Liu
 * @description 举报记录控制器
 * @date 14/4/2022 4:59 pm
 */
package com.easytrade.easytradeapi.controller;

import com.easytrade.easytradeapi.constant.consists.Result;
import com.easytrade.easytradeapi.model.ReportRecord;
import com.easytrade.easytradeapi.service.intf.ReportService;
import com.easytrade.easytradeapi.utils.ReturnResultUtil;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@Validated
public class ReportRecordController {
    @Autowired
    ReportService reportService;

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
    @PostMapping("/report/submitReport")
    public Result submitReport(@RequestParam @NotNull ObjectId submitUserId,
                               @RequestParam @NotNull ObjectId reportedUserId,
                               @RequestParam @NotNull String des,
                               @RequestParam List<String> pics,
                               @RequestParam int chatStartPos,
                               @RequestParam int chatEndPos){
        reportService.submitReportRecord(submitUserId, reportedUserId, des, pics, chatStartPos, chatEndPos);
        return ReturnResultUtil.success("submit report successfully");
    }

    /**
     * 根据id取消举报
     *
     * @param id id 举报记录id
     */
    @PostMapping("/report/cancelReportByObjectId")
    public Result cancelReportByObjectId(@RequestParam @NotNull ObjectId id){
        reportService.cancelReportByObjectId(id);
        return ReturnResultUtil.success("cancel report successfully");
    }

    /**
     * 根据提交举报者id得到所有相关举报
     *
     * @param submitUserId 提交用户id
     * @return {@link List}<{@link ReportRecord}> 举报记录集合
     */
    @GetMapping("/report/getAllReportRecordBySubmitUserId")
    public Result getAllReportRecordBySubmitUserId(@RequestParam @NotNull ObjectId submitUserId){
        List<ReportRecord> rs = reportService.getAllReportRecordBySubmitUserId(submitUserId);
        return ReturnResultUtil.success(rs);
    }

    /**
     * 根据被举报者id得到所有相关举报
     *
     * @param reportedUserId 报告用户id
     * @return {@link List}<{@link ReportRecord}> 举报记录集合
     */
    @GetMapping("/report/getAllReportRecordByReportedUserId")
    public Result getAllReportRecordByReportedUserId(@RequestParam @NotNull ObjectId reportedUserId){
        List<ReportRecord> rs = reportService.getAllReportRecordByReportedUserId(reportedUserId);
        return ReturnResultUtil.success(rs);
    }
}
