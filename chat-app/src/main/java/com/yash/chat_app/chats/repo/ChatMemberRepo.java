package com.yash.chat_app.chats.repo;

import com.yash.chat_app.chats.entity.Chat;
import com.yash.chat_app.chats.entity.ChatMember;
import com.yash.chat_app.chats.entity.Roles;
import com.yash.chat_app.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMemberRepo extends JpaRepository<ChatMember,Long> {
    boolean existsByChatAndUser(Chat chat, User currUser);

    List<ChatMember> findByUser(User user);

    List<ChatMember> findByChat(Chat chat);

    boolean existsByChatAndUserAndRoles(Chat chat, User user, Roles roles);
}
