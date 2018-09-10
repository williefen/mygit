package com.pinyougou.solr;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.ItemMapper;
import com.pinyougou.pojo.TbItem;
import org.junit.Test;
import org.junit.internal.runners.model.EachTestNotifier;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

/**
 * @Author Willie Chen
 * @Description
 * @Date 2018/9/10
 * @Param $param
 **/

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations= "classpath*:spring/applicationContext*.xml")
public class ItemImport2SolrTest {

    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private ItemMapper itemMapper;

    @Test
    public  void importItem(){
    //1、查询所有审核过的商品数据
      TbItem param = new TbItem();
      param.setStatus("1");
      List<TbItem> itemList = itemMapper.select(param);
    //2.遍历每个商品并转换商品规格
      for ( TbItem item: itemList) {

           Map map = JSON.parseObject(item.getSpec(), Map.class);
           item.setSpecMap(map);

           // 保存到solr中
          solrTemplate.saveBean(item);
      }
         solrTemplate.commit();
  }
}
