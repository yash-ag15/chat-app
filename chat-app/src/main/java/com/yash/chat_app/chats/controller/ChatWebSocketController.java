package com.yash.chat_app.chats.controller;

import com.yash.chat_app.chats.dto.MessageRequest;
import com.yash.chat_app.chats.dto.MessageResponse;
import com.yash.chat_app.chats.entity.Message;
import com.yash.chat_app.chats.service.MessageService;
import com.yash.chat_app.config.WebSocketConfig;
import com.yash.chat_app.user.User;
import com.yash.chat_app.user.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
public class ChatWebSocketController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.send")
    public void sendMessage(MessageRequest request,
                            SimpMessageHeaderAccessor headerAccessor) {
        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) headerAccessor.getUser();

        if (authentication == null) {
            Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();

            if (sessionAttributes != null) {
                authentication = (UsernamePasswordAuthenticationToken)
                        sessionAttributes.get(WebSocketConfig.WS_AUTH_ATTR);
            }
        }

        if (authentication == null) {
            throw new RuntimeException("WebSocket not authenticated");
        }


        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User sender = userPrincipal.getUser();

        Message savedMessage = messageService.sendMessage(sender, request);

        MessageResponse response = new MessageResponse(
                savedMessage.getId(),
                sender.getUsername(),
                savedMessage.getContent(),
                savedMessage.getSentAt(),
                savedMessage.getChat().getId()
        );

        messagingTemplate.convertAndSend(
                "/topic/chat/" + savedMessage.getChat().getId(),
                response
        );
    }
}
