package org.yasukusury.onlinedocument.commons.constant;

/**
 * @描述: web全局常用key
 * @作者: Pengo.Wen
 * @日期: 2017-03-31 16:23
 * @版本: v1.0
 */
public interface ConstantWebContext {

    String RESPONE_SUCCESS = "0";

    String RESPONE_ERROR = "error";

    String RESPONE_UNABLE = "unable";

    String RESPONE_CLASS_NOT = "classNot";

    String RESPONE_GRADE_NOT = "gradeNot";

    String RESPONE_CAN_NOT_DO = "canNotDo";
    /**
     * web 路径
     */
    String REQUEST_CXT = "cxt";

    /**
     * request 请求id
     */
    String REQUEST_ID = "REQUEST_ID";

    /**
     * request cookie
     */
    String REQUEST_COOKIE_MAP = "cookieMap";

    /**
     * request param
     */
    String REQUEST_PARAM_MAP = "paramMap";

    /**
     * 国际化，设置request中的当前语言
     */
    String REQUEST_LOCALE_PRAM = "localePram";

    /**
     * 国际化，设置cookie中当前语言的key
     */
    String COOKIE_LANGUAGE = "language";

    /**
     * 国际化，设置request中的当前语言map
     */
    String REQUEST_I18N_MAP = "i18nMap";

    /**
     * 国际化，设置request中的当前语言对应列后缀
     */
    String REQUEST_I18N_COLUMN_SUFFIX = "i18nColumnSuffix";

    /**
     * 直接渲染模板的路径
     */
    String REQUEST_TO_URL = "toUrl";

    /**
     * 当前登录用户的id
     */
    String REQUEST_C_USER_ID = "cUserId";

    /**
     * 当前登录用户
     */
    String REQUEST_C_USER = "cUser";

    /**
     * 验证码key
     */
    String REQUEST_AUTH_CODE = "authCode";

    /**
     * 找回密码验证码key
     */
    String REQUEST_RETRIEVEPWD_CODE = "retrievePWDCode";

    /**
     * 权限标示验证码
     */
    String COOKIE_AUTHMARK = "authmark";

    /**
     * 排序方式
     */
    String REQUEST_ORDER_MODE = "orderMode";

    /**
     * 第几页
     */
    String REQUEST_PAGE_NUMBER = "pageNumber";

    /**
     * 第几页 layui格式
     */
    String REQUEST_PAGE = "page";

    /**
     * 每页显示几多
     */
    String REQUEST_PAGE_SIZE = "pageSize";

    /**
     * 每页显示几多 layui
     */
    String REQUEST_LIMIT = "limit";

    /**
     * 排序条件
     */
    String REQUEST_ORDER_COLUNM = "orderColunm";

    /**
     * 分页查询条件key前缀
     */
    String REQUEST_QUERY = "_query";

    /**
     * 表单重复提交验证key
     */
    String REQUEST_FORM_TOKEN = "formToken";

    /**
     * 表单重复提交验证token
     */
    String COOKIE_TOKEN = "token";

    /**
     * 当前范文日志request key
     */
    String REQ_SYS_LOG_KEY = "reqSysLog";

    /**
     * render耗时计算key
     */
    String RENDER_TIME_KEY = "renderTime";

    String SEARCH_PAGE_KEY = "search_page";

    String SEARCH_SIZE_KEY = "search_size";

}
