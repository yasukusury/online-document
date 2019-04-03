package org.yasukusury.onlinedocument.commons.base;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.yasukusury.onlinedocument.commons.constant.ConstantPagination;
import org.yasukusury.onlinedocument.commons.constant.ConstantWebContext;
import org.yasukusury.onlinedocument.commons.json.Result;
import org.yasukusury.onlinedocument.commons.utils.spring.StringToTimeConverter;
import org.yasukusury.onlinedocument.biz.entity.User;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.beans.PropertyEditorSupport;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.security.Timestamp;
import java.util.Date;


public abstract class BaseController {
    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    protected HttpServletRequest request;

    @Autowired
    protected HttpServletResponse response;

    @Autowired
    protected HttpSession session;

    @Autowired
    protected ServletContext application;


    /**
     * 初始化数据绑定
     * 1. 将所有传递进来的String进行HTML编码，防止XSS攻击
     * 2. 将字段中Date类型转换为String类型
     */
    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        // String类型转换，将所有传递进来的String进行HTML编码，防止XSS攻击
        binder.registerCustomEditor(String.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                setValue(text == null ? null : text.trim());
            }

            @Override
            public String getAsText() {
                Object value = getValue();
                return value != null ? value.toString() : "";
            }
        });
        //String 转 Timestamp
        binder.registerCustomEditor(Timestamp.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String value) {
                setValue(StringToTimeConverter.convert(value));
            }
        });
        // Date 类型转换
        binder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                Date date;
                try {
                    System.out.println("===日期转换==");
                    date = DateUtils.parseDate(text, new String[]{"yyyy-MM-dd", "yyyy-MM-dd hh:mm:ss"});
                } catch (Exception e) {
                    date = new Date();
                }
                setValue(date);
            }
        });

    }


    /**
     * 系统异常处理
     *
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(Exception e) {
        log.error("-->系统发生异常", e);
        ModelAndView model = new ModelAndView();
        model.addObject("message", e.getMessage());
        model.setViewName("error");
        return model;
    }

    protected String getPara(String param){
        String value = request.getParameter(param);
        if (null != value && !value.isEmpty()) {
            try {
                return URLDecoder.decode(value.replaceAll("%", "%25"), "UTF-8").trim();
            } catch (UnsupportedEncodingException e) {
                LoggerFactory.getLogger(BaseController.class).error("decode异常：" + value);
                return value;
            }
        }
        return value;
    }

    /**
     * 重定向至地址 url
     * @param url 请求地址
     * @return
     */
    protected String redirectTo(String url) {
        StringBuffer rto = new StringBuffer("redirect:");
        rto.append(url);
        return rto.toString();
    }

    protected void addCookie(String domain, String path, boolean isHttpOnly,
                             String name, String value, int maxAge){
        Cookie cookie = new Cookie(name, value);

        // 所在域：比如a1.4bu4.com 和 a2.4bu4.com 共享cookie
        if(null != domain && !domain.isEmpty()){
            cookie.setDomain(domain);
        }

        // 设置cookie所在路径
        cookie.setPath("/");
        if(null != path && !path.isEmpty()){
            cookie.setPath(path);
        }

        // 是否只读
        try {
            cookie.setHttpOnly(isHttpOnly);
        } catch (Exception e) {
            log.error("servlet容器版本太低，servlet3.0以前不支持设置cookie只读" + e.getMessage());
        }

        // 设置cookie的过期时间
        if (maxAge > 0){
            cookie.setMaxAge(maxAge);
        }

        // 添加cookie
        response.addCookie(cookie);
    }

    /**
     * 上传文件的路径
     *
     * @param request
     * @return
     * @throws MalformedURLException
     */
    protected String getDocRoot(HttpServletRequest request) throws MalformedURLException {
        String path = request.getServletContext().getRealPath("/") + "upload";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    /**
     * 项目根路径
     * @return /sms/upload
     * @throws MalformedURLException
     */
    protected String getUploadRoot() throws MalformedURLException {
        return "/upload";
    }

    /**
     * 请求/WEB-INF/下的视图文件
     */
    @RequestMapping(value = "/toUrl", produces = "text/html;charset=utf-8")
    @ApiIgnore
    public String toUrl(String toUrl) {
        return (toUrl);
    }

    /**
     * 获取当前登录用户
     * @return
     */
    protected User getCUser(){
        return (User) session.getAttribute(ConstantWebContext.REQUEST_C_USER);
    }

    protected void giveCUser(ModelAndView modelAndView){
        User user = (User) session.getAttribute(ConstantWebContext.REQUEST_C_USER);
        boolean login = user != null;
        if (login) {
            modelAndView.addObject("cUser", user);
        }
        modelAndView.addObject("login", login);
    }


    protected <T> Page<T> getPage(){

        int current;
        if(request.getParameter(ConstantWebContext.REQUEST_PAGE_NUMBER) != null) {
            current = Integer.parseInt(getPara(ConstantWebContext.REQUEST_PAGE_NUMBER));
        }else if (request.getParameter(ConstantWebContext.REQUEST_PAGE) != null){
            current = Integer.parseInt(getPara(ConstantWebContext.REQUEST_PAGE));
        }else{
            current = ConstantPagination.DEFAULT_PAGE_NUMBER;
        }
        int size;
        if(request.getParameter(ConstantWebContext.REQUEST_PAGE_SIZE) != null) {
            size = Integer.parseInt(getPara(ConstantWebContext.REQUEST_PAGE_SIZE));
        }else if (request.getParameter(ConstantWebContext.REQUEST_LIMIT) != null){
            size = Integer.parseInt(getPara(ConstantWebContext.REQUEST_LIMIT));
        }else{
            size = ConstantPagination.DEFAULT_PAGE_SIZE;
        }
        String orderField = BaseModel.ID;
        if(StringUtils.isNotEmpty(request.getParameter(ConstantWebContext.REQUEST_ORDER_COLUNM))) {
            orderField = getPara(ConstantWebContext.REQUEST_ORDER_COLUNM);
        }
        Page<T> page = new Page<>(current, size);
        page.setDesc(orderField);

        return page;
    }

    protected Pageable getSearchPage(){

        int current;
        if(request.getParameter(ConstantWebContext.SEARCH_PAGE_KEY) != null) {
            current = Integer.parseInt(getPara(ConstantWebContext.SEARCH_PAGE_KEY));
        }else{
            current = ConstantPagination.DEFAULT_PAGE;
        }
        int size;
        if(request.getParameter(ConstantWebContext.SEARCH_SIZE_KEY) != null) {
            size = Integer.parseInt(getPara(ConstantWebContext.SEARCH_SIZE_KEY));
        }else{
            size = ConstantPagination.DEFAULT_SIZE;
        }

        return PageRequest.of(current, size);
    }


    protected void redirIndex() throws IOException {
        response.sendRedirect("/");
    }

    protected void redirIndex2Login() throws IOException {
        response.sendRedirect("/?goLogin=/");
    }

    protected boolean selfVisit(Long id){
        return getCUser() != null && getCUser().getId().equals(id);
    }

    protected boolean requiredSelf(Long id) throws IOException {
        if (selfVisit(id)){
            return true;
        }
        redirIndex2Login();
        return false;
    }

    protected boolean requiredLogin() throws IOException {
        return requiredLogin(false);
    }

    protected boolean requiredLogin(Result result) throws IOException {
        boolean b = requiredLogin(false);
        if (!b) {
            result.setMsg("需要登录的操作");
        }
        return b;
    }

    protected boolean requiredLogin(boolean redir) throws IOException {
        if (getCUser() != null){
            return true;
        }
        if (redir) {
            redirIndex2Login();
        }
        return false;
    }

    protected ModelAndView ofView(String viewName) {
        ModelAndView modelAndView = new ModelAndView(viewName);
        giveCUser(modelAndView);
        return modelAndView;
    }
}
