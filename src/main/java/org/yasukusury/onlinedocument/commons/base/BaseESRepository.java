package org.yasukusury.onlinedocument.commons.base;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * @author 30254
 * creadtedate: 2019/2/20
 */
@NoRepositoryBean
public interface BaseESRepository<T> extends ElasticsearchRepository<T, String> {

}
