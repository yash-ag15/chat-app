package com.yash.chat_app.chats.controller;

import com.yash.chat_app.chats.dto.Presence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.ArrayList;

@Component
public class WebSocketEventListener {

    @Autowired
    SimpMessagingTemplate messagingTemplate;

    @Autowired
    OnlineCurrUser onlineCurrUser;

    @EventListener
    public void handleConnect(SessionConnectEvent sessionConnectEvent) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(sessionConnectEvent.getMessage());

        String username = (String) accessor.getSessionAttributes().get("username");
        if (username == null) return;

        onlineCurrUser.add(username);

        messagingTemplate.convertAndSend(
                "/topic/presence",
                new Presence(username, true)
        );
    }

    @EventListener
    public void handleSubscribe(SessionSubscribeEvent sessionSubscribeEvent) {
        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.wrap(sessionSubscribeEvent.getMessage());

        String destination = accessor.getDestination();
        if (destination == null || !destination.startsWith("/topic/initial-presence/")) return;

        String username = destination.substring("/topic/initial-presence/".length());
        if (username.isBlank()) return;

        messagingTemplate.convertAndSend(
                destination,
                new ArrayList<>(onlineCurrUser.getAll())
        );
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent sessionDisconnectEvent) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(sessionDisconnectEvent.getMessage());

        String username = (String) accessor.getSessionAttributes().get("username");
        if (username == null) return;

        onlineCurrUser.remove(username);

        messagingTemplate.convertAndSend("/topic/presence", new Presence(username, false));
    }
}
