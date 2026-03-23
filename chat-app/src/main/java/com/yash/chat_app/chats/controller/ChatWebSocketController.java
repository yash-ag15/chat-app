package com.yash.chat_app.chats.controller;

import com.yash.chat_app.chats.dto.MessageRequest;
import com.yash.chat_app.chats.dto.MessageResponse;
import com.yash.chat_app.chats.dto.SeenRequest;
import com.yash.chat_app.chats.dto.Typing;
import com.yash.chat_app.chats.entity.ChatMember;
import com.yash.chat_app.chats.entity.Message;
import com.yash.chat_app.chats.entity.MessageStatus;
import com.yash.chat_app.chats.repo.ChatMemberRepo;
import com.yash.chat_app.chats.repo.MessageRepo;
import com.yash.chat_app.chats.service.MessageService;
import com.yash.chat_app.config.WebSocketConfig;
import com.yash.chat_app.user.User;
import com.yash.chat_app.user.repo.UserRepo;
import com.yash.chat_app.user.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;

@Controller
public class ChatWebSocketController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ChatMemberRepo memberRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private MessageRepo messageRepo;

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
        String status = savedMessage.getChat().isGroup() ? null : savedMessage.getStatus().name();
        MessageResponse response = new MessageResponse(
                savedMessage.getId(),
                sender.getUsername(),
                savedMessage.getContent(),
                savedMessage.getSentAt(),
                savedMessage.getChat().getId(),
                status

        );

        messagingTemplate.convertAndSend(
                "/topic/chat/" + savedMessage.getChat().getId(),
                response
        );

        List<ChatMember> members = memberRepo.findByChatWithUser(savedMessage.getChat());

        for (ChatMember chatMember : members) {

            User user = chatMember.getUser();

            Map<String, Object> payload = Map.of(
                    "chatId", savedMessage.getChat().getId(),
                    "lastMessage", savedMessage.getContent(),
                    "lastMessageTime", savedMessage.getSentAt()
            );



            messagingTemplate.convertAndSend(
                    "/topic/chat-list/" + user.getEmail(),
                    payload
            );
        }

    }

    @MessageMapping("/chat.typing")
    public  void handleTyping(Typing payload,SimpMessageHeaderAccessor messageHeaderAccessor){

        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) messageHeaderAccessor.getUser();

        if (authentication == null) {
            Map<String, Object> sessionAttributes = messageHeaderAccessor.getSessionAttributes();

            if (sessionAttributes != null) {
                authentication = (UsernamePasswordAuthenticationToken)
                        sessionAttributes.get(WebSocketConfig.WS_AUTH_ATTR);
            }
        }

        if (authentication == null) {
            throw new RuntimeException("WebSocket not authenticated");
        }

        messagingTemplate.convertAndSend("/topic/typing/"+payload.chatId(),payload);
    }

//    @MessageMapping("/chat.seen")
//    public void handleSeen(SeenRequest seenRequest, SimpMessageHeaderAccessor headerAccessor ){
//
//        UsernamePasswordAuthenticationToken authentication =
//                (UsernamePasswordAuthenticationToken) headerAccessor.getUser();
//
//        if (authentication == null) {
//            Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
//
//            if (sessionAttributes != null) {
//                authentication = (UsernamePasswordAuthenticationToken)
//                        sessionAttributes.get(WebSocketConfig.WS_AUTH_ATTR);
//            }
//        }
//
//        if (authentication == null) {
//            throw new RuntimeException("WebSocket not authenticated");
//        }
//
//        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
//        User user = userPrincipal.getUser();
//
//        List<Message>messages= messageRepo.findByChatAndStatus(seenRequest.chatId(), MessageStatus.SENT ,user.getId());
//
//        for(Message m:messages){
//            m.setStatus(MessageStatus.SEEN);
//        }
//
//        messageRepo.saveAll(messages);
//
//        for(Message m:messages){
//            messagingTemplate.convertAndSend("/topic/message-status/"+m.getChat().getId(), Map.of("messageId",m.getId(),
//                    "status",  MessageStatus.SEEN.name()));
//        }
//
//    }
}
