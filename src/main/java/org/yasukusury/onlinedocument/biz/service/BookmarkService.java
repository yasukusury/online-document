package org.yasukusury.onlinedocument.biz.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yasukusury.onlinedocument.biz.dto.BookmarkDto;
import org.yasukusury.onlinedocument.biz.entity.Book;
import org.yasukusury.onlinedocument.biz.entity.Bookmark;
import org.yasukusury.onlinedocument.biz.entity.Chapter;
import org.yasukusury.onlinedocument.biz.mapper.BookmarkMapper;
import org.yasukusury.onlinedocument.commons.base.BaseService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 30254
 * creadtedate: 2019/3/4
 */
@Service
public class BookmarkService extends BaseService<BookmarkMapper, Bookmark> {

    @Autowired
    private BookService bookService;

    @Autowired
    private ChapterService chapterService;

    public List<BookmarkDto> warpedBookmarkList(Long userId) {
        List<Bookmark> bookmarks = listByExample(Bookmark.builder().user(userId).build());
        List<Bookmark> deadb = listByExample(Bookmark.builder().user(userId).build());
        if (bookmarks.size() == 0) {
            return new ArrayList<>();
        }
        List<Long> bookIds = new ArrayList<>();
        List<Long> chapterIds = new ArrayList<>();
        bookmarks.forEach(v -> {
            bookIds.add(v.getBook());
            if (v.getChapter() != null) {
                chapterIds.add(v.getChapter());
            }
        });
        Map<Long, Book> bookMap = new HashMap<>();
        Map<Long, Chapter> chapterMap = new HashMap<>();
        bookService.listByIds(bookIds, false).forEach(v -> bookMap.put(v.getId(), v));
        chapterService.listByIds(chapterIds).forEach(v -> chapterMap.put(v.getId(), v));
        List<BookmarkDto> dtos = new ArrayList<>(bookmarks.size());
        bookmarks.forEach(v -> {
            Book book = bookMap.get(v.getBook());
            if (book == null){

                return;
            }
            // bookmark.chapter = 0 表示书签在根节点上，map.get出来结果正好是null
            Chapter chapter = chapterMap.get(v.getChapter());
            if (chapter == null) {
                return;
            }
            dtos.add(BookmarkDto.toDto(v, book, chapter));
        });
        return dtos;
    }

    public Bookmark getByUserAndBook(Long user, Long book){
        Bookmark bookmark = Bookmark.builder().user(user).book(book).build();
        return getByExample(bookmark);
    }

    public BookmarkDto getDtoByUserAndBook(Long user, Long book){
        BookmarkDto dto = baseMapper.getDtoByUserAndBook(user, book);
        BookmarkDto.completeUrl(dto);
        return dto;
    }

    public List<BookmarkDto> listDtoByUser(Long user){
        List<BookmarkDto> dtos = baseMapper.listDtoByUser(user);
        dtos.forEach(BookmarkDto::completeUrl);
        return dtos;
    }

}
