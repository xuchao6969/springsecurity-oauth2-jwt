package com.xc.soj_demo.service.impl;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.xc.soj_demo.authenticationConfig.TokenConfig;
import com.xc.soj_demo.constant.CodeConstant;
import com.xc.soj_demo.dao.UserDao;
import com.xc.soj_demo.entity.User;
import com.xc.soj_demo.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("userService")
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Value("${server.port}")
    private String port;

    @Autowired
    private UserDao dao;

    @Autowired
    HttpServletRequest request;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    private TokenConfig tokenConfig;

    @Override
    public Map<String, Object> login(String username, String password) {
        Map<String, Object> map = new HashMap<>();
        User userObj = dao.getUserByUserName(username);
        // 判断用户是否存在
        if (null == userObj) {
            map.put("code", 1);// 用户不存在
            return map;
        }
        // 判断密码是否正确
        if (!password.equals(userObj.getPassword())) {
            map.put("code", -1);// 密码错误
            return map;
        }
        map.put("code", 0);// 用户存在 密码正确
        map.put("userId", userObj.getUserId());
        map.put("username", userObj.getUsername());
        String userId = userObj.getUserId();

//        List<String> listAuthority = dao.getUserPermissionByUserid(userId);
        //模拟 查询用户权限（查询结果 可以对用户进行增删改查）
        List<String> listPermission = new ArrayList<>();
        listPermission.add("user::add");
        listPermission.add("user::list");
        listPermission.add("user::update");
        listPermission.add("user::delete");

//        List<String> listRole = dao.getUserRolesByUid(userId);
        //模拟 查询用户角色 (查询结果 userObj具有系统管理员 普通管理员的角色)
        List<String> listRole = new ArrayList<>();
//        listRole.add("sys_admin");
//        listRole.add("admin");
        listRole.add(userObj.getUserRole());

        map.put("authOrity", listRole);
        map.put("userPermission", listPermission);
        map.put("password", password);
        try {
            map.put("access_token", getOAuthToken(map));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 调用OAuth2的获取令牌接口
     *
     * @description 1.将用户信息存入公共map中 2.获取访问令牌 3.写入"刷新令牌"到数据库
     *
     */
    private String getOAuthToken(Map<String, Object> map) throws JsonProcessingException {
        CodeConstant.USER_MAP = map;

        Map<String, String> tokenMap = tokenConfig.getConfig();
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<String, String>();
        formData.add("username", map.get("username").toString());
        formData.add("password", map.get("password").toString());
        formData.add("client_id", tokenMap.get("clientId"));
        formData.add("client_secret", tokenMap.get("secret"));
        formData.add("grant_type", tokenMap.get("grantTypes"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String urlStr = "http://localhost:" + port + "/oauth/token";
        Map<?, ?> resultMap = restTemplate.exchange(urlStr, HttpMethod.POST,
                new HttpEntity<MultiValueMap<String, String>>(formData, headers), Map.class).getBody();

        if (null != resultMap) {
            try {
                setRefreshToken(map.get("userId").toString(), resultMap.get("refresh_token").toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultMap.get("access_token").toString();
        }
        return null;
    }

    /**
     * 更新用户的刷新令牌
     *
     */
    public void setRefreshToken(String userId, String refreshToken) throws Exception {
        dao.setRefreshToken(userId, refreshToken);
    }

    public String getPasswordByUserName(String username) throws Exception {
        String password = dao.getPasswordByUserName(username);
        return password;
    }



    public Boolean verdictRefreshTokenByUId(Integer userId, String refreshToken) {
        if (userId == null || userId < 0) {
            return false;
        }
        if (refreshToken.isEmpty()) {
            return false;
        }
        String baseRefreshToken = dao.selectRefreshTokenByUserId(userId);
        if (refreshToken.equals(baseRefreshToken)) {
            return true;
        }
        return false;
    }


}
