package com.xc.soj_demo.dao;

import org.apache.ibatis.annotations.Mapper;
import com.xc.soj_demo.entity.User;

import java.util.Map;

@Mapper
public interface UserDao {
    User getUserByUserName(String username);

    void addToken2User(Map<String, Object> map);

    void setRefreshToken(String userId, String refreshToken);

    String getPasswordByUserName(String username);

    String getRefreshToken(String username)throws Exception;

    String selectRefreshTokenByUserId(Integer userId);
}
