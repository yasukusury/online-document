package org.yasukusury.onlinedocument.biz.entity;

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
@ApiModel("章节")
public class Chapter extends BaseModel {

    private static final long serialVersionUID = 1L;

    @Builder
    public Chapter(Long id, Timestamp createTime, Timestamp updateTime, Timestamp deleteTime, Boolean valid, String name, Long pid, Integer seq, Long book, Boolean url) {
        super(id, createTime, updateTime, deleteTime, valid);
        this.name = name;
        this.pid = pid;
        this.seq = seq;
        this.book = book;
        this.url = url;
    }

    @ApiModelProperty("章节名字")
    private String name;

    @ApiModelProperty(value = "章节父id", example = "1")
    private Long pid;

    @ApiModelProperty(value = "章节同级顺位", example = "0")
    private Integer seq;

    @ApiModelProperty(value = "章节书籍id", example = "1")
    private Long book;

    @TableField(exist = false)
    @JsonIgnore
    @ApiModelProperty(hidden = true)
    private List<Chapter> subChapters;

    private Boolean url;


    public static final String NAME = "name";

    public static final String PID = "pid";

    public static final String SEQ = "seq";

    public static final String BOOK = "book";

    public static final String URL = "url";

}
