package org.yasukusury.onlinedocument.biz.entity;

import io.swagger.annotations.ApiModel;
import lombok.*;
import org.yasukusury.onlinedocument.commons.base.BaseModel;
import java.sql.Timestamp;

/**
 * <p>
 * 用户账号表
 * </p>
 *
 * @author yasukusury
 * @since 2018-11-26
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ApiModel("评论")
public class Comment extends BaseModel {

    private static final long serialVersionUID = 1L;

    @Builder
    public Comment(Long id, Timestamp createTime, Timestamp updateTime, Timestamp deleteTime, Boolean valid, Long chapter, Long user, String content, Long praise) {
        super(id, createTime, updateTime, deleteTime, valid);
        this.chapter = chapter;
        this.user = user;
        this.content = content;
        this.praise = praise;
    }


    private Long chapter;

    private Long user;

    private String content;

    private Long praise;

    public static final String CHAPTER = "chapter";

    public static final String USER = "user";

    public static final String CONTENT = "content";

    public static final String PRAISE = "commentPraise";


}
