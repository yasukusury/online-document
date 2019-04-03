package org.yasukusury.onlinedocument.biz.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.yasukusury.onlinedocument.biz.dto.BookDto;
import org.yasukusury.onlinedocument.biz.dto.BookmarkDto;
import org.yasukusury.onlinedocument.biz.entity.Book;
import org.yasukusury.onlinedocument.biz.entity.ESBook;
import org.yasukusury.onlinedocument.biz.entity.User;
import org.yasukusury.onlinedocument.biz.entity.UserAct;
import org.yasukusury.onlinedocument.biz.service.*;
import org.yasukusury.onlinedocument.commons.base.BaseController;
import org.yasukusury.onlinedocument.commons.json.Result;
import springfox.documentation.annotations.ApiIgnore;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * @author 30254
 * creadtedate: 2018/12/5
 */
@RestController
@CommonsLog
@RequestMapping("/book")
@Api("/book")
public class BookController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserActService userActService;

    @Autowired
    private BookService bookService;

    @Autowired
    private BookmarkService bookmarkService;

    @Autowired
    private ChapterService chapterService;

    @Autowired
    private FileService fileService;

    @ModelAttribute
    public Book book(@PathVariable(name = "bid", required = false) Long id) {
        if (id != null && id > 0) {
            return bookService.getById(id);
        }
        return new Book();
    }

    private ModelAndView direct2Edit(Long userId, String viewName) {
        ModelAndView modelAndView = ofView(viewName);
        modelAndView.addObject("user", userId != null ? userService.getById(userId) : getCUser());
        modelAndView.addObject("self", userId == null || getCUser() == null ? null : userId.equals(getCUser().getId()));
        return modelAndView;
    }

    @GetMapping("/list/{aid}")
    @ApiOperation("获取作者书籍列表")
    @ApiImplicitParam(value = "用户id", name = "aid", paramType = "path", required = true)
    public Result list(@PathVariable("aid") Long aid
            , @ApiParam(value = "上次最后一个书籍id", example = "1") @RequestParam(name = "his", required = false) Long his) {
        Result result = new Result();
        try {
//            List<Book> bookList = bookService.listBookByAuthorLimited(aid, his, 10);
            List<BookDto> dtoList = bookService.listDtoByAuthor(aid, getCUser() == null || !getCUser().getId().equals(aid));
            result.setData(dtoList);
            result.setSuccess(true);
            result.setMsg("获取书单成功");
        } catch (Exception e) {
            result.setMsg("获取书单失败");
        }
        return result;
    }

    @GetMapping("/book/b{bid}")
    @ApiIgnore
    public ModelAndView book(@ModelAttribute Book book) throws IOException {
        ModelAndView modelAndView = ofView("/book/book");
        boolean b = requiredSelf(book.getAuthor());
        if (!b) {
            return null;
        }
        modelAndView.addObject("author", userService.getById(book.getAuthor()));
        modelAndView.addObject("self", true);
        return modelAndView;
    }

    @PostMapping("/book")
    @ApiOperation("修改书籍")
    public Result edit(@ApiParam(value = "书籍", name = "book", required = true) Book book) {
        Result result = new Result();
        try {
            bookService.saveOrUpdate(book);
            ESBook esBook = ESBook.builder().author(book.getAuthor()).description(book.getIntroduction())
                    .id(book.getId().toString()).name(book.getName()).updateTime(new Date()).build();
            String[] tags = book.getTags().split(",");
            if (tags.length > 0) {
                esBook.setTag1(tags[0]);
                if (tags.length > 1) {
                    esBook.setTag2(tags[1]);
                    if (tags.length > 2) {
                        esBook.setTag3(tags[2]);
                    }
                }
            }
            bookService.indexESBook(esBook);
            result.setSuccess(true);
            result.setMsg("书籍保存成功");
        } catch (Exception e) {
            result.setMsg("书籍保存失败");
        }
        return result;
    }

    @PostMapping("/book/b{bid}")
    @ApiOperation("删除书籍")
    @ApiImplicitParam(value = "书籍id", name = "bid", paramType = "path", required = true)
    public Result delete(@ModelAttribute Book book) {
        Result result = new Result();
        try {
            bookService.remove(book);
            ESBook esBook = ESBook.builder().id(book.getId().toString()).build();
            bookService.deleteESBook(esBook);
            result.setSuccess(true);
            result.setMsg("书籍删除成功");
        } catch (Exception e) {
            result.setMsg("书籍删除失败");
        }
        return result;
    }

    @GetMapping({"/document/b{bid}", "/document/{mode}/b{bid}"})
    @ApiIgnore
    public ModelAndView document(@ModelAttribute Book book, @PathVariable(name = "mode", required = false) String mode) throws IOException {
        ModelAndView modelAndView;
        if ("edit".equals(mode)) {
            boolean b = requiredSelf(book.getAuthor());
            if (!b) {
                return null;
            }
            modelAndView = ofView("/book/document-edit");
        } else {
            modelAndView = ofView("/book/document");
        }
        chapterService.warpBook(book);
        User author = userService.getById(book.getId());
        BookDto dto = BookDto.toDto(book, author);
        modelAndView.addObject("book", dto);
        if (!selfVisit(book.getAuthor())) {
            bookService.bookVisitCountUp(book);
        }
        if (getCUser() != null) {
            BookmarkDto bookmarkDto = bookmarkService.getDtoByUserAndBook(getCUser().getId(), book.getId());
            UserAct userAct = userActService.getById(getCUser().getId());
            modelAndView.addObject("bookmark", bookmarkDto);
            modelAndView.addObject("act", userAct);
        }
        return modelAndView;
    }

    @GetMapping("/b{bid}")
    @ApiOperation("获取书籍")
    public Result document(@ModelAttribute Book book) {
        Result result = new Result();
        try {
            chapterService.warpBook(book);
            User author = userService.getById(book.getId());
            BookDto dto = BookDto.toDto(book, author);
            result.setData(dto);
            result.setSuccess(true);
            result.setMsg("获取文档成功");
        } catch (Exception e) {
            result.setMsg("获取文档失败");
        }
        return result;
    }

    @GetMapping("/download/b{bid}")
    @ApiOperation("下载书籍")
    @ApiImplicitParam(value = "书籍id", name = "bid", paramType = "path")
    public Result download(@ModelAttribute Book book, @ApiParam("下载格式") @RequestParam("format") String format) throws Exception {
        Result result = new Result();
        int count = chapterService.countUrlBookChapter(book.getId());
        if (count >= 5) {
            chapterService.warpBook(book);
            File file;
            switch (format) {
                case "docx":
                    file = fileService.packBook2DOCX(BookDto.toDto(book, userService.getById(book.getAuthor())));
                    break;
                case "pdf":
                    file = fileService.packBook2PDF(BookDto.toDto(book, userService.getById(book.getAuthor())));
                    break;
                default:
                case "html":
                    file = fileService.packBook2HTMLs(BookDto.toDto(book, userService.getById(book.getAuthor())));
            }
            String path = file.getCanonicalPath();
            result.setData(path.substring(path.indexOf("\\upload")).replaceAll("\\\\", "/"));
            result.setSuccess(true);
            result.setMsg("下载允许");
        } else {
            result.setMsg("下载禁止，书籍所含实际性页面少于5");
        }
        return result;
    }

    @PostMapping("/love/b{bid}")
    @ApiOperation("喜欢书籍")
    @ApiImplicitParam(value = "书籍id", name = "bid", paramType = "path")
    public Result love(@ModelAttribute Book book, @ApiParam("喜欢方向") @RequestParam("good") Boolean love) {
        Result result = new Result();
        try {
            if (!requiredLogin(result)) {
                return result;
            }
            long cnt = book.getLove();
            if (love) {
                ++cnt;
            } else {
                --cnt;
            }
            userActService.userLove(getCUser().getId(), book.getId(), love);
            book.setLove(cnt);
            bookService.updateById(book);
            result.setData(cnt);
            result.setSuccess(true);
            result.setMsg("喜欢操作成功");
        } catch (Exception e) {
            result.setMsg("喜欢操作失败");
        }
        return result;
    }

    @PostMapping("/praise/b{bid}")
    @ApiOperation("赞书籍")
    @ApiImplicitParam(value = "书籍id", name = "bid", paramType = "path")
    public Result praise(@ModelAttribute Book book, @ApiParam("赞方向") @RequestParam("good") Boolean praise) {
        Result result = new Result();
        try {
            if (!requiredLogin(result)) {
                return result;
            }
            long cnt = book.getPraise();
            if (praise) {
                ++cnt;
            } else {
                --cnt;
            }
            userActService.userBookPraise(getCUser().getId(), book.getId(), praise);
            book.setPraise(cnt);
            bookService.updateById(book);
            result.setData(cnt);
            result.setSuccess(true);
            result.setMsg("赞操作成功");
        } catch (Exception e) {
            result.setMsg("赞操作失败");
        }
        return result;
    }


}
