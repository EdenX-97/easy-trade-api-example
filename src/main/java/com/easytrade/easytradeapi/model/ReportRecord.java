/**
 * @author: Hongzhang Liu
 * @description 举报记录
 * @date 14/4/2022 4:45 pm
 */
package com.easytrade.easytradeapi.model;

import com.easytrade.easytradeapi.constant.consists.Chat;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import javax.validation.constraints.Pattern;
import java.util.ArrayList;

@Document(collection = "reportRecords")
@Data
public class ReportRecord {
    @MongoId
    ObjectId id;

    // 举报者
    ObjectId submitUserId;

    // 被举报者
    ObjectId reportedUserId;

    // 文字描述（小于两百字），只能输入中英文和数字，必填
    @Pattern(regexp = "^[A-z0-9\\u4e00-\\u9fa5]*$")
    String description;

    // 两张图片，可选
    ArrayList<String> pics;

    // 聊天记录，可选
    ArrayList<Chat> chats;
}
