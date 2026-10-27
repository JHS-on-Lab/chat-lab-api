package me.son.chatlabapi.global.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import me.son.chatlabapi.auth.exception.AuthErrorCode;
import me.son.chatlabapi.auth.jwt.exception.CustomJwtException;
import me.son.chatlabapi.auth.jwt.exception.JwtErrorCode;
import me.son.chatlabapi.auth.jwt.service.JwtService;
import me.son.chatlabapi.chat.domain.repository.ChatRoomMemberRepository;
import me.son.chatlabapi.chat.exception.ChatErrorCode;
import me.son.chatlabapi.global.exception.BusinessException;
import me.son.chatlabapi.global.security.CustomUserDetails;
import me.son.chatlabapi.user.domain.repository.UserRepository;

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
    private static final String APP_DESTINATION_PREFIX = "/app/";

    private final JwtService jwtService;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final UserRepository userRepository;

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
                // Access Token 이 아직 유효하더라도 비활성화 / 삭제된 사용자의 신규 연결은 차단
                if (!userRepository.existsByIdAndEnabledTrue(userDetails.getId())) {
                    throw new BusinessException(AuthErrorCode.ACCOUNT_DISABLED);
                }
                Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails.getId(), null, userDetails.getAuthorities());
                accessor.setUser(authentication);
            } catch (CustomJwtException e) {
                log.error("WebSocket JWT validation failed", e);
                throw e;
            }
        } else if (StompCommand.SEND.equals(accessor.getCommand())) {
            // 클라이언트가 브로커(/topic, /queue)로 직접 SEND 하면 서버 검증 없이 구독자에게 전달되어
            // 다른 사용자를 사칭한 메시지 위조가 가능하므로, 애플리케이션 목적지(/app)만 허용한다.
            String destination = accessor.getDestination();
            if (destination == null || !destination.startsWith(APP_DESTINATION_PREFIX)) {
                throw new BusinessException(AuthErrorCode.ACCESS_DENIED);
            }
        } else if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            Long roomId = extractRoomId(accessor.getDestination());

            if (roomId != null) {
                Long userId = resolveUserId(accessor.getUser());

                if (userId == null || !chatRoomMemberRepository.existsByRoom_IdAndUser_Id(roomId, userId)) {
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
