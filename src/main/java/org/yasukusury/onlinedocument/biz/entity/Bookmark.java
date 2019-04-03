package org.yasukusury.onlinedocument.biz.entity;

import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.yasukusury.onlinedocument.commons.base.BaseModel;

import java.sql.Timestamp;

/**
 * @author 30254
 * creadtedate: 2019/3/4
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ApiModel("书签")
public class Bookmark extends BaseModel {

    @Builder
    public Bookmark(Long id, Timestamp createTime, Timestamp updateTime, Timestamp deleteTime, Boolean valid, Long user, Long book, Long chapter, Double progress) {
        super(id, createTime, updateTime, deleteTime, valid);
        this.user = user;
        this.book = book;
        this.chapter = chapter;
        this.progress = progress;
    }

    private Long user;

    private Long book;

    private Long chapter;

    private Double progress;
}
