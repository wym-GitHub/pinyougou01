package cn.itcast.demo.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;


import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @RequestMapping("loginname")
    public String loginName(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println(name);
        return name;
    }
}
