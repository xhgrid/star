package com.sofyun.star.config;

import com.sofyun.star.client.UserClient;
import com.sofyun.user.auth.AuthUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

public class AppClientDetailService implements UserDetailsService {

    @Autowired
    private UserClient userClient;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AuthUser user = userClient.findByCode(username).getData();
        if (null == user){
            throw new UsernameNotFoundException(String.format("No user found with username '%s'.", username));
        }
        return User.withUsername(user.getCode()).password(user.getPwd()).authorities(mapToGrantedAuthorities(user.getRoles())).build();
    }

    private static List<GrantedAuthority> mapToGrantedAuthorities(List<String> authorities) {
        return authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

}
