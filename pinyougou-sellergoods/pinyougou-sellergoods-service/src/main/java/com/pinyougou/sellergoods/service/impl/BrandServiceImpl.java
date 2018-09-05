package com.pinyougou.sellergoods.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.BrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import com.pinyougou.service.impl.BaseServiceImpl;
import com.pinyougou.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;


import java.util.List;
import java.util.Map;

/**
 * @Author Willie Chen
 * @Description
 * @Date 2018/8/27
 * @Param $param
 **/

//Servive注解来自阿里的注解
@Service(interfaceClass= BrandService.class)
public class BrandServiceImpl extends BaseServiceImpl<TbBrand> implements BrandService{

    @Autowired
    private BrandMapper brandMapper;

    @Override
    public List<TbBrand> queryAll(){
        return  brandMapper.queryAll();
    }

    @Override
    public List<TbBrand> testPage(Integer page, Integer rows) {
        PageHelper.startPage(page,rows);

        return brandMapper.selectAll();
    }

    @Override
    public PageResult search(TbBrand brand, Integer page, Integer rows) {
        // 设置分页
        PageHelper.startPage(page, rows);
        // 设置查询条件
        Example example = new Example(TbBrand.class);
        Example.Criteria criteria = example.createCriteria();
        //根据首字母查询
        if (!StringUtils.isEmpty(brand.getFirstChar())) {
            criteria.andEqualTo("firstChar", brand.getFirstChar());
        }
        if (!StringUtils.isEmpty(brand.getName())) {
            criteria.andLike("name", "%" + brand.getName() + "%");
        }
        List<TbBrand> list = brandMapper.selectByExample(example);
        PageInfo<TbBrand> pageInfo = new PageInfo<>(list);
        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public List<Map<String, Object>> selectOptionList() {
        return brandMapper.selectOptionList();
    }
}
