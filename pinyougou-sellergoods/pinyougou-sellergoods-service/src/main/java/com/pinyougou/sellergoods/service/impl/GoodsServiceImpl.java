package com.pinyougou.sellergoods.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.service.impl.BaseServiceImpl;
import com.pinyougou.vo.Goods;
import com.pinyougou.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

@Service(interfaceClass = GoodsService.class)
public class GoodsServiceImpl extends BaseServiceImpl<TbGoods> implements GoodsService {

    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private GoodsDescMapper goodsDescMapper;
    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private  BrandMapper brandMapper;
    @Autowired
    private  ItemCatMapper itemCatMapper;
    @Autowired
    private SellerMapper sellerMapper;

    @Override
    public PageResult search(Integer page, Integer rows, TbGoods goods) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbGoods.class);
        Example.Criteria criteria = example.createCriteria();
        if(!StringUtils.isEmpty(goods.getSellerId())){
            criteria.andLike("sellerId",  goods.getSellerId());
        }
        if(!StringUtils.isEmpty(goods.getAuditStatus())){
            criteria.andLike("auditStatus",  goods.getAuditStatus());
        }
        if(!StringUtils.isEmpty(goods.getGoodsName())){
            criteria.andLike("goodsName", "%" + goods.getGoodsName() + "%");
        }

        List<TbGoods> list = goodsMapper.selectByExample(example);
        PageInfo<TbGoods> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public void addGoods(Goods goods) {
        // 保存基本信息
        add(goods.getGoods());
          //保存描述信息
        goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());
        goodsDescMapper.insertSelective(goods.getGoodsDesc());
          //保存sku列表
        saveItemList(goods);
    }

    @Override
    public Goods findGoodsById(Long id) {
        Goods goods = new Goods();
        TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
        goods.setGoods(tbGoods);

        TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(id);
         goods.setGoodsDesc(tbGoodsDesc);

        Example example = new Example(TbItem.class);
        example.createCriteria().andEqualTo("goodsId",id);
        List<TbItem> itemList=itemMapper.selectByExample(example);
        goods.setItemList(itemList);

        return goods;
    }

    /**
     * 保存sku动态数据
     * @param goods
     */
    private void saveItemList(Goods goods){
            if("1".equals(goods.getGoods().getIsEnableSpec())){
                if (goods.getItemList() != null && goods.getItemList().size() > 0) {
                    for (TbItem item : goods.getItemList()) {
                        //商品的标题应该为：spu商品名称+所有规格选项值
                        String title = goods.getGoods().getGoodsName();
                        //将sku对于的规格及选项数据转换为一个map;获取对应规格的选项
                        Map<String, Object> map = JSON.parseObject(item.getSpec());
                        Set<Entry<String, Object>> entries = map.entrySet();
                        for (Entry entry : entries) {
                            title += " " + entry.getValue();
                        }
                        item.setTitle(title);

                        setItemValue(item, goods);
                        //保存tbItem
                        itemMapper.insertSelective(item);
                    }
                }
            } else {
                //不启用规格
                TbItem item = new TbItem();

                item.setPrice(goods.getGoods().getPrice());
                item.setNum(9999);
                item.setIsDefault("1");//表示默认
                item.setStatus("0");//未审核
                item.setTitle(goods.getGoods().getGoodsName());

                setItemValue(item, goods);
                itemMapper.insertSelective(item);
            }

}

    private void setItemValue(TbItem item, Goods goods) {
        //查询品牌
        TbBrand brand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
        item.setBrand(brand.getName());

        //商品分类第3级的中文名称
        TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id());
        item.setCategory(itemCat.getName());
        //商品分类id
        item.setCategoryid(itemCat.getId());

        item.setCreateTime(new Date());
        item.setGoodsId(goods.getGoods().getId());

        //获取spu的第一张图片
        if (!StringUtils.isEmpty(goods.getGoodsDesc().getItemImages())) {
            //将图片json格式字符串转换为一个Json对象
            List<Map> images = JSONArray.parseArray(goods.getGoodsDesc().getItemImages(), Map.class);

            item.setImage(images.get(0).toString());
        }

        //设置商家数据
        TbSeller seller = sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());

        item.setSeller(seller.getName());
        item.setSellerId(seller.getSellerId());

        item.setUpdateTime(item.getCreateTime());
    }
}
