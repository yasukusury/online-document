package org.yasukusury.onlinedocument.biz.controller;

import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.yasukusury.onlinedocument.biz.entity.User;
import org.yasukusury.onlinedocument.biz.entity.UserAct;
import org.yasukusury.onlinedocument.biz.service.BookService;
import org.yasukusury.onlinedocument.biz.service.SearchService;
import org.yasukusury.onlinedocument.biz.service.UserActService;
import org.yasukusury.onlinedocument.biz.service.UserService;
import org.yasukusury.onlinedocument.commons.base.BaseController;
import org.yasukusury.onlinedocument.commons.constant.ConstantWebContext;
import org.yasukusury.onlinedocument.commons.json.Result;
import org.yasukusury.onlinedocument.commons.utils.Md5Utils;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author 30254
 * creadtedate: 2018/11/27
 */
@RestController
@CommonsLog
@ApiIgnore
public class LoginController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserActService userActService;

    @Autowired
    private SearchService searchService;

    @Autowired
    private BookService bookService;

    @GetMapping({"/","/index"})
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView("/index");
        modelAndView.addObject("hotTag",searchService.getHotTags());
        modelAndView.addObject("maxTag",searchService.getMaxTags());
        modelAndView.addObject("recommendBooks", bookService.getRecommendBooks());
        giveCUser(modelAndView);
        return modelAndView;
    }

    @GetMapping("/login")
    public ModelAndView login(Model model){
        return new ModelAndView("/login");
    }

    @PostMapping("/login")
    public Result login(User user) {
        Result result = new Result();
        User cUser = (User) session.getAttribute(ConstantWebContext.REQUEST_C_USER);
        if (cUser == null) {
            String password = user.getPassword();
            user.setPassword(null);
            cUser = userService.getByExample(user);
            if (cUser != null) {
                String md5 = new Md5Utils().getkeyBeanofStr(password).toUpperCase();
                if (cUser.getPassword().equals(md5)) {
                    session.setAttribute(ConstantWebContext.REQUEST_C_USER, cUser);
                    result.setMsg("登录成功，欢迎" + user.getUsername() + "回来");
                    result.setSuccess(true);
                    return result;
                }
            }
            result.setMsg("用户名与密码不对应");
        }
        return result;
    }

    @GetMapping("/register")
    public ModelAndView register(){
        ModelAndView modelAndView = new ModelAndView("/user/register");
        modelAndView.addObject("user", new User());
        return modelAndView;
    }

    @PostMapping("/register")
    public Result register(User user){
        Result result = new Result();
        try {
            userService.save(user);
            userActService.save(new UserAct());
            session.setAttribute(ConstantWebContext.REQUEST_C_USER, user);
            result.setSuccess(true);
            result.setMsg("注册成功");
        }catch (Exception e){
            result.setMsg("注册失败");
        }
        return result;
    }

    @PostMapping("/logout")
    public Result logout(){
        Result result = new Result();
        try {
            session.setAttribute(ConstantWebContext.REQUEST_C_USER, null);
            result.setSuccess(true);
            result.setMsg("已登出");
        } catch (Exception e){
            result.setMsg("登出失败");
        }
        return result;
    }
}
