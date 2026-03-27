package com.stu.helloserver.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stu.helloserver.common.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

public class AuthInterceptor implements HandlerInterceptor {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String method = request.getMethod();
        String uri = request.getRequestURI();

        boolean isRegister = "POST".equalsIgnoreCase(method) && "/api/users".equals(uri);
        boolean isLogin = "POST".equalsIgnoreCase(method) && "/api/users/login".equals(uri);

        if (isRegister || isLogin) {
            return true;
        }

        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            writeUnauthorizedResponse(response, method);
            return false;
        }

        return true;
    }

    private void writeUnauthorizedResponse(HttpServletResponse response, String method) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        Result<String> result = Result.error(401, "非法操作：敏感动作 [" + method + "] 需要登录");
        response.getWriter().write(OBJECT_MAPPER.writeValueAsString(result));
    }
}
