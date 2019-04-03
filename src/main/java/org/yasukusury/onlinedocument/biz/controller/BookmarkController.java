package org.yasukusury.onlinedocument.biz.controller;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.yasukusury.onlinedocument.biz.dto.BookmarkDto;
import org.yasukusury.onlinedocument.biz.entity.Bookmark;
import org.yasukusury.onlinedocument.biz.service.BookService;
import org.yasukusury.onlinedocument.biz.service.BookmarkService;
import org.yasukusury.onlinedocument.biz.service.ChapterService;
import org.yasukusury.onlinedocument.commons.base.BaseController;
import org.yasukusury.onlinedocument.commons.json.Result;

import java.io.IOException;
import java.util.List;

/**
 * @author 30254
 * creadtedate: 2019/3/4
 */
@RestController
@RequestMapping("/bookmark")
public class BookmarkController extends BaseController {

    @Autowired
    private BookmarkService bookmarkService;

    @GetMapping("/list")
    @ApiOperation("获取所有书签")
    public Result list() throws Exception {
        Result result = new Result();
        if (!requiredLogin(result)) {
            return result;
        }
        try {
            Long id = getCUser().getId();
            List<BookmarkDto> bookmarks = bookmarkService.listDtoByUser(id);
            result.setData(bookmarks);
            result.setSuccess(true);
            result.setMsg("获取书签失败");
        } catch (Exception e) {
            result.setMsg("获取书签失败");
        }
        return result;
    }

    @GetMapping("/get/b{bid}")
    @ApiOperation("获取书签")
    @ApiImplicitParam(value = "书籍id", name = "bid", paramType = "path")
    public Result get(@PathVariable(value = "bid") Long book) throws Exception {
        Result result = new Result();
        if (!requiredLogin(result)) {
            return result;
        }
        try {
            Long id = getCUser().getId();
            BookmarkDto dto = bookmarkService.getDtoByUserAndBook(id, book);
            result.setData(dto);
            result.setSuccess(true);
            result.setMsg("获取书签成功");
        } catch (Exception e) {
            result.setMsg("获取书签失败");
        }
        return result;
    }
    @PostMapping("/mark/b{bid}/c{cid}")
    @ApiOperation("记录标签")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "书籍id", name = "bid", paramType = "path"),
            @ApiImplicitParam(value = "章节id", name = "cid", paramType = "path")
    })
    public Result mark(@ApiParam("阅读进度") @RequestParam(value = "progress") Double progress
            , @PathVariable(value = "bid", required = false) Long book
            , @PathVariable(value = "cid", required = false) Long chapter) throws Exception {
        Result result = new Result();
        try {
            if (!requiredLogin(result)) {
                return result;
            }
            Bookmark bookmark = null;
            Long user = getCUser().getId();
            if (user != null && user > 0 && book != null && book > 0) {
                bookmark = bookmarkService.getByUserAndBook(user, book);
            }
            if(bookmark == null) {
                bookmark = new Bookmark();
            }
            bookmark.setUser(user);
            bookmark.setBook(book);
            bookmark.setProgress(progress);
            bookmark.setChapter(chapter);
            bookmarkService.saveOrUpdate(bookmark);

            result.setData(bookmarkService.getDtoByUserAndBook(getCUser().getId(), book));
            result.setSuccess(true);
            result.setMsg("记录书签成功");
        } catch (Exception e) {
            result.setMsg("记录书签失败");
        }
        return result;
    }

    @PostMapping("/remove/b{bid}")
    @ApiOperation("去除标签")
    @ApiImplicitParam(value = "书籍id", name = "bid", paramType = "path")
    public Result remove(@PathVariable(value = "bid", required = false) Long book) throws Exception {
        Result result = new Result();
        try {
            if (!requiredLogin(result)) {
                return result;
            }
            Bookmark bookmark;
            Long user = getCUser().getId();
            if (user != null && user > 0 && book != null && book > 0) {
                bookmark = bookmarkService.getByUserAndBook(user, book);
            } else {
                bookmark = new Bookmark();
            }
            bookmarkService.remove(bookmark);
            result.setSuccess(true);
            result.setMsg("记录书签成功");
        } catch (Exception e) {
            result.setMsg("记录书签失败");
        }
        return result;
    }
}
