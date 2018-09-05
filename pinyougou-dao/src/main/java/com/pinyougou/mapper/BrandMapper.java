package com.pinyougou.mapper;

import com.pinyougou.pojo.TbBrand;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @Author Willie Chen
 * @Description
 * @Date 2018/8/27
 * @Param $param
 **/
public interface BrandMapper extends Mapper<TbBrand> {
    List<TbBrand> queryAll();

    /**
     * 查询数据库中的所有品牌；并返回一个集合，集合中的数据结构如下：
     *
     * @return [{id:'1',text:'联想'},{id:'2',text:'华为'}]
     */
    List<Map<String, Object>> selectOptionList();
}
