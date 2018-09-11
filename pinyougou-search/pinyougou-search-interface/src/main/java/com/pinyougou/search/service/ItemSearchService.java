package com.pinyougou.search.service;

import java.util.Map;

/**
 * @Author Willie Chen
 * @Description
 * @Date 2018/9/10
 * @Param $param
 **/
public interface ItemSearchService {

    Map<String, Object> search(Map<String, Object> searchMap);
}
