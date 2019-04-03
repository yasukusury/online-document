package org.yasukusury.onlinedocument.biz.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yasukusury.onlinedocument.biz.dto.CommentDto;
import org.yasukusury.onlinedocument.biz.entity.Comment;
import org.yasukusury.onlinedocument.biz.mapper.CommentMapper;
import org.yasukusury.onlinedocument.commons.base.BaseService;

import java.util.*;

/**
 * @author 30254
 * creadtedate: 2019/3/4
 */
@Service
public class CommentService extends BaseService<CommentMapper, Comment> {

    @Autowired
    private UserService userService;

    public List<CommentDto> listDtoByChapter(Long chapter){
        return baseMapper.listDtoByChapter(chapter);
    }


}
