package com.pinyougou.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("itemsearch")
public class ItemSearchController {
    @Reference
    private ItemSearchService itemSearchService;
    @RequestMapping("search")
    public Map<String,Object>search(@RequestBody Map searchMap){
        System.out.println("访问进入");
        return itemSearchService.search(searchMap);
    }
}
