package org.yasukusury.onlinedocument.biz.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.yasukusury.onlinedocument.biz.entity.User;
import org.yasukusury.onlinedocument.biz.entity.UserAct;
import org.yasukusury.onlinedocument.biz.service.BookService;
import org.yasukusury.onlinedocument.biz.service.BookmarkService;
import org.yasukusury.onlinedocument.biz.service.UserActService;
import org.yasukusury.onlinedocument.biz.service.UserService;
import org.yasukusury.onlinedocument.commons.base.BaseController;
import org.yasukusury.onlinedocument.commons.json.Result;
import springfox.documentation.annotations.ApiIgnore;


/**
 * @author 30254
 * creadtedate: 2018/12/1
 */
@RestController
@CommonsLog
@RequestMapping("/user")
@Api("/user")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserActService userActService;

    @Autowired
    private BookmarkService bookmarkService;

    @Autowired
    private BookService bookService;

    @ModelAttribute
    public User user(@PathVariable(name = "uid", required = false) Long id) {
        if (id != null && id > 0) {
            return userService.getById(id);
        }
        return new User();
    }

    private ModelAndView direct2Person(User user, String viewName) {
        ModelAndView modelAndView = ofView(viewName);
        modelAndView.addObject("user", user != null ? user : getCUser());
        modelAndView.addObject("self", user != null && getCUser() != null && user.getId().equals(getCUser().getId()));
        return modelAndView;
    }

    @GetMapping("/checkName")
    @ApiOperation("检查用户名存在")
    public Result checkName(@ApiParam("用户名")@RequestParam(value = "name") String name) {
        Result result = new Result();
        try {
            result.setData(userService.isUsernameExist(name));
            result.setSuccess(true);
            result.setMsg("检查成功");
        } catch (Exception e) {
            result.setMsg("检查失败");
        }
        return result;
    }

    @GetMapping("/act")
    @ApiOperation("获取用户活动信息")
    public Result act(){
        Result result = new Result();
        try {
            if (!requiredLogin(result)){
                return result;
            }
            UserAct userAct = userActService.getById(getCUser().getId());
            result.setSuccess(true);
            result.setData(userAct);
            result.setMsg("赞操作成功");
        } catch (Exception e) {
            result.setMsg("赞操作失败");
        }
        return result;
    }

    @GetMapping("/info/{uid}")
    @ApiIgnore
    public ModelAndView info(@ModelAttribute User user) {
        ModelAndView modelAndView = direct2Person(user, "/user/info");
        modelAndView.addObject("title", "info");
        return modelAndView;
    }

    @PostMapping("/info")
    public Result infoEdit(User user) {
        Result result = new Result();
        User cUser = getCUser();
        try {
            cUser.setGender(user.getGender());
            cUser.setIntroduction(user.getIntroduction());
            cUser.setEmail(user.getEmail());
            userService.updateById(cUser);
            result.setSuccess(true);
            result.setMsg("修改成功");
        } catch (Exception e) {
            result.setMsg("修改失败");
        }
        return result;
    }

    @GetMapping("/portrait/{uid}")
    @ApiIgnore
    public ModelAndView portrait(@ModelAttribute User user) {
        return direct2Person(user, "/user/portrait");
    }

    @PostMapping("/portrait")
    public Result portraitEdit(User user) {
        Result result = new Result();
        try {
            User cUser = getCUser();
            cUser.setPortrait(user.getPortrait());
            userService.updateById(user);
            result.setSuccess(true);
            result.setMsg("保存成功");
            userService.save(cUser);
        } catch (Exception e) {
            result.setMsg("保存失败");
        }
        return result;
    }

    @GetMapping("/bookList/{uid}")
    @ApiIgnore
    public ModelAndView bookList(@ModelAttribute User user) {
        ModelAndView modelAndView = direct2Person(user, "/user/bookList");
        modelAndView.addObject("title", "bookList");
        modelAndView.addObject("bookList",
                bookService.listDtoByAuthor(user.getId(), getCUser() == null || !getCUser().getId().equals(user.getId())));
        return modelAndView;
    }

    @GetMapping("/collection/{uid}")
    @ApiIgnore
    public ModelAndView collection(@ModelAttribute User user) {
        ModelAndView modelAndView = direct2Person(user, "/user/collection");
        modelAndView.addObject("title", "collection");
        modelAndView.addObject("bookList",
                bookService.listCollectionDtoByUser(user.getId(), getCUser() == null || !getCUser().getId().equals(user.getId())));
        return modelAndView;
    }

    @GetMapping("/newBook/{aid}")
    @ApiIgnore
    public ModelAndView newBook(@PathVariable(required = false,name = "aid")Long id){
        ModelAndView modelAndView = ofView("/book/book");
        modelAndView.addObject("author", userService.getById(id));
        modelAndView.addObject("self", getCUser().getId().equals(id));
        return modelAndView;
    }

    @GetMapping("/bookmark/")
    @ApiIgnore
    public ModelAndView bookmark() throws Exception {
        if (!requiredLogin(true)){
            return null;
        }
        Long id = getCUser().getId();
        ModelAndView modelAndView = ofView("/user/bookmark");
        modelAndView.addObject("bookmarkList", bookmarkService.listDtoByUser(id));
        return modelAndView;
    }
}
