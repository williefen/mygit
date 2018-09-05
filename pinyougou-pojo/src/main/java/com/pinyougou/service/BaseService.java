package com.pinyougou.service;


import com.pinyougou.vo.PageResult;

import java.io.Serializable;
import java.util.List;

/**
 * @Author Willie Chen
 * @Description
 * @Date 2018/8/27
 * @Param $param
 **/
public interface BaseService<T> {


    //根据主键查询；业界中如果使用主键类型的时候更多使用Serializable而不用Object
     T findOne(Serializable id);
    //查询全部
     List<T> findAll();
    //根据条件查询
     List<T> findByWhere(T t);

    //根据分页查询
     PageResult findPage(Integer page, Integer pageSize);

    //根据条件分页查询
     PageResult findPage(Integer page, Integer pageSize, T t);

    // 新增
     void add(T t);
     // 修改
     void update(T t);

     // 批量删除
     void deleteByIds(Serializable[] ids);
}
