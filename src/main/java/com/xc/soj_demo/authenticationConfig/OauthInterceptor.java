package com.xc.soj_demo.authenticationConfig;

import com.xc.soj_demo.constant.CodeConstant;
import com.xc.soj_demo.dao.UserDao;
import com.xc.soj_demo.entity.User;
import com.xc.soj_demo.jwt.JwtToken;
import com.xc.soj_demo.util.JsonUtil;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * OAuth2异常拦截类
 *
 * 1.对oauth错误异常进行拦截，这里主要针对令牌过期进行处理
 * 2.新的令牌与刷新令牌的存储
 * 3.载入用户信息到spring Security的ContextHolder中，保证后续url转发
 * 4.刷新令牌过期后的返回状态
 *
 */
public class OauthInterceptor extends OAuth2AuthenticationEntryPoint {

    @Value("${server.port}")
    private String port;

    @Autowired
    private TokenConfig tokenConfig;

    /**
     * 在启动类中注入了restTemplate Bean
     */
    @Autowired
    RestTemplate restTemplate;

    @Resource
    private UserDao dao;

    @Autowired
    private AuthenticationManager authenticationManager;

    private WebResponseExceptionTranslator exceptionTranslator = new DefaultWebResponseExceptionTranslator();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        try {
            ResponseEntity<?> result = exceptionTranslator.translate(authException);
            JSONObject objBody = JSONObject.fromObject(result.getBody());
            String message = objBody.getString("message");

            //判断是否为"访问令牌过期",如果不是则以默认的方法继续处理其他异常
            if (message.contains("Access token expired")) {

                //根据访问令牌，解析出当前令牌用户的用户名称，密码等信息
                String localUser = JwtToken.parseToken(request.getHeader("Authorization"), tokenConfig.getSigningKey());
                @SuppressWarnings("unchecked")
                Map<String, Object> userMap = (Map<String, Object>) JsonUtil.json2Map(localUser);
                String username = (String)userMap.get("username");

                //根据用户名称，从数据库获取用户的刷新令牌
                String refresh_token = dao.getRefreshToken(username);

                //获取当前用户信息
                User userObj  = dao.getUserByUserName(username);
                Map<String, Object> map = new HashMap<>();
                map.put("code", 1);//用户存在 密码正确
                map.put("userId", userObj.getUserId());
                map.put("username", userObj.getUsername());
                map.put("password", userObj.getPassword());
                List<String> listPermission = new ArrayList<>();
                listPermission.add("user::add");
                listPermission.add("user::list");
                listPermission.add("user::update");
                listPermission.add("user::delete");

                List<String> listRole = new ArrayList<>();
//                listRole.add("sys_admin");
//                listRole.add("admin");
                listRole.add(userObj.getUserRole());

                map.put("authOrity", listRole);
                map.put("userPermission", listPermission);

                //获取OAuth2框架的配置信息，用于访问刷新令牌接口
                Map<String, String> tokenMap = tokenConfig.getConfig();
                Map<String,String> mapParam = new HashMap<>();
                mapParam.put("username", userObj.getUsername());
                mapParam.put("password", userObj.getPassword());
                mapParam.put("client_id", tokenMap.get("clientId"));
                mapParam.put("client_secret", tokenMap.get("secret"));
                mapParam.put("grant_type", "refresh_token");//这里没有写错 采用刷新令牌的方式
                mapParam.put("refresh_token", refresh_token);
                try {

                    @SuppressWarnings("unchecked")
                    Map<String, String> mapResult = restTemplate
                            .getForObject(
                                    "http://localhost:"+port+"/oauth/token?username={username}&password={password}&client_id={client_id}&client_secret={client_secret}&grant_type={grant_type}&refresh_token={refresh_token}",
                                    Map.class, mapParam);
                    // 如果刷新成功 跳转到原来需要访问的页面
                    //写入用户信息到公共变量中，写入信息到SecurityContext中
                    CodeConstant.USER_MAP = map;
                    List<GrantedAuthority> grantedAuthorityList = new ArrayList<>();
                    for (String role : listRole) {
                        grantedAuthorityList.add(new SimpleGrantedAuthority(
                                role));
                    }
                    UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
                            userObj.getUsername(), userObj.getPassword(), grantedAuthorityList);
                    Authentication authentications = authenticationManager
                            .authenticate(authRequest);
                    SecurityContextHolder.getContext().setAuthentication(
                            authentications);

                    response.setHeader("access_token",
                            mapResult.get("access_token"));
//					response.setHeader("refresh_token",
//							mapResult.get("refresh_token"));

                    //把新获取到的refresh_token存到数据库
                    dao.setRefreshToken(userObj.getUserId(),mapResult.get("access_token"));
                    response.setHeader("isRefreshToken", "yes");
                    request.getRequestDispatcher(request.getRequestURI())
                            .forward(request, response);
                } catch (Exception e) {
//                    e.printStackTrace();
                    // 获取刷新令牌失败时（刷新令牌过期时），返回指定格式的错误信息
                    response.setHeader("Content-Type", "application/json;charset=utf-8");
                    response.getWriter().print("{\"code\":411,\"message\":\"刷新令牌以过期，需要重新登录.\"}");
                    response.getWriter().flush();
                }
            }else{
                super.commence(request,response,authException);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
