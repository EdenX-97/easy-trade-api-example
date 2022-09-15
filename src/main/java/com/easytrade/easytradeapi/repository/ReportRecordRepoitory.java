/**
 * @author: Hongzhang Liu
 * @description 举报记录持久层
 * @date 14/4/2022 6:35 pm
 */
package com.easytrade.easytradeapi.repository;

import com.easytrade.easytradeapi.model.ReportRecord;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ReportRecordRepoitory extends MongoRepository<ReportRecord, ObjectId> {
    /**
     * 根据提交举报者id得到所有相关举报
     *
     * @param submitUserId 提交用户id
     * @return {@link List}<{@link ReportRecord}> 举报记录集合
     */
    public List<ReportRecord> findAllBySubmitUserId(ObjectId submitUserId);

    /**
     * 根据被举报者id得到所有相关举报
     *
     * @param reportedUserId 报告用户id
     * @return {@link List}<{@link ReportRecord}> 举报记录集合
     */
    public List<ReportRecord> findAllByReportedUserId(ObjectId reportedUserId);

    /**
     * 根据举报者id检测数据库中是否存在数据
     *
     * @param submitUserId 举报用户id
     * @return boolean 是/否
     */
    public boolean existsBySubmitUserId(ObjectId submitUserId);

    /**
     * 根据被举报者id检测数据库中是否存在数据
     *
     * @param reportedUserId 被举报用户id
     * @return boolean 是/否
     */
    public boolean existsByReportedUserId(ObjectId reportedUserId);
}
