package org.yasukusury.onlinedocument.biz.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.yasukusury.onlinedocument.biz.dto.BookmarkDto;
import org.yasukusury.onlinedocument.biz.entity.Bookmark;
import org.yasukusury.onlinedocument.commons.base.BaseMapper;

import java.util.List;


/**
 * @author 30254
 * creadtedate: 2019/3/4
 */
@Mapper
public interface BookmarkMapper extends BaseMapper<Bookmark> {

    List<BookmarkDto> listDtoByUser(@Param("user")Long user);

    BookmarkDto getDtoByUserAndBook(@Param("user")Long user, @Param("book")Long book);
}
