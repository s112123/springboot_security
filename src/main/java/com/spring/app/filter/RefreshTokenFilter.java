package com.spring.app.filter;

import com.google.gson.Gson;
import com.spring.app.exception.RefreshTokenException;
import com.spring.app.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@RequiredArgsConstructor
@Log4j2
public class RefreshTokenFilter extends OncePerRequestFilter {

    private final String refreshPath;
    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain
    ) throws ServletException, IOException {
        String path = request.getRequestURI();

        // 요청경로가 '/refreshToken'이 아닌 경우
        if (!path.equals(refreshPath)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 요청경로가 '/refreshToken'인 경우
        // 전송된 JSON에서 accessToken과 refreshToken을 얻어온다
        Map<String, String> tokens = parseRequestJSON(request);
        String accessToken = tokens.get("accessToken");
        String refreshToken = tokens.get("refreshToken");

        // Access Token 검증
        try {
            checkAccessToken(accessToken);
        } catch (RefreshTokenException refreshTokenException) {
            refreshTokenException.sendResponseError(response);
            return;
        }

        // Refresh Token 검증
        Map<String, Object> refreshClaims = null;
        try {
            refreshClaims = checkRefreshToken(refreshToken);

            // Refresh Token의 유효시간이 얼마남지 않은 경우
            Integer exp = (Integer) refreshClaims.get("exp");
            Date expTime = new Date(Instant.ofEpochMilli(exp).toEpochMilli() * 1000);
            Date current = new Date(System.currentTimeMillis());

            // 만료시간과 현재 시간의 간격 계산 (만일 3일 미만인 경우에는 Refresh Token도 다시 생성)
            long gapTime = (expTime.getTime() - current.getTime());

            // Access Token 새로 생성
            String username = (String) refreshClaims.get("username");
            String accessTokenValue = jwtUtil.generateToken(Map.of("username", username), 1);
            String refreshTokenValue = tokens.get("refreshToken");

            // Refresh Token이 3일도 안남았다면
            if (gapTime < (1000 * 60 * 60 * 24 * 3)) {
                refreshTokenValue = jwtUtil.generateToken(Map.of("username", username), 30);
            }

            // 새로운 토큰 전송
            sendTokens(accessTokenValue, refreshTokenValue, response);
        } catch (RefreshTokenException refreshTokenException) {
            refreshTokenException.sendResponseError(response);
        }
    }

    // JSON 문자열을 Map 객체로 변환
    private Map<String, String> parseRequestJSON(HttpServletRequest request) {
        try (Reader reader = new InputStreamReader(request.getInputStream())) {
            Gson gson = new Gson();
            return gson.fromJson(reader, Map.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // Access Token 검증
    private void checkAccessToken(String accessToken) throws RefreshTokenException {
        try {
            jwtUtil.validateToken(accessToken);
        } catch (ExpiredJwtException expiredJwtException) {
            // Access Token은 만료기간이 지난 상황은 자연스러운 것이므로 로그만 출력해주도록 한다
            log.info("Access Token has expired");
        } catch (Exception exception) {
            throw new RefreshTokenException(RefreshTokenException.ErrorCase.NO_ACCESS);
        }
    }

    // Refresh Token 검증
    private Map<String, Object> checkRefreshToken(String refreshToken) throws RefreshTokenException {
        try {
            Map<String, Object> values = jwtUtil.validateToken(refreshToken);
            return values;
        } catch (ExpiredJwtException expiredJwtException) {
            throw new RefreshTokenException(RefreshTokenException.ErrorCase.OLD_REFRESH);
        } catch (MalformedJwtException malformedJwtException) {
            throw new RefreshTokenException(RefreshTokenException.ErrorCase.NO_REFRESH);
        } catch (Exception exception) {
            new RefreshTokenException(RefreshTokenException.ErrorCase.NO_REFRESH);
        }

        return null;
    }

    // 토큰 전송
    private void sendTokens(String accessTokenValue, String refreshTokenValue, HttpServletResponse response) {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Gson gson = new Gson();
        String jsonData = gson.toJson(Map.of("accessToken", accessTokenValue, "refreshToken", refreshTokenValue));
        try {
            response.getWriter().println(jsonData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
