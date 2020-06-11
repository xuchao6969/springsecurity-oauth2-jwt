package com.xc.soj_demo.entity;


import lombok.Data;

@Data
public class User {

    private String userId;
    private String username;
    private String password;
    private String userPermission;
    private String userRole;
    private String token;
    private String refreshToken;

}
