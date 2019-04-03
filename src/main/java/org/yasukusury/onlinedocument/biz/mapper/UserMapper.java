package org.yasukusury.onlinedocument.biz.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.yasukusury.onlinedocument.biz.entity.User;
import org.yasukusury.onlinedocument.commons.base.BaseMapper;

import java.util.Map;
import java.util.Set;

/**
 * <p>
 * 用户账号表 Mapper 接口
 * </p>
 *
 * @author yasukusury
 * @since 2018-11-26
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("select count(id) from user where username = #{name}")
    Integer countUsername(@Param("name") String name);

    Map<Long, User> mapIdByIdSet(@Param("ids")Set<Long> ids);

    @Update("update user set portrait = #{portrait} where id = #{id}")
    void updatePortraitById(@Param("portrait")String portrait, @Param("id")Long id);
}
