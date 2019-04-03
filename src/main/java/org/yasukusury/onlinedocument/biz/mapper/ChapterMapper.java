package org.yasukusury.onlinedocument.biz.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.yasukusury.onlinedocument.biz.dto.ChapterESo;
import org.yasukusury.onlinedocument.biz.entity.Chapter;
import org.yasukusury.onlinedocument.commons.base.BaseMapper;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户账号表 Mapper 接口
 * </p>
 *
 * @author yasukusury
 * @since 2018-11-26
 */
@Mapper
public interface ChapterMapper extends BaseMapper<Chapter> {

    String BASE_COLUMN_SQL = "id,\n" +
            "        create_time,\n" +
            "        update_time,\n" +
            "        delete_time,\n" +
            "        valid,\n" +
            "        name, pid, seq, book, url ";

    String INSERT_BATCH_SQL = "<script>" +
            "INSERT INTO chapter(name, url, pid, seq, book) VALUES\n" +
            "<foreach collection=\"list\" item=\"item\" index=\"index\" separator=\",\"> \n" +
            "   (     " +
            "            #{item.name}," +
            "            #{item.url}," +
            "            #{item.pid}," +
            "            #{item.seq}," +
            "            #{item.book}" +
            "   ) " +
            "</foreach>" +
            "</script>";

    String LIST_BOOK_CHAPTERS = "<script>" +
            "SELECT " + BASE_COLUMN_SQL +
            "FROM chapter " +
            "WHERE book = #{book} " +
            "AND valid = 1" +
            "</script>";

    String COUNT_URL_BOOK_CHAPTER = "<script>" +
            "SELECT count(id) " +
            "FROM chapter " +
            "WHERE book = #{book} AND url = 1 " +
            "AND valid = 1" +
            "</script>";

    @Select(LIST_BOOK_CHAPTERS)
    List<Chapter> listBookChapters(@Param("book") Long book);

    @Insert(INSERT_BATCH_SQL)
    int insertBatch(@Param("list") Collection<Chapter> chapters);

    @Select(COUNT_URL_BOOK_CHAPTER)
    int countUrlBookChapter(@Param("book")Long book);

    List<ChapterESo> listESoById(@Param("list")List<Long> list);
}
