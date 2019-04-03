package org.yasukusury.onlinedocument.biz.entity;

import lombok.*;
import org.yasukusury.onlinedocument.commons.base.BaseModel;
import java.sql.Timestamp;

import lombok.experimental.Accessors;

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
public class Image extends BaseModel {

    private static final long serialVersionUID = 1L;

    @Builder
    public Image(Long id, Timestamp createTime, Timestamp updateTime, Timestamp deleteTime, Boolean valid, String url, String area, Long tid) {
        super(id, createTime, updateTime, deleteTime, valid);
        this.url = url;
        this.area = area;
        this.tid = tid;
    }

    private String url;

    private String area;

    private Long tid;

    public static final String URL = "url";

    public static final String AREA = "area";

    public static final String TID = "tid";


}
