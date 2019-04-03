package org.yasukusury.onlinedocument.commons.config;

import org.beetl.json.ext.BeetlJsonHttpMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author 30254
 * creadtedate:2018/8/17
 */
@Configuration
@EnableScheduling
public class WebConf implements WebMvcConfigurer {

    public static final String UPLOAD_PATH = new File("upload/").getAbsolutePath();

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 静态资源访问
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
        // 上传资源访问
        registry.addResourceHandler("/upload/**").addResourceLocations("file:"+ UPLOAD_PATH + File.separator);
    }


    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        // 默认视图解析器
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setSuffix(".html");
        resolver.setViewNames("/**");
        resolver.setContentType("text/html;charset=UTF-8");
        resolver.setOrder(1);
        registry.viewResolver(resolver);
    }

}
