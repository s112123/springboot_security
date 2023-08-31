package com.spring.app.filter;

import com.spring.app.exception.AccessTokenException;
import com.spring.app.member.service.SecurityUserDetailsService;
import com.spring.app.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@RequiredArgsConstructor
public class TokenCheckFilter extends OncePerRequestFilter {

    // SecurityUserDetailsService를 이용해서 JWT의 username 값으로 사용자 정보를 얻어온다
    private final SecurityUserDetailsService securityUserDetailsService;
    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain
    ) throws ServletException, IOException {
        // 요청주소
        String path = request.getRequestURI();

        // 요청주소가 '/members' 로 시작하지 않는 경우
        if (!path.startsWith("/members")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 요청주소가 '/members' 로 시작하는 경우
        // Access Token에 문제가 있을 때, 자동으로 브라우저에 에러메시지를 상태코드와 함께 전송하도록 처리
        try {
            Map<String, Object> payload = validateAccessToken(request);

            // @PreAuthorize 적용을 위한 설정
            // username으로 UserDetails를 구한다
            String username = (String) payload.get("username");
            UserDetails userDetails = securityUserDetailsService.loadUserByUsername(username);

            // UsernamePasswordAuthenticationToken 객체를 생성한다
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            // UsernamePasswordAuthenticationToken 객체를 스프링 시큐리티에서 사용할 수 있도록 한다
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            filterChain.doFilter(request, response);
        } catch (AccessTokenException accessTokenException) {
            accessTokenException.sendResponseError(response);
        }
    }

    // Access Token 검증
    private Map<String, Object> validateAccessToken(HttpServletRequest request) throws AccessTokenException {
        String requestHeader = request.getHeader("Authorization");

        // JWT 토큰인지 검증 ('Bearer ' = 7자리)
        if (requestHeader == null || requestHeader.length() < 8) {
            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.UNACCEPT);
        }

        // 토큰 타입 검증
        String tokenType = requestHeader.substring(0, 6);
        if (!tokenType.equalsIgnoreCase("Bearer")) {
            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.BADTYPE);
        }

        // 토큰 내용 검증
        String tokenText = requestHeader.substring(7);
        try {
            Map<String, Object> values = jwtUtil.validateToken(tokenText);
            return values;
        } catch (MalformedJwtException malformedJwtException) {
            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.MALFORM);
        } catch (SignatureException signatureException) {
            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.BADSIGN);
        } catch (ExpiredJwtException expiredJwtException) {
            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.EXPIRED);
        }
    }

}
