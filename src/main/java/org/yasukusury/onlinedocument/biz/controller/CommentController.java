package org.yasukusury.onlinedocument.biz.controller;

import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.yasukusury.onlinedocument.biz.dto.CommentDto;
import org.yasukusury.onlinedocument.biz.entity.Comment;
import org.yasukusury.onlinedocument.biz.service.CommentService;
import org.yasukusury.onlinedocument.biz.service.UserActService;
import org.yasukusury.onlinedocument.commons.base.BaseController;
import org.yasukusury.onlinedocument.commons.json.Result;

import java.util.List;

/**
 * @author 30254
 * creadtedate: 2019/3/4
 */
@RestController
@RequestMapping("/comment")
@Api("留言")
public class CommentController extends BaseController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserActService userActService;

    @GetMapping("/list/c{cid}")
    @ApiOperation("获取所有留言")
    @ApiImplicitParam(value = "章节id", name = "cid", paramType = "path")
    public Result list(@PathVariable(value = "cid", required = false) Long chapter){
        Result result = new Result();
        try {
            if (!requiredLogin(result)) {
                return result;
            }
            List<CommentDto> dtos = commentService.listDtoByChapter(chapter);
            result.setData(dtos);
            result.setSuccess(true);
            result.setMsg("获取留言成功");
        } catch (Exception e){
            result.setMsg("获取留言失败");
        }
        return result;
    }

    @PostMapping("/mark/c{cid}")
    @ApiOperation("写留言")
    @ApiImplicitParam(value = "章节id", name = "cid", paramType = "path")
    public Result mark(@PathVariable(value = "cid", required = false) Long chapter
            , @RequestParam("content")String content){
        Result result = new Result();
        try {
            if (!requiredLogin(result)) {
                return result;
            }
            Comment comment = Comment.builder().user(getCUser().getId()).chapter(chapter).content(content).build();
            commentService.save(comment);
            CommentDto commentDto = CommentDto.toDto(comment, getCUser());
            result.setData(commentDto);
            result.setSuccess(true);
            result.setMsg("写留言成功");
        } catch (Exception e){
            result.setMsg("写留言失败");
        }
        return result;
    }


    @PostMapping("/praise/r{rid}")
    @ApiOperation("赞留言")
    @ApiImplicitParam(value = "留言id", name = "rid", paramType = "path")
    public Result praise(@PathVariable("rid") Long rId, @ApiParam("赞方向")@RequestParam("good")Boolean praise) {
        Result result = new Result();
        try {
            if (!requiredLogin(result)){
                return result;
            }
            Comment comment = commentService.getById(rId);
            long cnt = comment.getPraise();
            if (praise) {
                ++cnt;
            } else {
                --cnt;
            }
            userActService.userCommentPraise(getCUser().getId(), comment.getId(), praise);
            comment.setPraise(cnt);
            commentService.updateById(comment);
            result.setData(cnt);
            result.setSuccess(true);
            result.setMsg("赞操作成功");
        } catch (Exception e) {
            result.setMsg("赞操作失败");
        }
        return result;
    }
//
//    @GetMapping("/get/u{uid}/b{bid}/c{cid}")
//    @ApiOperation("获取所有评论")
//    public Result list(@PathVariable(value = "bid", required = false) Long book
//            , @PathVariable(value = "cid", required = false) Long chapter
//            , @PathVariable(value = "uid", required = false) Long user){
//        Result result = new Result();
//        try {
//            List<CommentDto> dtos = commentService.listDtoByChapter(book, chapter);
//            result.setData(dtos);
//            result.setSuccess(true);
//            result.setMsg("获取评论成功");
//        } catch (Exception e){
//            result.setMsg("获取评论失败");
//        }
//        return result;
//    }
}
