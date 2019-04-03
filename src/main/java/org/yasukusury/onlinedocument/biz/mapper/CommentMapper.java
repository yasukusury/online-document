package org.yasukusury.onlinedocument.biz.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.yasukusury.onlinedocument.biz.dto.CommentDto;
import org.yasukusury.onlinedocument.biz.entity.Comment;
import org.yasukusury.onlinedocument.commons.base.BaseMapper;

import java.util.List;

/**
 * <p>
 * 用户账号表 Mapper 接口
 * </p>
 *
 * @author yasukusury
 * @since 2018-11-26
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

    List<CommentDto> listDtoByChapter(@Param("chapter") Long chapter);
}
