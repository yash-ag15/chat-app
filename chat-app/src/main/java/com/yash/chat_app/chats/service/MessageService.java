package com.yash.chat_app.chats.service;

import com.yash.chat_app.chats.dto.MessageRequest;
import com.yash.chat_app.chats.entity.Chat;
import com.yash.chat_app.chats.entity.Message;
import com.yash.chat_app.chats.entity.MessageStatus;
import com.yash.chat_app.chats.repo.ChatMemberRepo;
import com.yash.chat_app.chats.repo.ChatRepo;
import com.yash.chat_app.chats.repo.MessageRepo;
import com.yash.chat_app.friends.repo.FriendsConnectedRepo;
import com.yash.chat_app.user.User;
import com.yash.chat_app.user.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class MessageService {

    @Autowired
    ChatService chatService;

    @Autowired
    MessageRepo messageRepo;

    @Autowired
    FriendsConnectedRepo fcRepo;

    @Autowired
    UserRepo userRepo;

    @Autowired
    ChatRepo chatRepo;

    @Autowired
    ChatMemberRepo memberRepo;

    public Message sendMessage(User currUser, MessageRequest messageRequest) {





        Optional<Chat> chatOptional = chatRepo.findById(messageRequest.chatId());

        if (chatOptional.isEmpty()) {
            throw new RuntimeException("Chat does not exist");
        }

        Chat chat = chatOptional.get();

        boolean isMember = memberRepo.existsByChatAndUser(chat, currUser);

        if (!isMember) {
            throw new RuntimeException("User not part of this chat");
        }

        Message message = new Message();
        message.setSender(currUser);
        message.setContent(messageRequest.content());
        message.setChat(chat);
        if (!chat.isGroup()) {
            message.setStatus(MessageStatus.SENT);
        } else {
            message.setStatus(null);
        }

        return messageRepo.save(message);
    }
}