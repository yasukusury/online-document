package org.yasukusury.onlinedocument.commons.config;

import org.beetl.core.TagFactory;
import org.beetl.core.resource.ClasspathResourceLoader;
import org.beetl.ext.spring.BeetlGroupUtilConfiguration;
import org.beetl.ext.spring.BeetlSpringViewResolver;
import org.beetl.ext.spring.SpringBeanTagFactory;
import org.beetl.json.ext.BeetlJsonHttpMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author 30254
 * creadtedate:2018/8/21
 */
@Configuration
public class BeetlConf {

    @Value("${beetl.configResources}") String configResources;
    @Value("${beetl.templatesPath}") String templatePath;
    @Bean
    public BeetlGroupUtilConfiguration beetlConfig(@Qualifier("authTagFactory") SpringBeanTagFactory auth) {
        // Beetl模板配置类
        BeetlGroupUtilConfiguration config = new BeetlGroupUtilConfiguration();
        config.init();
        // 获取Spring Boot 的ClassLoader
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if(loader==null){
            loader = BeetlConf.class.getClassLoader();
        }
        config.setConfigFileResource(new ClassPathResource(configResources));
        ClasspathResourceLoader cploder = new ClasspathResourceLoader(loader,
                templatePath);
        config.setResourceLoader(cploder);
        HashMap<String, TagFactory> map = new HashMap<>();
        map.put("auth", auth);
        config.setTagFactorys(map);

        config.init();
        // 如果使用了优化编译器，涉及到字节码操作，需要添加ClassLoader
        config.getGroupTemplate().setClassLoader(loader);
        return config;

    }

    @Bean
    public BeetlSpringViewResolver beetlSpringViewResolver(@Qualifier("beetlConfig") BeetlGroupUtilConfiguration beetl) {
        // Beetl视图解析器
        BeetlSpringViewResolver resolver = new BeetlSpringViewResolver();
        resolver.setViewNames("/**");
        resolver.setSuffix(".html");
        resolver.setContentType("text/html;charset=UTF-8");
        resolver.setOrder(0);
        resolver.setConfig(beetl);
        return resolver;
    }

    @Bean
    public SpringBeanTagFactory authTagFactory(){
        SpringBeanTagFactory factory = new SpringBeanTagFactory();
        factory.setName("authTag");
        return factory;
    }


    @Bean
    public BeetlJsonHttpMessageConverter beetlJsonHttpMessageConverter(){
        BeetlJsonHttpMessageConverter converter = new BeetlJsonHttpMessageConverter();
        ArrayList<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
        mediaTypes.add(MediaType.TEXT_HTML);
        converter.setSupportedMediaTypes(mediaTypes);
        HashMap<String, String> map = new HashMap<>(16);
        map.put("~d", "f/yyyy-MM-dd HH:mm:ss/");
        converter.setPolicys(map);
        return converter;
    }
}
