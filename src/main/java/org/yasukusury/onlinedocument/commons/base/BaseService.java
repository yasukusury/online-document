package org.yasukusury.onlinedocument.commons.base;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.ibatis.session.SqlSession;
import org.yasukusury.onlinedocument.commons.utils.DateTool;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 30254
 */
@CommonsLog
public abstract class BaseService<M extends BaseMapper<T>, T extends BaseModel> extends ServiceImpl<M, T> {

    public T getByExample(T t){
        t.setValid(true);
        QueryWrapper<T> wrapper = new QueryWrapper<>(t);
        return baseMapper.selectOne(wrapper);
    }

    public List<T> listByExample(T t){
        t.setValid(true);
        QueryWrapper<T> wrapper = new QueryWrapper<>(t);
        return baseMapper.selectList(wrapper);
    }

    protected void initEntity(T entity){
        if(entity.getId() != null && entity.getId() > 0){
            entity.setUpdateTime(DateTool.getTime());
        }else{
            entity.setCreateTime(DateTool.getTime());
            entity.setUpdateTime(DateTool.getTime());
            entity.setValid(true);
        }
    }

    @Override
    public boolean save(T entity) throws RuntimeException{
        initEntity(entity);
        return super.save(entity);
    }

    @Override
    public boolean saveBatch(Collection<T> entityList) throws RuntimeException {
        entityList.forEach(this::initEntity);
        return super.saveBatch(entityList);
    }

    @Override
    public boolean saveBatch(Collection<T> entityList, int batchSize) throws RuntimeException {
        if (CollectionUtils.isEmpty(entityList)) {
            return false;
        }
        entityList.forEach(this::initEntity);
        return super.saveBatch(entityList, batchSize);
    }

    @Override
    public boolean saveOrUpdate(T entity)  throws RuntimeException {
        initEntity(entity);
        return super.saveOrUpdate(entity);
    }

    @Override
    public boolean saveOrUpdateBatch(Collection<T> entityList)  throws RuntimeException {
        entityList.forEach(this::initEntity);
        return super.saveOrUpdateBatch(entityList);
    }

    @Override
    public boolean saveOrUpdateBatch(Collection<T> entityList, int batchSize)  throws RuntimeException {
        entityList.forEach(this::initEntity);
        return super.saveOrUpdateBatch(entityList, batchSize);
    }

    public boolean remove(T entity) throws RuntimeException{
        if(null != entity){
            prepareDelete(entity);
            return updateById(entity);
        }else {
            return false;
        }
    }

    public void prepareDelete(Serializable id){
        T entity = getById(id);
        entity.setValid(false);
        entity.setDeleteTime(new Timestamp(System.currentTimeMillis()));
    }

    public void prepareDelete(T entity){
        entity.setValid(false);
        entity.setDeleteTime(new Timestamp(System.currentTimeMillis()));
    }

    @Override
    public boolean removeById(Serializable id) throws RuntimeException {
        T entity = getById(id);
        entity.setDeleteTime(new Timestamp(System.currentTimeMillis()));
        return remove(entity);
    }

    public boolean removeBatchById(Collection<T> entityList) throws RuntimeException {
        entityList.forEach(this::prepareDelete);
        return updateBatchById(entityList);
    }


    @Override
    public T getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    public T getOne(Wrapper<T> queryWrapper) {
        return super.getOne(queryWrapper, false);
    }

    @Override
    public Collection<T> listByIds(Collection<? extends Serializable> idList) {
        return super.listByIds(idList).stream().filter(BaseModel::getValid).collect(Collectors.toList());
    }

    @Override
    public List<T> list(Wrapper<T> queryWrapper) {
        return super.list(queryWrapper);
    }

    @Override
    public int count(Wrapper<T> wrapper) {
        return super.count(wrapper);
    }

    @Override
    public boolean updateById(T entity) throws RuntimeException {
        entity.setUpdateTime(DateTool.getTime());
        //因为乐观锁导致更新不成功，则抛出异常
        boolean result = super.updateById(entity);
        if(!result){
            throw new RuntimeException("更新失败，该实体对象非最新状态，请重新加载！");
        }
        return true;
    }

}
