package org.yasukusury.onlinedocument.biz.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.yasukusury.onlinedocument.biz.dto.BookDto;
import org.yasukusury.onlinedocument.biz.entity.ESBook;
import org.yasukusury.onlinedocument.biz.entity.ESChapter;
import org.yasukusury.onlinedocument.biz.service.BookService;
import org.yasukusury.onlinedocument.biz.service.ChapterService;
import org.yasukusury.onlinedocument.biz.service.SearchService;
import org.yasukusury.onlinedocument.commons.base.BaseController;
import org.yasukusury.onlinedocument.commons.json.Result;
import org.yasukusury.onlinedocument.commons.json.SearchResult;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 30254
 * creadtedate: 2019/2/23
 */
@RestController
@RequestMapping("/search")
public class SearchController extends BaseController {

    @Autowired
    private BookService bookService;

    @Autowired
    private SearchService searchService;

    @Autowired
    private ChapterService chapterService;

    @GetMapping("/")
    public ModelAndView search(){
        ModelAndView modelAndView = ofView("/search");
        modelAndView.addObject("hotTag",searchService.getHotTags());
        modelAndView.addObject("maxTag",searchService.getMaxTags());
        return modelAndView;
    }


    @GetMapping("/book")
    @ApiOperation("搜索书籍")
    public Result searchBook(@ApiParam("关键字") @RequestParam(value = "keyword") String keyword){
        Result result = new Result();
        try {
            Page<ESBook> page = bookService.searchBook(keyword, getSearchPage());
            List<BookDto> dtoList = searchService.searchBook(page);
            result.setData(dtoList);
            result.setSuccess(true);
            result.setMsg("搜索成功");
        } catch (Exception e) {
            result.setMsg("搜索失败");
        }
        return result;
    }

    @GetMapping("/chapter")
    @ApiOperation("搜索章节")
    public Result searchChapter(@ApiParam("关键字") @RequestParam(value = "keyword") String keyword){
        Result result = new Result();
        try {
            Page<ESChapter> page = chapterService.searchChapter("content", keyword, getSearchPage());
            List<SearchResult> resultList = searchService.searchChapter(page);
            result.setData(resultList);
            result.setSuccess(true);
            result.setMsg("搜索成功");
        } catch (Exception e) {
            result.setMsg("搜索失败");
        }
        return result;
    }

    @GetMapping("/hotTag")
    @ApiOperation("近一月人气标签")
    public Result searchHotTag(){
        Result result = new Result();
        try{
            List<String> hotTags = searchService.getHotTags();
            result.setData(hotTags);
            result.setSuccess(true);
            result.setMsg("获取成功");
        }catch (Exception e){
            result.setMsg("获取失败");
        }
        return result;
    }

    @GetMapping("/maxTag")
    @ApiOperation("总人气标签")
    public Result searchMaxTag(){
        Result result = new Result();
        try{
            List<String> maxTags = searchService.getMaxTags();
            result.setData(maxTags);
            result.setSuccess(true);
            result.setMsg("获取成功");
        }catch (Exception e){
            result.setMsg("获取失败");
        }
        return result;
    }
}
