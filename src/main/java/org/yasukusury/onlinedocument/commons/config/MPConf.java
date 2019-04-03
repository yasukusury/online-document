package org.yasukusury.onlinedocument.commons.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 30254
 * creadtedate: 2018/11/26
 */
@Configuration
@MapperScan("org.yasukusury.onlinedocument.biz.mapper*")
public class MPConf {
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        // mybatis分页拦截器
        return new PaginationInterceptor();
    }
}
