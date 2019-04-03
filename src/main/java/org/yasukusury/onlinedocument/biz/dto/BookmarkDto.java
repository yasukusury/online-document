package org.yasukusury.onlinedocument.biz.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.yasukusury.onlinedocument.biz.entity.Book;
import org.yasukusury.onlinedocument.biz.entity.Bookmark;
import org.yasukusury.onlinedocument.biz.entity.Chapter;

/**
 * @author 30254
 * creadtedate: 2019/3/4
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookmarkDto {

    private String book;
    private String chapter;
    @JsonIgnore
    private Long bookId;
    @JsonIgnore
    private Long chapterId;
    private String url;

    public static BookmarkDto toDto(Bookmark bookmark, Book book, Chapter chapter) {
        return builder()
                .book(book.getName())
                .chapter(chapter != null ? chapter.getName() : book.getName())
                .url("/book/document/b" + book.getId()
                        + "?nav=" + (chapter != null ? chapter.getId() : book.getId())
                        + "&progress=" + bookmark.getProgress())
                .build();
    }

    public static BookmarkDto completeUrl(BookmarkDto dto) {
        if (dto == null) {
            return null;
        }
        if (dto.getBookId()!= null && dto.getChapterId()!=null) {
            String progress = dto.getUrl();
            dto.setUrl("/book/document/b" + dto.bookId
                    + "?nav=" + (dto.chapterId)
                    + "&progress=" + progress);
        }
        return dto;
    }
}
