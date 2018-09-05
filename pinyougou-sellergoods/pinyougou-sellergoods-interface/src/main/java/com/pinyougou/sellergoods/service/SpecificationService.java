package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.service.BaseService;
import com.pinyougou.vo.PageResult;
import com.pinyougou.vo.Specification;

import java.util.List;
import java.util.Map;

public interface SpecificationService extends BaseService<TbSpecification> {

    PageResult search(Integer page, Integer rows, TbSpecification specification);

    /**
     * 保存规格及其选项到数据库中
     * @param specification
     */

    void add(Specification specification);

    /**
     *根据规格id 查询规格及选项列表
     *
     */
    Specification findOne(Long id);
    /**
     * 更新规格及其选项到数据库中
     * @param specification 规格及规格选项列表
     */
    void update(Specification specification);

    /**
     * 根据规格id集合删除对应的规格和选项
     * @param ids 规格id集合
     */
    void deleteSpecificationByIds(Long[] ids);

    /**
     * 查询格式化的规格数据
     * @return [{id:111,text:"内存大小"},...]
     */
    List<Map<String, Object>> selectOptionList();
}