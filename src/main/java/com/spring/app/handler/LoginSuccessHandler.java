package com.spring.app.handler;

import com.google.gson.Gson;
import com.spring.app.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@Log4j2
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request, HttpServletResponse response, Authentication authentication
    ) throws IOException, ServletException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // JWT claim 생성
        // authentication.getName() → username 값 반환
        Map<String, Object> claim = Map.of("username", authentication.getName());
        log.info(authentication.getAuthorities());

        // Access Token 생성: 1일
        String accessToken = jwtUtil.generateToken(claim, 1);
        // Refresh Token 생성: 30일
        String refreshToken = jwtUtil.generateToken(claim, 30);

        // 응답으로 Access Token, Refresh Token을 JSON으로 전달
        Gson gson = new Gson();
        Map<String, String> tokens = Map.of("accessToken", accessToken, "refreshToken", refreshToken);
        String jsonData = gson.toJson(tokens);

        // response.getWriter().println(jsonStr);
        PrintWriter out = response.getWriter();
        out.write(jsonData);
        out.flush();
        out.close();
    }

}
