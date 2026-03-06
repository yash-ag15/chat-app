package com.yash.chat_app.chats.controller;

import com.yash.chat_app.chats.dto.MessageRequest;
import com.yash.chat_app.chats.entity.Chat;
import com.yash.chat_app.chats.entity.Message;
import com.yash.chat_app.chats.service.MessageService;
import com.yash.chat_app.user.User;
import com.yash.chat_app.user.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired
    MessageService messageService;

    @PostMapping
    public Message sendMessage(Authentication authentication,
                               @RequestBody MessageRequest messageRequest) {

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User currUser = userPrincipal.getUser();

        return messageService.sendMessage(currUser, messageRequest);
    }
}