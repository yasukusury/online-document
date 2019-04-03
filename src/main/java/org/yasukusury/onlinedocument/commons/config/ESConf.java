package org.yasukusury.onlinedocument.commons.config;

import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 * @author 30254
 * creadtedate: 2019/2/20
 */
@EnableElasticsearchRepositories(basePackages = "org.yasukusury.onlinedocument.biz.repository")
public class ESConf {

}
