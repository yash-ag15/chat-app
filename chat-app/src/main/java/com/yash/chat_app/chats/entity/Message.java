package com.yash.chat_app.chats.entity;

import com.yash.chat_app.user.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Data
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    private Chat chat;

    private String content;

    private Instant sentAt = Instant.now();

    @Enumerated(EnumType.STRING)
    private MessageStatus status;


}
