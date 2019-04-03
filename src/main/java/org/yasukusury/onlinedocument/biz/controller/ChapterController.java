package org.yasukusury.onlinedocument.biz.controller;

import com.alibaba.fastjson.JSON;
import io.swagger.annotations.*;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.yasukusury.onlinedocument.biz.entity.Book;
import org.yasukusury.onlinedocument.biz.entity.Chapter;
import org.yasukusury.onlinedocument.biz.entity.ESChapter;
import org.yasukusury.onlinedocument.biz.service.BookService;
import org.yasukusury.onlinedocument.biz.service.ChapterService;
import org.yasukusury.onlinedocument.biz.service.FileService;
import org.yasukusury.onlinedocument.commons.base.BaseController;
import org.yasukusury.onlinedocument.commons.json.Result;

/**
 * @author 30254
 * creadtedate: 2019/2/12
 */
@RestController
@CommonsLog
@RequestMapping("/chapter")
@Api("/chapter")
public class ChapterController extends BaseController {

    @Autowired
    private FileService fileService;

    @Autowired
    private BookService bookService;

    @Autowired
    private ChapterService chapterService;

    @ModelAttribute
    public Book book(@PathVariable(name = "bid", required = false) Long id) {
        if (id != null && id > 0) {
            return bookService.getById(id);
        }
        return new Book();
    }

    @ModelAttribute
    public Chapter chapter(@PathVariable(name = "cid", required = false) Long cid) {
        if (cid != null && cid > 0) {
            return chapterService.getById(cid);
        }
        return new Chapter();
    }

    @PostMapping("/edit/b{bid}")
    @Transactional(rollbackFor = Exception.class)
    @ApiOperation("更新书籍章节")
    @ApiImplicitParam(name = "书籍id", paramType = "path")
    public Result edit(@ModelAttribute Book book
            , @ApiParam(value = "章节json列表", name = "chapterList"
            , example = "[\"id\":1,\"name\":\"chapter1.2\",\"pid\":\"2\",\"valid\":true]")
            @RequestParam("chapterList") String chapterJson) {
        Result result = new Result();
        try {
            chapterService.updateChapters(JSON.parseArray(chapterJson, Chapter.class), book);
            result.setSuccess(true);
            result.setMsg("保存文档成功");
        } catch (Exception e) {
            result.setMsg("保存文档失败");
        }
        return result;
    }

    @PostMapping("/text/b{bid}/c{cid}")
    @Transactional(rollbackFor = Exception.class)
    @ApiImplicitParams({@ApiImplicitParam(value = "书籍id", name = "bid", paramType = "path", required = true)
            ,@ApiImplicitParam(value = "章节id,若0则为书籍首页", name = "cid", paramType = "path", required = true)})
    public Result text(@ModelAttribute Book book, @ModelAttribute Chapter chapter
            , @ApiParam(value = "md内容")@RequestParam(value = "md", required = false) String md
            , @ApiParam(value = "html内容")@RequestParam(value = "html", required = false) String html
            , @ApiParam(value = "是则添加,否则移除")@RequestParam("set") Boolean set) {
        Result result = new Result();
        try {
            ESChapter esChapter = ESChapter.builder().id(chapter.getId().toString())
                    .bookId(book.getId()).build();
            if (set) {
                chapter.setUrl(true);
                fileService.mdFile(md, html, chapter);
                chapterService.indexESChapter(esChapter, html);
            } else {
                chapter.setUrl(false);
                chapterService.deleteESChapter(esChapter);
            }
            chapterService.updateById(chapter);
            result.setSuccess(true);
            result.setMsg("保存内容成功");
        } catch (Exception e) {
            result.setMsg("保存内容失败");
        }
        return result;
    }

    @GetMapping("/text/b{bid}/c{cid}")
    @ApiOperation("获取章节md内容")
    @ApiImplicitParams({@ApiImplicitParam(value = "书籍id", name = "bid", paramType = "path", required = true)
            ,@ApiImplicitParam(value = "章节id,若0则为书籍首页", name = "cid", paramType = "path", required = true)})
    public Result md(@ModelAttribute Book book, @ModelAttribute Chapter chapter) {
        Result result = new Result();
        try {
            String md = fileService.mdFile(chapter);
//            md = md.replaceAll("\\\\", "\\\\");
//            md = StringEscapeUtils.escapeJson(md);
//            md = md.replaceAll("\t", "    ");
//            md = md.replaceAll("\"", "\\\\\"");
            result.setData(md);
            result.setSuccess(true);
            result.setMsg("获取内容成功");
        } catch (Exception e) {
            result.setMsg("获取内容失败");
        }
        return result;
    }

    @GetMapping("/read/b{bid}/c{cid}")
    @ApiOperation("获取章节html内容")
    @ApiImplicitParams({@ApiImplicitParam(value = "书籍id", name = "bid", paramType = "path", required = true)
            ,@ApiImplicitParam(value = "章节id,若0则为书籍首页", name = "cid", paramType = "path", required = true)})
    public Result html(@ModelAttribute Book book, @ModelAttribute Chapter chapter) {
        Result result = new Result();
        try {
            String html = fileService.htmlFile(chapter);
//            html = html.replaceAll("\"", "\\\\\"");
            result.setData(html);
            result.setSuccess(true);
            result.setMsg("获取内容成功");
        } catch (Exception e) {
            result.setMsg("获取内容失败");
        }
        return result;
    }
}
