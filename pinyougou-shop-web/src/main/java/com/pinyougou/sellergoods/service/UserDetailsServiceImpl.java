package com.pinyougou.sellergoods.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSeller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
        @Reference
    private SellerService sellerService;

//    public void setSellerService(SellerService sellerService) {
//        this.sellerService = sellerService;
//    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //设置角色
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_SELLER"));

        //得到商家对象

        TbSeller seller = sellerService.findOne(username);
        if (seller != null) {
            if (seller.getStatus().equals("1")) {

                return new User(username, seller.getPassword(), grantedAuthorities);

            } else {
                return null;

            }
        }

        return null;
    }
}
