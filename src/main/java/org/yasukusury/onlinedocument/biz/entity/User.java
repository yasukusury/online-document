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
@ApiModel("用户")
public class User extends BaseModel {

    private static final long serialVersionUID = 1L;

    @Builder
    public User(Long id, Timestamp createTime, Timestamp updateTime, Timestamp deleteTime, Boolean valid, String username, String password, Boolean gender, String portrait, String email, String introduction) {
        super(id, createTime, updateTime, deleteTime, valid);
        this.username = username;
        this.password = password;
        this.gender = gender;
        this.portrait = portrait;
        this.email = email;
        this.introduction = introduction;
    }

    /**
     * 用户名称
     */
    private String username;

    /**
     * 用户密码
     */
    private String password;

    private Boolean gender;

    private String portrait;

    private String email;

    private String introduction;

    public static final String USERNAME = "username";

    public static final String PASSWORD = "password";

    public static final String GENDER = "gender";

    public static final String PORTRAIT = "portrait";

    public static final String EMAIL = "email";

    public static final String INTRODUCTION = "introduction";

}
