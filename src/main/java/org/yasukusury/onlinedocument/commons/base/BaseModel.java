package org.yasukusury.onlinedocument.commons.base;


import lombok.*;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author 30254
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseModel implements Serializable {

    /**
     * 主键
     */
    private Long id;
    /**
     * 创建时间
     */
    private Timestamp createTime;
    /**
     * 更新时间
     */
    private Timestamp updateTime;
    /**
     * 删除时间
     */
    private Timestamp deleteTime;
    /**
     * 是否被删除，取代sql中判断删除时间datetime为null
     */
    private Boolean valid = true;


    public static final String ID = "id";

    public static final String CREATETIME = "createTime";

    public static final String UPDATETIME = "updateTime";

    public static final String DELETETIME = "deleteTime";

    public static final String VALID = "valid";
}
