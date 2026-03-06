package com.yash.chat_app.chats.repo;

import com.yash.chat_app.chats.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MessageRepo extends JpaRepository<Message,Long> {

    Optional<Message> findTopByChatIdOrderBySentAtDesc(Long id);



    Page<Message> findByChatIdOrderBySentAtDesc(Long chatId, Pageable pageable);
}
