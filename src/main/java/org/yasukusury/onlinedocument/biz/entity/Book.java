package org.yasukusury.onlinedocument.biz.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.yasukusury.onlinedocument.commons.base.BaseModel;
import java.sql.Timestamp;
import java.util.List;

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
@ApiModel(value = "书籍", parent = BaseModel.class)
public class Book extends BaseModel {

    private static final long serialVersionUID = 1L;

    @Builder
    public Book(Long id, Timestamp createTime, Timestamp updateTime, Timestamp deleteTime, Boolean valid, Long author, User authorR, String name, String introduction, String cover, List<Chapter> chaptersR, String tags, Boolean open, Long visit, Long love, Long praise) {
        super(id, createTime, updateTime, deleteTime, valid);
        this.author = author;
        this.authorR = authorR;
        this.name = name;
        this.introduction = introduction;
        this.cover = cover;
        this.chaptersR = chaptersR;
        this.tags = tags;
        this.open = open;
        this.visit = visit;
        this.love = love;
        this.praise = praise;
    }

    @ApiModelProperty(value = "作者id", example = "1")
    private Long author;

    @TableField(exist = false)
    @JsonIgnore
    @ApiModelProperty(hidden = true)
    private User authorR;

    @ApiModelProperty("书籍名字")
    private String name;

    @ApiModelProperty("书籍简介")
    private String introduction;

    private String cover;

    @TableField(exist = false)
    @JsonIgnore
    @ApiModelProperty(hidden = true)
    private List<Chapter> chaptersR;

    private String tags;

    private Boolean open;

    private Long visit;

    private Long love;

    private Long praise;

    public static final String AUTHOR = "author";

    public static final String NAME = "name";

    public static final String INTRODUCTION = "introduction";

    public static final String COVER = "cover";

    public static final String CHAPTERS = "chapters";

    public static final String TAGS = "tags";

    public static final String OPEN = "open";

    public static final String VISIT = "visit";

    public static final String LOVE = "love";

    public static final String PRAISE = "praise";


}
