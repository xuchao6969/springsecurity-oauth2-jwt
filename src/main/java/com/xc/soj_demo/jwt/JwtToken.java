package com.xc.soj_demo.jwt;

import io.jsonwebtoken.ClaimJwtException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;

/**
 * token加解密
 * 
 * 1.对称加解密
 * 2.非对称加解密(RSA)
 * 
 */
@Configuration
public class JwtToken {
	
    /**
     * 加载jwt.jks文件
     */
    private static InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("certificate/jwt.jks");
    private static PrivateKey privateKey = null;
    private static PublicKey publicKey = null;

    static {
        try {
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(inputStream, "xc1234".toCharArray());
            privateKey = (PrivateKey) keyStore.getKey("jwt", "xc1234".toCharArray());
            publicKey = keyStore.getCertificate("jwt").getPublicKey();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 生成jwt token(非对称加密模式 公钥私钥)
     */
    public static String generateTokenRSA(String subject, int expirationSeconds) {
        Date nowDate = new Date();
        Date expireDate = new Date(nowDate.getTime() + expirationSeconds * 1000);
        return Jwts.builder()
                .setClaims(null)
                .setHeaderParam("typ", "JWT")
                .setSubject(subject)
                .setIssuedAt(nowDate)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.RS256, privateKey) 
                .compact();
    }

    /**
     * 解析jwt token(非对称加密模式 公钥私钥)
     */
    public static Claims parseTokenRSA(String token) {
    	if (StringUtils.isEmpty(token)) {
            return null;
        }
  
        try {
            return Jwts.parser()
                    .setSigningKey(publicKey)
                    .parseClaimsJws(token)
                    .getBody();
        }catch (Exception e){
            System.out.println("try-catch：validate is token error ");
            return null;
        }
    }
    
    /**
     * token是否过期
     * @return  true：过期
     */
    public static boolean isTokenExpired(Date expiration) {
    	boolean before = expiration.before(new Date());
        return before;
    }
    
    /**
     * 生成jwt token(对称加密模式)
     */
    public static String generateToken(String subject, int expirationSeconds,
    		String signingKey) {
        Date nowDate = new Date();
        Date expireDate = new Date(nowDate.getTime() + expirationSeconds * 1000);
        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject(subject)
                .setIssuedAt(nowDate)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS512, signingKey)
                .compact();
    }
    
    /**
     * 解析jwt token(对称加密模式)
     */
	public static String parseToken(String token,String signingKey) {
    	if (StringUtils.isEmpty(token)) {
            return null;
        }
		token = StringUtils.substringAfter(token, "bearer");//定义token令牌的类型为bearer
		Claims claims;
		try {
			claims = Jwts.parser().setSigningKey(signingKey.getBytes("UTF-8")).parseClaimsJws(token).getBody();
		} catch (ClaimJwtException e) {
		    //源码DefaultJwtParser.Class中的处理过程是 从token中取出载荷payload部分，解析出claim(claim中存在用户信息)，然后在解析是否过期，最后才抛出的异常
            //所以是可以从 ClaimJwtException e中取出需要的部分 并且源码ClaimJwtException.Class类中有header和claim两个私有属性并提供了get方法
            claims = e.getClaims();
		} catch (UnsupportedEncodingException e) {
            return null;
        }
        String localUser = (String) claims.get("userinfo");// 拿到当前用户
		return localUser;
	}
	
	
}