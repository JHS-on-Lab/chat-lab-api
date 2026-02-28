package me.son.chatlabapi.global.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import me.son.chatlabapi.auth.jwt.exception.CustomJwtException;
import me.son.chatlabapi.auth.jwt.exception.JwtErrorCode;
import me.son.chatlabapi.auth.jwt.service.JwtService;
import me.son.chatlabapi.global.security.CustomUserDetails;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Log4j2
@Component
@RequiredArgsConstructor
public class JwtChannelInterceptor implements ChannelInterceptor {
    private final JwtService jwtService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null) {
            return message;
        }

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {

            String authHeader = accessor.getFirstNativeHeader("Authorization");
            log.info("WebSocket Authorization Header: {}", authHeader);

            if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
                throw new CustomJwtException(JwtErrorCode.JWT_NOT_FOUND);
            }

            String token = authHeader.substring(7);

            try {
                CustomUserDetails userDetails = jwtService.getCustomUserDetails(token);

                Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                accessor.setUser(authentication);
            } catch (CustomJwtException e) {
                log.error("WebSocket JWT validation failed", e);
                throw e;
            }
        }

        return message;
    }
}
