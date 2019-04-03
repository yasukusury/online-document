package org.yasukusury.onlinedocument.biz.service;

import org.springframework.stereotype.Service;
import org.yasukusury.onlinedocument.biz.entity.Image;
import org.yasukusury.onlinedocument.biz.mapper.ImageMapper;
import org.yasukusury.onlinedocument.commons.base.BaseService;

/**
 * @author 30254
 * creadtedate: 2019/1/4
 */
@Service
public class ImageService extends BaseService<ImageMapper, Image> {

    public static final String CHAPTER = "chapter";
    public static final String BOOK = "book";
    public static final String PORTRAIT = "portrait";

}
