package org.yasukusury.onlinedocument.biz.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.yasukusury.onlinedocument.biz.entity.Book;
import org.yasukusury.onlinedocument.biz.entity.Chapter;
import org.yasukusury.onlinedocument.biz.entity.User;
import org.yasukusury.onlinedocument.biz.service.UserService;
import org.yasukusury.onlinedocument.commons.utils.spring.SpringContextHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author 30254
 * creadtedate: 2019/1/7
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookDto {

    private Long id;

    private String author;

    @JSONField(serialize = false)
    private Long authorId;

    private String name;

    private String introduction;

    private String cover;

    private List<ChapterDto> sub;

    @JSONField(serialize = false)
    private String tags;

    @JSONField(serialize = false)
    private Boolean open;

    @JSONField(serialize = false)
    @Builder.Default
    private Long visit = 0L;

    @JSONField(serialize = false)
    @Builder.Default
    private Long love = 0L;

    @JSONField(serialize = false)
    @Builder.Default
    private Long praise = 0L;

    public static BookDto toDto(Book book, User user){
        BookDto dto = BookDto.builder()
                .id(book.getId())
                .name(book.getName())
                .author(user.getUsername())
                .authorId(user.getId())
                .introduction(book.getIntroduction())
                .cover(book.getCover())
                .tags(book.getTags())
                .open(book.getOpen())
                .visit(book.getVisit())
                .love(book.getLove())
                .praise(book.getPraise())
                .build();
        List<Chapter> chapters = book.getChaptersR();
        if (chapters != null && chapters.size() > 0){
            List<ChapterDto> subs = new ArrayList<>();
            for (Chapter c : chapters){
                subs.add(ChapterDto.toDto(c));
            }
            dto.sub = subs;
        }
        return dto;
    }

}
