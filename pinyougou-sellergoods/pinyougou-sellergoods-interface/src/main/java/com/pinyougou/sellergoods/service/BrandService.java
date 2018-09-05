package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbBrand;
import com.pinyougou.service.BaseService;
import com.pinyougou.vo.PageResult;

import javax.swing.*;
import java.util.List;
import java.util.Map;

/**
 * @Author Willie Chen
 * @Description
 * @Date 2018/8/27
 * @Param $param
 **/
public interface BrandService extends BaseService<TbBrand> {
    // 查询品牌
    List<TbBrand> queryAll();

    /**
     * 根据分页信息分页查询品牌数据
     * @return
     */
    @Deprecated
    List<TbBrand> testPage(Integer page,Integer rows);


    PageResult search(TbBrand brand,Integer page,Integer rows);

    List<Map<String, Object>> selectOptionList();
}
