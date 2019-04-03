package org.yasukusury.onlinedocument.biz.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.yasukusury.onlinedocument.biz.dto.BookDto;
import org.yasukusury.onlinedocument.biz.entity.Book;
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
public interface BookMapper extends BaseMapper<Book> {

    String BASE_COLUMN_LIST = "\n" +
            "        id,\n" +
            "        create_time,\n" +
            "        update_time,\n" +
            "        delete_time,\n" +
            "        valid,\n" +
            "        author, name, introduction, cover, chapters, tags, `open`, visit, love, praise ";

    String LIST_BOOK_BY_AUTHOR_LIMITED = "<script>" +
            "SELECT "+ BASE_COLUMN_LIST +"\n" +
            "        FROM book\n" +
            "        WHERE author = #{aid}\n" +
            "        <if test=\"his != null\">\n" +
            "            AND #{his} > id\n" +
            "        </if>\n" +
            "        AND valid = 1\n" +
            "        ORDER BY id DESC\n" +
            "        LIMIT #{size}" +
            "</script>";

    @Select(LIST_BOOK_BY_AUTHOR_LIMITED)
    List<Book> listBookByAuthorLimited(@Param("aid")Long aid, @Param("his") Long his, @Param("size") int size);

    List<BookDto> listDtoByAuthor(@Param("author")Long author, @Param("open")boolean open);

    BookDto getDtoById(@Param("id")Long id, @Param("open")boolean open);

    List<BookDto> listDtoById(@Param("list")List<Long> list, @Param("open")boolean open);

    List<BookDto> descDtoByPraise();

    List<BookDto> descDtoByLove();

    List<BookDto> descDtoById();
}
