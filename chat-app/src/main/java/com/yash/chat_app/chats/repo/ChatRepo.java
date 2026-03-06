package com.yash.chat_app.chats.repo;

import com.yash.chat_app.chats.entity.Chat;
import com.yash.chat_app.chats.entity.ChatMember;
import com.yash.chat_app.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ChatRepo extends JpaRepository<Chat,Long> {
    @Query("""
SELECT c
FROM Chat c
JOIN ChatMember cm1 ON cm1.chat = c
JOIN ChatMember cm2 ON cm2.chat = c
WHERE c.isGroup = false
AND cm1.user = :user1
AND cm2.user = :user2
""")
    Optional<Chat> findPrivateChat(User user1, User user2);
}
