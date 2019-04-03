package org.yasukusury.onlinedocument.biz.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.yasukusury.onlinedocument.biz.entity.ESBook;
import org.yasukusury.onlinedocument.commons.base.BaseESRepository;

/**
 * @author 30254
 * creadtedate: 2019/2/20
 */
public interface ESBookRepository extends BaseESRepository<ESBook> {

//    @Query("{\"bool\" : {\"must\" : {\"bool\" : {\"should\" : [ {\"field\" : {\"name\" : \"?\"}}, {\"field\" : {\"name\" : \"?\"}} ]}}}}")
    Page<ESBook> findESBooksByNameContainsOrTag1OrTag2OrTag3OrDescriptionContains(String name, String tag1, String tag2, String tag3, String description, Pageable pageable);
}
