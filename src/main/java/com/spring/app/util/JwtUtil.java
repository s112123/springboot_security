package com.spring.app.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${com.spring.app.jwt.secret-key}")
    private String secretKey;

    // JWT 생성
    public String generateToken(Map<String, Object> claim, int days) {
        // header
        Map<String, Object> headers = new HashMap<>();
        headers.put("typ", "JWT");
        headers.put("alg", "HS256");

        // payload = claim
        Map<String, Object> payloads = new HashMap<>();
        payloads.putAll(claim);

        // expiration
        int time = (60 * 24) * days;

        // JWT 생성
        String jwtToken = Jwts.builder()
                .setHeader(headers)
                .setClaims(payloads)
                .setIssuedAt(Date.from(ZonedDateTime.now().toInstant()))
                .setExpiration(Date.from(ZonedDateTime.now().plusMinutes(time).toInstant()))
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
                .compact();

        return jwtToken;
    }

    // JWT 검증
    public Map<String, Object> validateToken(String jwtToken) {
        Map<String, Object> claim = null;

        claim = Jwts.parser()
                .setSigningKey(secretKey.getBytes())
                .parseClaimsJws(jwtToken)
                .getBody();

        return claim;
    }

}
