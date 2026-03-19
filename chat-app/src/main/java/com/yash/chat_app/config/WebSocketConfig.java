package com.yash.chat_app.config;

import com.yash.chat_app.auth.jwt.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.Map;

@EnableWebSocketMessageBroker
@Configuration
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    public static final String WS_AUTH_ATTR = "wsAuth";

    @Autowired
    private JWTService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("http://localhost:*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {

            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
                StompCommand command = accessor.getCommand();
                Map<String, Object> sessionAttributes = accessor.getSessionAttributes();

                if (command == null) {
                    return message;
                }

                if (StompCommand.CONNECT.equals(command)) {
                    String token = resolveToken(accessor);
                    String email = jwtService.extractEmail(token);
                    UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                    if (!jwtService.isTokenValid(token, userDetails)) {
                        throw new RuntimeException("Invalid websocket token");
                    }

                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );

                    accessor.setUser(auth);

                    if (sessionAttributes != null) {
                        sessionAttributes.put(WS_AUTH_ATTR, auth);
                    }

                } else if (accessor.getUser() == null && sessionAttributes != null) {
                    Object auth = sessionAttributes.get(WS_AUTH_ATTR);
                    if (auth instanceof UsernamePasswordAuthenticationToken authentication) {
                        accessor.setUser(authentication);
                    }
                }

                return message;
            }
        });
    }

    private String resolveToken(StompHeaderAccessor accessor) {
        String token = accessor.getFirstNativeHeader("token");
        if (token == null || token.isBlank()) {
            token = accessor.getFirstNativeHeader("Authorization");
        }
        if (token == null || token.isBlank()) {
            throw new RuntimeException("Missing websocket token");
        }
        if (token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return token;
    }
}