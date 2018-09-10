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
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.Map.Entry;

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

        // 不查询删除商品
        criteria.andNotEqualTo("isDelete","1");
        // 商家限定
        if(!StringUtils.isEmpty(goods.getSellerId())){
            criteria.andEqualTo("sellerId",  goods.getSellerId());
        }
        if(!StringUtils.isEmpty(goods.getAuditStatus())){
            criteria.andEqualTo("auditStatus",  goods.getAuditStatus());
        }
        if(!StringUtils.isEmpty(goods.getGoodsName())){
            criteria.andLike("goodsName", "%" + goods.getGoodsName() + "%");
        }

        List<TbGoods> list = goodsMapper.selectByExample(example);
        PageInfo<TbGoods> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    @Transactional
    @Override
    public void addGoods(Goods goods) {
        // 保存基本信息
        add(goods.getGoods());

       //  int i=11/0;
          //保存描述信息
        goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());
        goodsDescMapper.insertSelective(goods.getGoodsDesc());
          //保存sku列表
        saveItemList(goods);
    }

    @Override
    public Goods findGoodsById(Long id) {
        Goods goods = new Goods();
        //1、根据商品id查询商品基本信息
        TbGoods tbGoods = findOne(id);
        goods.setGoods(tbGoods);
         // 2、根据商品id查询商品描述信息
        TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(id);
        goods.setGoodsDesc(tbGoodsDesc);
           // 3、根据商品id查询商品sku列表
        TbItem param = new TbItem();
        param.setGoodsId(id);

       // 根据条件查询；查询条件（也就是该方法对应处理的对象）
        List<TbItem> itemList = itemMapper.select(param);
        goods.setItemList(itemList);

        return goods;
    }

    @Transactional
    @Override
    public void updateGoods(Goods goods) {
        //更新商品信息
        //修改过则重新设置未审核
        goods.getGoods().setAuditStatus("0");
        goodsMapper.updateByPrimaryKeySelective(goods.getGoods());
        //更新商品描述信息
        goodsDescMapper.updateByPrimaryKeySelective(goods.getGoodsDesc());
        //删除原有的SKU列表，防止数据冗沉
        TbItem param = new TbItem();
        param.setGoodsId(goods.getGoods().getId());
        itemMapper.delete(param);
        //保存sku列表
        saveItemList(goods);
    }
    @Transactional
    @Override
    public void updateStatus(Long[] ids, String status) {
        TbGoods goods = new TbGoods();
        goods.setAuditStatus(status);

        Example example = new Example(TbGoods.class);
        Example.Criteria criteria = example.createCriteria();

        // 设置查询条件
        criteria.andIn("id", Arrays.asList(ids));
        goodsMapper.updateByExampleSelective(goods,example);

        if ("2".equals(status)){
            TbItem item = new TbItem();
            item.setStatus("1");
            Example itemExample = new Example(TbItem.class);
            Example.Criteria criteria1 = itemExample.createCriteria();
            // update tb_item set status='1' where goods_id in(1,3,34)
            criteria1.andIn("goodsId", Arrays.asList(ids));

            //根据条件选择性更新；要更新的数值；更新条件
            itemMapper.updateByExampleSelective(item, itemExample);
        }
    }

    @Override
    public void deleteGoodsByIds(Long[] ids) {
         TbGoods goods = new TbGoods();
         goods.setIsDelete("1");

          Example example = new Example(TbGoods.class);
          example.createCriteria().andIn("id",Arrays.asList(ids));

        //批量更新商品的删除状态为删除
         goodsMapper.updateByExampleSelective(goods,example);
    }

    /**
     * 上下架
     * @param ids
     * @param status
     * @throws Exception
     */
    @Override
    public void updownStatus(Long[] ids, String status) throws Exception {

        TbGoods goods = new TbGoods();
        for (Long id : ids) {
            TbGoods oneGoods = findOne(id);
            if ("1".equals(status)) {
                if ("2".equals(oneGoods.getAuditStatus())) {
                    goods.setIsMarketable("1");
                } else {
                    throw new Exception("有未通过审核商品，请确认");
                }
            } else {
                if ("2".equals(oneGoods.getAuditStatus()) && "1".equals(oneGoods.getIsMarketable())) {
                    goods.setIsMarketable("0");
                } else {
                    throw new Exception("有未上架商品，请确认");
                }
            }
        }
        Example example = new Example(TbGoods.class);
        example.createCriteria().andIn("id", Arrays.asList(ids));
        //批量更新商品的上下架状态
        goodsMapper.updateByExampleSelective(goods, example);
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
