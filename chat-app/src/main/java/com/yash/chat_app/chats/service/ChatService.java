package com.yash.chat_app.chats.service;

import com.yash.chat_app.chats.dto.ChatResponse;
import com.yash.chat_app.chats.dto.MessageResponse;
import com.yash.chat_app.chats.entity.Chat;
import com.yash.chat_app.chats.entity.ChatMember;
import com.yash.chat_app.chats.entity.Message;
import com.yash.chat_app.chats.entity.Roles;
import com.yash.chat_app.chats.repo.ChatMemberRepo;
import com.yash.chat_app.chats.repo.ChatRepo;
import com.yash.chat_app.chats.repo.MessageRepo;
import com.yash.chat_app.user.User;
import com.yash.chat_app.user.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ChatService {

    @Autowired
    UserRepo userRepo;

    @Autowired
    ChatRepo chatRepo;

    @Autowired
    ChatMemberRepo memberRepo;

    @Autowired
    MessageRepo messageRepo;
    public Chat getPrivateChat(User currUser, String username) {

        User receiver = userRepo.findByUsername(username);

        if (receiver == null) {
            throw new RuntimeException("User not found");
        }

        Optional<Chat> containChat = chatRepo.findPrivateChat(currUser, receiver);

        if (containChat.isPresent()) {
            return containChat.get();
        }

        Chat chat = new Chat();
        chat.setGroup(false);

        chat = chatRepo.save(chat);

        ChatMember chatMember1 = new ChatMember();
        chatMember1.setChat(chat);
        chatMember1.setUser(currUser);
        chatMember1.setRoles(Roles.MEMBER);

        ChatMember chatMember2 = new ChatMember();
        chatMember2.setChat(chat);
        chatMember2.setUser(receiver);
        chatMember2.setRoles(Roles.MEMBER);

        memberRepo.save(chatMember1);
        memberRepo.save(chatMember2);

        return chat;
    }

    public List<ChatResponse> getAllChat(User user) {

        List<ChatMember> members = memberRepo.findByUser(user);

        List<ChatResponse> chats = new ArrayList<>();

        for (ChatMember m : members) {

            Chat chat = m.getChat();


            Optional<Message> lastMsg =
                    messageRepo.findTopByChatIdOrderBySentAtDesc(chat.getId());

            String lastMessage = null;
            Instant lastMessageTime = null;

            if (lastMsg.isPresent()) {
                lastMessage = lastMsg.get().getContent();
                lastMessageTime = lastMsg.get().getSentAt();
            }

            ChatResponse response = new ChatResponse(
                    chat.getId(),
                    chat.isGroup(),
                    chat.getCreatedAt(),
                    lastMessage,
                    lastMessageTime
            );

            chats.add(response);
        }

        return chats;
    }

    public Page<MessageResponse> getChatMessages(User user, Long chatId,int page,int size) {

        Chat chat = chatRepo.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat not found"));

        boolean isMember = memberRepo.existsByChatAndUser(chat, user);

        if(!isMember){
            throw new RuntimeException("User not part of chat");
        }

        Pageable pageable = PageRequest.of(page, size);

        Page<Message> messages =
                messageRepo.findByChatIdOrderBySentAtDesc(chatId, pageable);


        return messages.map(m ->
                new MessageResponse(
                        m.getId(),
                        m.getSender().getUsername(),
                        m.getContent(),
                        m.getSentAt()
                )
        );
    }
}