package me.son.chatlabapi.global.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import me.son.chatlabapi.auth.jwt.exception.CustomJwtException;
import me.son.chatlabapi.auth.jwt.exception.JwtErrorCode;
import me.son.chatlabapi.auth.jwt.service.JwtService;
import me.son.chatlabapi.chat.domain.repository.ChatRoomMemberRepository;
import me.son.chatlabapi.chat.exception.ChatErrorCode;
import me.son.chatlabapi.global.exception.BusinessException;
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
@Component
@RequiredArgsConstructor
public class JwtChannelInterceptor implements ChannelInterceptor {
    private static final Pattern ROOM_TOPIC_PATTERN = Pattern.compile("^/topic/chat/rooms/(\\d+)$");

    private final JwtService jwtService;
    private final ChatRoomMemberRepository chatRoomMemberRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null) {
            return message;
        }

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");

            if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
                throw new CustomJwtException(JwtErrorCode.JWT_NOT_FOUND);
            }

            String token = authHeader.substring(7);

            try {
                CustomUserDetails userDetails = jwtService.getCustomUserDetails(token);
                Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails.getId(), null, userDetails.getAuthorities());
                accessor.setUser(authentication);
            } catch (CustomJwtException e) {
                log.error("WebSocket JWT validation failed", e);
                throw e;
            }
        } else if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            Long roomId = extractRoomId(accessor.getDestination());

            if (roomId != null) {
                Long userId = resolveUserId(accessor.getUser());

                if (userId == null || !chatRoomMemberRepository.existsByRoomIdAndUserId(roomId, userId)) {
                    throw new BusinessException(ChatErrorCode.NOT_ROOM_MEMBER);
                }
            }
        }
        return message;
    }

    private Long extractRoomId(String destination) {
        if (destination == null) {
            return null;
        }

        Matcher matcher = ROOM_TOPIC_PATTERN.matcher(destination);
        return matcher.matches() ? Long.valueOf(matcher.group(1)) : null;
    }

    private Long resolveUserId(java.security.Principal user) {
        if (!(user instanceof Authentication authentication)) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        return principal instanceof Long id ? id : null;
    }
}
