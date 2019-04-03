package org.yasukusury.onlinedocument.biz.service;

import org.springframework.stereotype.Service;
import org.yasukusury.onlinedocument.biz.entity.UserAct;
import org.yasukusury.onlinedocument.biz.mapper.UserActMapper;
import org.yasukusury.onlinedocument.commons.base.BaseService;
import org.yasukusury.onlinedocument.commons.utils.StringTool;

import java.util.List;

/**
 * @author 30254
 * creadtedate: 2019/3/6
 */
@Service
public class UserActService extends BaseService<UserActMapper, UserAct> {

    public void userLove(Long user, Long book, boolean dir){
        UserAct act = getById(user);

        List<Long> books = StringTool.strings2LongList(act.getLove());
        if (dir){
            books.add(book);
            books.sort(Long::compareTo);
        } else {
            books.remove(book);
        }
        act.setLove(StringTool.longList2String(books));
        updateById(act);
    }

    public void userCommentPraise(Long user, Long comment, boolean dir){
        UserAct act = getById(user);

        List<Long> comments = StringTool.strings2LongList(act.getCommentPraise());
        if (dir){
            comments.add(comment);
            comments.sort(Long::compareTo);
        } else {
            comments.remove(comment);
        }
        act.setCommentPraise(StringTool.longList2String(comments));
        updateById(act);
    }

    public void userBookPraise(Long user, Long book, boolean dir){
        UserAct act = getById(user);

        List<Long> books = StringTool.strings2LongList(act.getBookPraise());
        if (dir){
            books.add(book);
            books.sort(Long::compareTo);
        } else {
            books.remove(book);
        }
        act.setBookPraise(StringTool.longList2String(books));
        updateById(act);
    }
}
