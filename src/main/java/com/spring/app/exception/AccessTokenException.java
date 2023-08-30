package com.spring.app.exception;

import com.google.gson.Gson;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

public class AccessTokenException extends RuntimeException {

    TOKEN_ERROR token_error;

    // 발생하는 예외의 종류를 미리 enum으로 구분해두고, 나중에 에러 메시지를 전송할 수 있는 구조로 작성한다
    public enum TOKEN_ERROR {

        UNACCEPT(401, "Token is null or too short"),
        BADTYPE(401, "Token type is Bearer"),
        MALFORM(403, "Malformed Token"),
        BADSIGN(403, "BadSignatured Token"),
        EXPIRED(403, "Expired Token");

        private int status;
        private String msg;

        TOKEN_ERROR(int status, String msg) {
            this.status = status;
            this.msg = msg;
        }

        public int getStatus() {
            return status;
        }

        public String getMsg() {
            return msg;
        }

    }

    public AccessTokenException(TOKEN_ERROR token_error) {
        super(token_error.name());
        this.token_error = token_error;
    }

    // 응답 상태 전송
    public void sendResponseError(HttpServletResponse response) {
        response.setStatus(token_error.getStatus());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Gson gson = new Gson();
        String responseText = gson.toJson(Map.of("msg", token_error.getMsg(), "time", new Date()));

        try {
            response.getWriter().println(responseText);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
