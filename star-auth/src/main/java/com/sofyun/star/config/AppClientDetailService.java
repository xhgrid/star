package com.sofyun.star.config;

import com.sofyun.common.dto.auth.AuthUser;
import com.sofyun.star.client.UserClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
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
        Date date = null;
        if (null != user.getLastLoginTime()){
            ZoneId zone = ZoneId.systemDefault();
            Instant instant = user.getLastLoginTime().atZone(zone).toInstant();
            date = Date.from(instant);
        }
        return new AppDetails(user.getId(), user.getCode(), user.getPwd(), mapToGrantedAuthorities(user.getRoles()), date);
    }

    private static List<GrantedAuthority> mapToGrantedAuthorities(List<String> authorities) {
        return authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

}
