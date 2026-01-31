package com.yash.chat_app.friends.entity;


import com.yash.chat_app.user.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
@Data
@Entity
@Table(
        name = "friend_requests",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"sender_id", "receiver_id"})
        }
)
public class FriendRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FriendRequestStatus status;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();


}
