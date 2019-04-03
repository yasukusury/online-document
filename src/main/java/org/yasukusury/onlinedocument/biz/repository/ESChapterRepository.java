package org.yasukusury.onlinedocument.biz.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.yasukusury.onlinedocument.biz.entity.Chapter;
import org.yasukusury.onlinedocument.biz.entity.ESChapter;
import org.yasukusury.onlinedocument.commons.base.BaseESRepository;

/**
 * @author 30254
 * creadtedate: 2019/2/21
 */
public interface ESChapterRepository extends BaseESRepository<ESChapter> {

    Page<ESChapter> findChaptersByContentContains(String keyword, Pageable pageable);
}
