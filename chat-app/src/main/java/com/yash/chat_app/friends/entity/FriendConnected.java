package com.yash.chat_app.friends.entity;

import com.yash.chat_app.user.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Table(
        name = "friendships",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user1_id", "user2_id"})
        }
)
@Data
public class FriendConnected {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user1_id", nullable = false)
    private User user1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user2_id", nullable = false)
    private User user2;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

}
