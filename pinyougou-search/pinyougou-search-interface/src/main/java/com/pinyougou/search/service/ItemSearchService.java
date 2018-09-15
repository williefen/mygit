package com.pinyougou.search.service;

import com.pinyougou.pojo.TbItem;

import java.util.List;
import java.util.Map;

/**
 * @Author Willie Chen
 * @Description
 * @Date 2018/9/10
 * @Param $param
 **/
public interface ItemSearchService {

     Map<String, Object> search(Map<String, Object> searchMap);

     // 更新商品列表
     void importItemList(List<TbItem> itemList);

    // 根据商品spu id集合删除solr中商品数据
    void deleteItemByGoodsIds(List<Long> goodsIds);

}
