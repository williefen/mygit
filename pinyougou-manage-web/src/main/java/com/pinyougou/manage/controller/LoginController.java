package com.pinyougou.manage.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.HashMap;
import java.util.Map;

/**
 * @Author Willie Chen
 * @Description
 * @Date 2018/9/1
 * @Param $param
 **/

@RequestMapping("/login")
@RestController
public class LoginController {

    @GetMapping("/getUsername")
    public Map<String,Object> getUsername(){

      Map<String,Object> resultMap=new HashMap<String,Object>();
      String username= SecurityContextHolder.getContext().getAuthentication().getName();
      resultMap.put("username",username);

      return resultMap;
    }
}
