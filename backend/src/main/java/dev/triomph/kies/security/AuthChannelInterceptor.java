package dev.triomph.kies.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

@Component
public class AuthChannelInterceptor implements ChannelInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(AuthChannelInterceptor.class);

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            List<String> authorization = accessor.getNativeHeader("Authorization");
            logger.debug("Authorization header: {}", authorization);

            String jwt = null;
            if (authorization != null && !authorization.isEmpty()) {
                String headerAuth = authorization.get(0);
                if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
                    jwt = headerAuth.substring(7);
                }
            }

            if (jwt != null) {
                try {
                    if (jwtUtils.validateJwtToken(jwt)) {
                        String userId = jwtUtils.getUserIdFromJwtToken(jwt);
                        UserDetails userDetails;

                        if (userId != null) {
                            userDetails = userDetailsService.loadUserById(Long.parseLong(userId));
                        } else {
                            String username = jwtUtils.getUserNameFromJwtToken(jwt);
                            userDetails = userDetailsService.loadUserByUsername(username);
                            logger.warn("Authenticated WebSocket user by username as ID was not in token: {}", username);
                        }
                        
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        
                        accessor.setUser(authentication);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        logger.debug("User {} authenticated and set for STOMP session and SecurityContextHolder", userDetails.getUsername());
                    }
                } catch (Exception e) {
                    logger.error("Cannot set user authentication from JWT in STOMP CONNECT: {}", e.getMessage());
                }
            } else {
                logger.warn("No JWT token found in STOMP CONNECT headers, client will be anonymous for STOMP messages if not already authenticated.");
            }
        }
        return message;
    }
}