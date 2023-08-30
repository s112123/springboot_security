package com.spring.app.util;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

@SpringBootTest
@Log4j2
public class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    @DisplayName(value = "JWT 생성")
    void generateToken() {
        Map<String, Object> loginInfo = Map.of("username", "a1234", "password", "1234");
        String jwtToken = jwtUtil.generateToken(loginInfo, 2);
        log.info(jwtToken);
    }

    @Test
    @DisplayName(value = "JWT 검증")
    void validateToken() {
        String jwtToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJwYXNzd29yZCI6IjEyMzQiLCJleHAiOjE5NDIyMTQ4NDQsImlhdCI6MTY5MzM4Mjg0NCwidXNlcm5hbWUiOiJhMTIzNCJ9.8MJSR753Td45pRURvTG416RoUfKdRQFYpQHAYK-jdgQ";
        Map<String, Object> claim = jwtUtil.validateToken(jwtToken);
        log.info(claim.get("username"));
        log.info(claim.get("password"));
    }

}
