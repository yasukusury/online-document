package org.yasukusury.onlinedocument.biz.service;

import org.springframework.stereotype.Service;
import org.yasukusury.onlinedocument.biz.entity.User;
import org.yasukusury.onlinedocument.biz.mapper.UserMapper;
import org.yasukusury.onlinedocument.commons.base.BaseService;
import org.yasukusury.onlinedocument.commons.utils.Md5Utils;

/**
 * @author 30254
 * creadtedate: 2018/12/1
 */
@Service
public class UserService extends BaseService<UserMapper, User> {

    @Override
    public boolean save(User entity) throws RuntimeException {
        if (entity.getId() == null){
            entity.setPassword(new Md5Utils().getkeyBeanofStr(entity.getPassword()).toUpperCase());
        }
        return super.save(entity);
    }

    public Boolean isUsernameExist(String name){
        Integer integer = baseMapper.countUsername(name);
        return integer == null || integer > 0;
    }

    public void updateUserPortrait(String url, Long uid){
        baseMapper.updatePortraitById(url, uid);
    }

}
