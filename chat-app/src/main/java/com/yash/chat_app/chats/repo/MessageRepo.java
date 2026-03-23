package com.yash.chat_app.chats.repo;

import com.yash.chat_app.chats.entity.Message;
import com.yash.chat_app.chats.entity.MessageStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MessageRepo extends JpaRepository<Message,Long> {

    Optional<Message> findTopByChatIdOrderBySentAtDesc(Long id);



    Page<Message> findByChatIdOrderBySentAtDesc(Long chatId, Pageable pageable);

    @Query("""
            SELECT m FROM Message m
            WHERE m.chat.id IN(
            SELECT cm.chat.id FROM ChatMember cm WHERE cm.user.id= :userId)
            AND m.sender.id != :userId
            AND m.status= :status
            AND m.chat.isGroup = false
            """)
    List<Message> findByReceiverAndStatus(Long userId, MessageStatus status);

    @Query("""
            SELECT m FROM Message m
            WHERE m.chat.id= :chatId
            AND m.sender.id!= :userId
            AND m.status= :status
            AND m.chat.isGroup = false
            """)
    List<Message> findByChatAndStatus(Long chatId, MessageStatus status, Long userId);
}
