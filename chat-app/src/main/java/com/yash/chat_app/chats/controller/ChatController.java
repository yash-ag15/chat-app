package com.yash.chat_app.chats.controller;

import com.yash.chat_app.chats.dto.ChatResponse;
import com.yash.chat_app.chats.dto.MessageResponse;
import com.yash.chat_app.chats.entity.Chat;
import com.yash.chat_app.chats.service.ChatService;
import com.yash.chat_app.user.User;
import com.yash.chat_app.user.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ChatController {
    @Autowired
    ChatService chatService;
@GetMapping("/chat/private/{username}")
    public Chat getPrivateChat(Authentication authentication, @PathVariable String username){
    UserPrincipal userPrincipal=(UserPrincipal)authentication.getPrincipal();
    User currUser=userPrincipal.getUser();
    return chatService.getPrivateChat(currUser,username);
}
@GetMapping("/chats")
public List<ChatResponse> getAllChat(Authentication authentication ){
    UserPrincipal userPrincipal=(UserPrincipal) authentication.getPrincipal();
    User user= userPrincipal.getUser();
    return chatService.getAllChat(user);
}

@GetMapping("/messages/{chatId}")
    public Page<MessageResponse> getChatMessages(Authentication authentication, @PathVariable Long chatId, @RequestParam int page,
                                                 @RequestParam int size){
    UserPrincipal userPrincipal=(UserPrincipal) authentication.getPrincipal();
    User user= userPrincipal.getUser();
   return chatService.getChatMessages(user,chatId,page,size);

}
}
