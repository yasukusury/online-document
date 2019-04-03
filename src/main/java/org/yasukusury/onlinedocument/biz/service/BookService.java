package org.yasukusury.onlinedocument.biz.service;

import lombok.Getter;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.yasukusury.onlinedocument.biz.dto.BookDto;
import org.yasukusury.onlinedocument.biz.entity.Book;
import org.yasukusury.onlinedocument.biz.entity.ESBook;
import org.yasukusury.onlinedocument.biz.entity.UserAct;
import org.yasukusury.onlinedocument.biz.mapper.BookMapper;
import org.yasukusury.onlinedocument.biz.repository.ESBookRepository;
import org.yasukusury.onlinedocument.commons.base.BaseService;
import org.yasukusury.onlinedocument.commons.utils.StringTool;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author 30254
 * creadtedate: 2019/1/4
 */
@Service
@CommonsLog
public class BookService extends BaseService<BookMapper, Book> {

    public static String BOOK_READ_MAP_KEY = "book-read";

    @Autowired
    private UserActService userActService;

    @Autowired
    private ESBookRepository repository;

    @Autowired
    private RedisService redisService;

    @Autowired
    private StringRedisSerializer stringRedisSerializer;

    @Getter
    private Map<String, List<BookDto>> recommendBooks;


    @PostConstruct
    @Scheduled(cron = "0 0 0 0/1 * *")
    void dayTask() {
        recommendBooks = recommendBooks();
    }

    public Map<String, List<BookDto>> recommendBooks() {
        Map<String, List<BookDto>> map = new HashMap<>();
        map.put("praise", baseMapper.descDtoByPraise());
        map.put("love", baseMapper.descDtoByLove());
        map.put("newest", baseMapper.descDtoById());
        return map;
    }

    public void redisBookReadSetback(Message message) {
        String key = stringRedisSerializer.deserialize(message.getBody());
        String[] split = key.split(":");
        String map = split[0];
        key = split[1];
        Long value = Long.parseLong(redisService.getExpireShadow(map, key).toString());
        Book book = getById(Long.parseLong(key));
        book.setVisit(value);
        updateById(book);
    }

    public Page<ESBook> searchBook(String keyword, Pageable pageable) {
        return repository.findESBooksByNameContainsOrTag1OrTag2OrTag3OrDescriptionContains(
                keyword, keyword, keyword, keyword, keyword, pageable);
    }

    public void indexESBook(ESBook esBook) {
        repository.index(esBook);
    }

    public void deleteESBook(ESBook esBook) {
        repository.delete(esBook);
    }

    public List<Book> listBookByAuthorLimited(Long aid, Long his, int size) {
        List<Book> bookList = baseMapper.listBookByAuthorLimited(aid, his, size);
        refreshListVisit(bookList);
        return bookList;
    }

    public List<BookDto> listDtoByAuthor(Long user, boolean open) {
        return baseMapper.listDtoByAuthor(user, open);
    }

    public List<BookDto> listCollectionDtoByUser(Long user, boolean open) {
        UserAct act = userActService.getById(user);
        List<Long> ids = StringTool.strings2LongList(act.getLove());
        return baseMapper.listDtoById(ids, open);
    }

    public List<BookDto> listDtoById(List<Long> ids, boolean open) {
        return baseMapper.listDtoById(ids, open);
    }

    public BookDto getDtoById(Long id, boolean open) {
        return baseMapper.getDtoById(id, open);
    }

    public void bookVisitCountUp(Book book) {
        try {
            String key = book.getId().toString();
            redisService.setOrInrExpireShadow(BOOK_READ_MAP_KEY, key, book.getVisit() + 1L, 1L, 5, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("redisService operation failed", e);
        }
    }

    private Map<String, Object> bookVisitGet(List<String> keys) {
        return redisService.hmget(BOOK_READ_MAP_KEY, keys);
    }

    @Override
    public Book getById(Serializable id) {
        Book book = super.getById(id);
        book.setVisit(Long.parseLong(redisService.hget(BOOK_READ_MAP_KEY, book.getId().toString()).toString()));
        return book;
    }


    public Collection<Book> listByIds(Collection<? extends Serializable> idList, boolean visit) {
        List<Book> bookList = new ArrayList<>(super.listByIds(idList));
        if (visit) {
            refreshListVisit(bookList);
        }
        return bookList;
    }

    private void refreshListVisit(List<Book> bookList) {
        List<String> ids = new ArrayList<>(bookList.size());
        bookList.forEach(v -> ids.add(v.getId().toString()));
        Map<String, Object> map = bookVisitGet(ids);
        bookList.forEach(v -> {
            Object o = map.get(v.getId().toString());
            if (o != null) {
                Long value = Long.parseLong(o.toString());
                v.setVisit(value);
            }
        });
    }

}
