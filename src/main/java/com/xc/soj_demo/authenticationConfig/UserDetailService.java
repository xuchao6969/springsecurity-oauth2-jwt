package com.xc.soj_demo.authenticationConfig;

import com.xc.soj_demo.constant.CodeConstant;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 用户信息获取（用户名称，密码，权限）
 * 
 */
@Component
public class UserDetailService implements UserDetailsService {
	
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //这个地方可以通过username从数据库获取正确的用户信息，包括密码和权限等。
		// 从user获取正确的用户信息，包括密码和权限等。
		Map<String, Object> user = CodeConstant.USER_MAP;
		if (user != null) {
			@SuppressWarnings("unchecked")
			List<String> authOrity = (List<String>) user.get("authOrity");
			String PASSWORD = "{noop}" + user.get("password").toString();
			List<GrantedAuthority> grantedAuthorityList = new ArrayList<>();
			for (String auth : authOrity) {
				grantedAuthorityList.add(new SimpleGrantedAuthority(auth));
			}
			return new User(username, PASSWORD, grantedAuthorityList);
		} else {
			throw new UsernameNotFoundException("用户[" + username + "]不存在");
		}

    }
    
    
}
