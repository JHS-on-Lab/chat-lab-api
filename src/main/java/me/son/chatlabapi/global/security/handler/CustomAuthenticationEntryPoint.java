package me.son.chatlabapi.global.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.extern.log4j.Log4j2;

import me.son.chatlabapi.auth.exception.AuthErrorCode;
import me.son.chatlabapi.auth.jwt.exception.CustomJwtException;
import me.son.chatlabapi.auth.jwt.exception.JwtErrorCode;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

import static me.son.chatlabapi.global.util.HttpRequestUtil.getClientIp;

@Log4j2
@Component
public class CustomAuthenticationEntryPoint extends AbstractSecurityErrorHandler implements AuthenticationEntryPoint {

    public CustomAuthenticationEntryPoint(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        Object jwtExObj = request.getAttribute("JWT_EXCEPTION");

        String method = request.getMethod();
        String uri = request.getRequestURI();
        String ip = getClientIp(request);
        String ua = request.getHeader("User-Agent");

        if (jwtExObj instanceof CustomJwtException jwtException) {
            JwtErrorCode errorCode = jwtException.getErrorCode();
            log.warn("JWT Error - method={}, uri={}, ip={}, ua={}, message={}", method, uri, ip, ua, errorCode.getMessage());
            writeErrorResponse(response, errorCode);
        } else {
            log.warn("Auth Error - method={}, uri={}, ip={}, ua={}, message={}", method, uri, ip, ua, authException.getMessage());
            AuthErrorCode errorCode = AuthErrorCode.UNAUTHORIZED;
            writeErrorResponse(response, errorCode);
        }
    }
}