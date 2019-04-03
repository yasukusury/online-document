package org.yasukusury.onlinedocument.biz.entity;

import io.swagger.annotations.ApiModel;
import lombok.*;
import org.yasukusury.onlinedocument.commons.base.BaseModel;

import java.sql.Timestamp;

/**
 * @author 30254
 * creadtedate: 2019/3/4
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ApiModel("用户活动")
public class UserAct extends BaseModel {
    @Builder
    public UserAct(Long id, Timestamp createTime, Timestamp updateTime, Timestamp deleteTime, Boolean valid, String bookPraise, String love, String commentPraise) {
        super(id, createTime, updateTime, deleteTime, valid);
        this.bookPraise = bookPraise;
        this.love = love;
        this.commentPraise = commentPraise;
    }

    private String love;

    private String bookPraise;

    private String commentPraise;
}
