package com.yash.chat_app.chats.controller;

import com.yash.chat_app.chats.dto.Presence;
import com.yash.chat_app.chats.entity.Message;
import com.yash.chat_app.chats.entity.MessageStatus;
import com.yash.chat_app.chats.repo.MessageRepo;
import com.yash.chat_app.user.User;
import com.yash.chat_app.user.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class WebSocketEventListener {

    @Autowired
    SimpMessagingTemplate messagingTemplate;
    @Autowired
    OnlineCurrUser onlineCurrUser;

    @Autowired
    MessageRepo messageRepo;

    @Autowired
    UserRepo userRepo;

    @EventListener
    public  void handleConnect(SessionConnectEvent sessionConnectEvent){
        StompHeaderAccessor accessor=StompHeaderAccessor.wrap(sessionConnectEvent.getMessage());



        String username = (String) accessor.getSessionAttributes().get("username");

        if (username == null) return;


        onlineCurrUser.add(username);


        new Thread(() -> {
            try {
                Thread.sleep(300); // wait for subscription
            } catch (Exception ignored) {}

            messagingTemplate.convertAndSend(
                    "/topic/initial-presence/" + username,
                    onlineCurrUser.getAll()
            );
        }).start();

        messagingTemplate.convertAndSend(
                "/topic/presence",
                new Presence(username, true)
        );




    }

    @EventListener
    public  void handleDisconnect(SessionDisconnectEvent sessionDisconnectEvent){
        StompHeaderAccessor accessor=StompHeaderAccessor.wrap(sessionDisconnectEvent.getMessage());


        String username = (String) accessor.getSessionAttributes().get("username");
        if(username ==null)throw new RuntimeException("Websocket not authenticated");


        onlineCurrUser.remove(username);

        messagingTemplate.convertAndSend("/topic/presence",new Presence(username,false));



    }


}
