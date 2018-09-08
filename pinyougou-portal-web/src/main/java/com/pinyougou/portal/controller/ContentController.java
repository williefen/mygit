package com.pinyougou.portal.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.content.service.ContentService;
import com.pinyougou.pojo.TbContent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author Willie Chen
 * @Description
 * @Date 2018/9/7
 * @Param $param
 **/
@RequestMapping("/content")
@RestController
public class ContentController {
   @Reference(timeout = 5000)
   private ContentService contentService;
    /**
     * 根据内容分类id查询该分类下的所有有效内容并且按照排序字段降序排序
     * @param categoryId 内容分类id
     * @return 内容列表
     */

    @GetMapping("/findContentListByCategoryId")
   public List<TbContent> findContentListByCategoryId(Long categoryId){

     return contentService.findContentListByCategoryId(categoryId);
   }
}
