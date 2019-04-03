package org.yasukusury.onlinedocument;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.yasukusury.onlinedocument.biz.entity.Book;
import org.yasukusury.onlinedocument.biz.service.BookService;
import org.yasukusury.onlinedocument.commons.utils.spring.SpringContextHolder;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OnlineDocumentApplicationTests {

    @Test
    public void contextLoads() {
    }

    @Test
    public void mybatisTest(){
        BookService bookService = SpringContextHolder.getBean(BookService.class);
        List<Book> bookList = bookService.listBookByAuthorLimited(1L, null, 1);
        System.out.println(bookList);
        Book book = bookList.get(0);
        System.out.println(book.getAuthorR());
        System.out.println(book.getChaptersR());
    }

}
